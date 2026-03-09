package com.ibkr.client;

import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Client wrapper responsible for managing the connection to TWS API.
 *
 * <p>This class encapsulates the setup and lifecycle of the underlying {@link EClientSocket},
 * including initialization of the {@link EReader} message processing loop required by the API.
 */
@Slf4j
@RequiredArgsConstructor
public class IBClient {

  private final EJavaSignal signal;
  private final EClientSocket client;

  /**
   * Establishes a connection to TWS API and starts the API message processing loop.
   */
  public void connect() {
    client.eConnect("127.0.0.1", 7497, 0);
    EReader reader = new EReader(client, signal);
    reader.start();

    new Thread(() -> {
      while (client.isConnected()) {
        signal.waitForSignal();
        try {
          reader.processMsgs();
        } catch (Exception e) {
          log.error("Error connecting to TWS API: {}", e.getMessage(), e);
        }
      }
    }).start();
  }

  /**
   * Returns an instance of {@link EClientSocket} used to send API requests.
   *
   * @return {@link EClientSocket} object
   */
  public EClientSocket client() {
    return client;
  }
}
