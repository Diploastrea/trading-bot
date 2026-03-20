package com.ibkr.client;

import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Client wrapper responsible for managing the connection to TWS API.
 *
 * <p>This class encapsulates the setup of the underlying {@link EClientSocket}, including
 * initialisation and managing the lifecycle of the {@link EReader} message processing loop required
 * by the API.
 */
@Slf4j
public class IBClient {

  private static final AtomicBoolean readerThreadRunning = new AtomicBoolean(false);
  private final EJavaSignal signal;
  private final EClientSocket client;
  private final AtomicBoolean readerThreadRunning = new AtomicBoolean(false);
  private volatile CountDownLatch latch = new CountDownLatch(1);

  @Getter
  private int nextValidId;

  public IBClient() {
    signal = new EJavaSignal();
    client = new EClientSocket(new EWrapperImpl(this), signal);
  }

  /**
   * Returns an instance of {@link EClientSocket} used to send TWS API requests.
   *
   * @return {@link EClientSocket} object
   */
  public EClientSocket client() {
    return client;
  }

  /**
   * Sets the next valid request ID, which ensures that each request is unique as required by TWS
   * API.
   *
   * @param nextValidId next valid request ID
   */
  protected void setNextValidId(int nextValidId) {
    this.nextValidId = nextValidId;
  }

  /**
   * This method is invoked to signal TWS API is ready to process requests.
   */
  protected void connectionReady() {
    latch.countDown();
  }

  /**
   * Establishes connection to TWS API and starts the API message processing loop.
   *
   * <p>To ensure the initial connection is fully initialised before the client sends requests, a
   * simple mechanism with {@link CountDownLatch} is used to block the current thread until the
   * handshake is complete and reader thread signals TWS API is ready. If the connection times out,
   * connection retry will be attempted.
   *
   * @throws InterruptedException if the current thread is interrupted while waiting
   */
  @SuppressWarnings("BusyWait")
  public void connect() {
    while (true) {
      log.info("Connecting to TWS API...");
      client.eDisconnect();
      resetLatch();

      client.eConnect(host, port, clientId);
      startReaderThread();
      try {
        boolean connectionComplete = latch.await(10, TimeUnit.SECONDS);
        if (connectionComplete) {
          log.info("TWS API connection fully initialised.");
          return;
        }

        log.error("TWS API connection handshake timed out, attempting to reconnect...");
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        log.error(e.getMessage(), e);
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Starts a reader thread for {@link EReader} to read from the socket and add messages to a
   * queue.
   *
   * <p>Once a message is placed in queue, notification flag is triggered to let the main thread
   * know there is a message waiting to be processed.
   */
  private void startReaderThread() {
    if (!readerThreadRunning.compareAndSet(false, true)) {
      return;
    }

    EReader reader = new EReader(client, signal);
    reader.start();
    readerThreadRunning.set(true);
    Thread.ofVirtual().name("reader").start(() -> {
      try {
        while (client.isConnected()) {
          signal.waitForSignal();
          reader.processMsgs();
        }
      } catch (IOException e) {
        log.error("Unexpected error while trying to read messages from the socket: {}",
            e.getMessage(), e);
      } finally {
        readerThreadRunning.set(false);
      }
    });
  }

  /**
   * Returns an instance of {@link EClientSocket} used to send TWS API requests.
   *
   * @return {@link EClientSocket} object
   */
  public EClientSocket client() {
    return client;
  }

  /**
   * This method is invoked to signal TWS API is ready to process requests.
   */
  public void connectionReady() {
    latch.countDown();
  }

  /**
   * Resets the {@link CountDownLatch} for a new TWS API connection attempt.
   */
  private void resetLatch() {
    latch = new CountDownLatch(1);
  }
}
