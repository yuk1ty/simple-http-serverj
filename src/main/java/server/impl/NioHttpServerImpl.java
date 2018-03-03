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

import base.data.Request;
import base.data.Response;
import base.exception.NioHttpServerException;
import fj.data.Either;
import fj.data.Option;
import handler.request.RequestHandler;
import handler.response.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.NioHttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NioHttpServerImpl implements NioHttpServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(NioHttpServerImpl.class);

  private final InetSocketAddress inetSocketAddress;

  private final Selector selector;

  private final RequestHandler requestHandler;

  private final ResponseHandler responseHandler;

  private final Map<SocketChannel, Request> pendingRequests = new ConcurrentHashMap<>();

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

  private void start() throws NioHttpServerException {
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

  private void doAccept(ServerSocketChannel serverSocketChannel) throws NioHttpServerException {
    try {
      SocketChannel socketChannel = serverSocketChannel.accept();
      LOGGER.info("Connected: %s", socketChannel.socket().getRemoteSocketAddress().toString());
      socketChannel.configureBlocking(false);
      socketChannel.register(selector, SelectionKey.OP_READ);
    } catch (IOException err) {
      throw new NioHttpServerException(err);
    }
  }

  private void doRead(SocketChannel socketChannel) throws NioHttpServerException {
    try {
      ByteBuffer buf = ByteBuffer.allocate(1024);
      socketChannel.read(buf);
      buf.flip();

      Option<Request> maybeRequest = requestHandler.apply(buf);
      buf.clear();

      Option<Either<Exception, Response>> maybeResponse =
          maybeRequest.map(
              request -> {
                try {
                  Response response = responseHandler.apply(request);
                  socketChannel.write(response.toByteBuf());
                  return Either.right(response);
                } catch (NioHttpServerException | IOException err) {
                  return Either.left(err);
                }
              });

      if (maybeResponse.isSome() && maybeResponse.some().isLeft()) {
        throw new NioHttpServerException(maybeResponse.some().left().value());
      }
    } catch (IOException e) {
      throw new NioHttpServerException(e);
    } finally {
      LOGGER.info("Disconnected: %s", socketChannel.socket().getRemoteSocketAddress().toString());
      try {
        socketChannel.close();
      } catch (IOException e) {
        throw new NioHttpServerException(e);
      }
    }
  }

  public void destroy(ServerSocketChannel serverSocketChannel) throws NioHttpServerException {
    try {
      if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
        selector.close();
        serverSocketChannel.close();
      }
    } catch (IOException err) {
      throw new NioHttpServerException(err);
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

      start();
    } catch (NioHttpServerException err) {
      LOGGER.error(err.getMessage());
    } catch (IOException err) {
      LOGGER.error(err.getMessage());
      err.printStackTrace();
    } finally {
      try {
        destroy(serverSocketChannel);
      } catch (NioHttpServerException err) {
        LOGGER.error(err.getMessage());
      }
    }
  }
}
