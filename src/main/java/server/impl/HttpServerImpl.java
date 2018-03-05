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
import server.HttpServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class HttpServerImpl implements HttpServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerImpl.class);

  private final int port;

  private final RequestHandler requestHandler;

  private final ResponseHandler responseHandler;

  public HttpServerImpl(int port, RequestHandler requestHandler, ResponseHandler responseHandler) {
    this.port = port;
    this.requestHandler = requestHandler;
    this.responseHandler = responseHandler;
  }

  private void start(ServerSocket serverSocket) {
    while (true) {

      try (Socket s = serverSocket.accept();
          InputStream in = s.getInputStream();
          OutputStream out = s.getOutputStream()) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String block = br.readLine();

        Option<Request> maybeRequest = requestHandler.apply(ByteBuffer.wrap(block.getBytes()));
        Option<Either<Exception, Response>> maybeResponse =
            maybeRequest.map(
                request -> {
                  try {
                    Response response = responseHandler.apply(request);
                    out.write(response.toString().getBytes());
                    return Either.right(response);
                  } catch (NioHttpServerException | IOException err) {
                    return Either.left(err);
                  }
                });

        if (maybeResponse.isSome() && maybeResponse.some().isLeft()) {
          throw new NioHttpServerException(maybeResponse.some().left().value());
        }
      } catch (IOException e) {
        LOGGER.error(e.getMessage());
        e.printStackTrace();
      } catch (NioHttpServerException e) {
        LOGGER.error(e.getMessage());
      }
    }
  }

  @Override
  public void run() {
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(port);
      start(serverSocket);
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    } finally {
      try {
        serverSocket.close();
      } catch (IOException e) {
        LOGGER.error(e.getMessage());
        e.printStackTrace();
      }
    }
  }
}
