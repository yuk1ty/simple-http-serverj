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
import handler.HttpServerHandler;
import handler.request.RequestHandler;
import handler.response.ResponseHandler;
import server.NioHttpServer;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Map;

public class NioHttpServerImpl implements NioHttpServer {

  private final Selector selector;

  private final ServerSocketChannel serverSocketChannel;

  private final Map<Class<?>, HttpServerHandler> handlers = new HashMap<>();

  public NioHttpServerImpl(
      InetSocketAddress inetSocketAddress, Map<Class<?>, HttpServerHandler> handlers)
      throws Exception {
    this.selector = Selector.open();
    this.serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.socket().bind(inetSocketAddress);
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    this.handlers.putAll(handlers);
  }

  @Override
  public void start() throws NioHttpServerException {}

  /** {@inheritDoc} */
  @Override
  public void run() {
    try {
      start();
    } catch (NioHttpServerException e) {
      e.printStackTrace();
    }
  }
}
