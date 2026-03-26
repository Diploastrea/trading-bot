package com.ibkr.client;

import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import com.ibkr.events.MarketDataSubscriptionEvent;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


/**
 * Client wrapper responsible for managing the connection to TWS API.
 *
 * <p>This class encapsulates the setup of the underlying {@link EClientSocket}, including
 * initialisation and managing the lifecycle of the {@link EReader} message processing loop required
 * by the API.
 *
 * <p>This class also stores {@code nextRequestId} given by TWS API, which is used to ensure each
 * request to the API is unique.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IBClient {

  private static final AtomicInteger nextRequestId = new AtomicInteger();
  private static final AtomicBoolean isReconnecting = new AtomicBoolean(false);
  private static final AtomicBoolean readerThreadRunning = new AtomicBoolean(false);
  private final EJavaSignal signal;
  private final EClientSocket client;
  private final ExecutorService executor;
  private final ApplicationEventPublisher publisher;
  private volatile CountDownLatch latch = new CountDownLatch(1);

  @Value("${ibkr.host}")
  private String host;

  @Value("${ibkr.port}")
  private int port;

  @Value("${ibkr.client-id}")
  private int clientId;

  /**
   * Returns and increments the next valid request ID.
   *
   * @return next request ID
   */
  public static int getNextRequestId() {
    return nextRequestId.getAndIncrement();
  }

  /**
   * Sets the next valid request ID returned by TWS API.
   *
   * @param nextRequestId next request ID
   */
  public void setNextRequestId(int nextRequestId) {
    IBClient.nextRequestId.set(nextRequestId);
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
   * Starts the connection process utilising virtual thread.
   */
  public void connect() {
    executor.execute(this::connectionLoop);
  }

  /**
   * Establishes connection to TWS API and starts the API message processing loop.
   *
   * <p>To ensure the initial connection is fully initialised before the client sends requests, a
   * simple mechanism with {@link CountDownLatch} is used to block the current thread until the
   * handshake is complete and reader thread signals TWS API is ready. Any requests sent prior to
   * handshake completion may be dropped by TWS API. If the connection times out, connection retry
   * will be attempted.
   */
  @SuppressWarnings("BusyWait")
  public void connectionLoop() {
    if (!isReconnecting.compareAndSet(false, true)) {
      return;
    }

    try {
      while (true) {
        log.info("Connecting to TWS API...");
        resetConnection();
        client.eConnect(host, port, clientId);
        startReaderThread();

        boolean connectionComplete = latch.await(10, TimeUnit.SECONDS);
        if (connectionComplete) {
          log.info("TWS API connection fully initialised.");
          publisher.publishEvent(new MarketDataSubscriptionEvent());
          return;
        }

        log.error("TWS API connection handshake timed out, attempting to reconnect...");
        Thread.sleep(15000);
      }
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
    } finally {
      isReconnecting.set(false);
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
    executor.submit(() -> {
      try {
        while (readerThreadRunning.get() && client.isConnected()) {
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
   * Resets the connection state for a new connection attempt.
   */
  private void resetConnection() {
    client.eDisconnect();
    signal.issueSignal();
    latch = new CountDownLatch(1);
  }
}
