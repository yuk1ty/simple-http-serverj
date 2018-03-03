package server.impl;

/*
 * Copyright 2017 Yuki Toyoda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import base.exception.NioHttpServerException;
import handler.request.RequestHandler;
import handler.response.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.NioHttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioHttpServerImpl implements NioHttpServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(NioHttpServerImpl.class);

  private final InetSocketAddress inetSocketAddress;

  private final Selector selector;

  private final RequestHandler requestHandler;

  private final ResponseHandler responseHandler;

  public NioHttpServerImpl(
      InetSocketAddress inetSocketAddress,
      RequestHandler requestHandler,
      ResponseHandler responseHandler)
      throws Exception {
    this.selector = Selector.open();
    this.inetSocketAddress = inetSocketAddress;
    this.requestHandler = requestHandler;
    this.responseHandler = responseHandler;
  }

  private void start(ServerSocketChannel serverSocketChannel) throws NioHttpServerException {
    try {
      while (selector.select() > 0) {
        for (Iterator iter = selector.selectedKeys().iterator(); iter.hasNext(); ) {
          SelectionKey key = (SelectionKey) iter.next();
          iter.remove();

          if (key.isAcceptable()) {
            doAccept((ServerSocketChannel) key.channel());
          } else if (key.isReadable()) {
            doRead((SocketChannel) key.channel());
          }
        }
      }
    } catch (IOException err) {
      throw new NioHttpServerException(err);
    }
  }

  private void doAccept(ServerSocketChannel serverSocketChannel) throws IOException {
    SocketChannel socketChannel = serverSocketChannel.accept();
    LOGGER.info("Connected: %s", socketChannel.socket().getRemoteSocketAddress().toString());
    socketChannel.configureBlocking(false);
    socketChannel.register(selector, SelectionKey.OP_READ);
  }

  private void doRead(SocketChannel socketChannel) throws IOException {
    try {

    } finally {
      LOGGER.info("Disconnected: %s", socketChannel.socket().getRemoteSocketAddress().toString());
      socketChannel.close();
    }
  }

  public void destroy(ServerSocketChannel serverSocketChannel) throws IOException {
    if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
      selector.close();
      serverSocketChannel.close();
    }
  }

  /** {@inheritDoc} */
  @Override
  public void run() {
    ServerSocketChannel serverSocketChannel = null;
    try {
      serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.socket().bind(inetSocketAddress);
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

      start(serverSocketChannel);
    } catch (NioHttpServerException | IOException err) {
      LOGGER.error(err.getCause().getMessage());
    } finally {
      try {
        destroy(serverSocketChannel);
      } catch (IOException err) {
        LOGGER.error(err.getCause().getMessage());
      }
    }
  }
}
