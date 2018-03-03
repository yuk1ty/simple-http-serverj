package server.context;

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

import handler.request.RequestHandler;
import handler.response.ResponseHandler;
import server.NioHttpServer;
import server.impl.NioHttpServerImpl;

import java.net.InetSocketAddress;

public class NioHttpServerBuilder {

  private InetSocketAddress inetSocketAddress;

  private RequestHandler requestHandler;

  private ResponseHandler responseHandler;

  public static final NioHttpServerBuilder of() {
    return new NioHttpServerBuilder();
  }

  private NioHttpServerBuilder() {}

  public NioHttpServerBuilder inetSocketAddress(InetSocketAddress inetSocketAddress) {
    this.inetSocketAddress = inetSocketAddress;
    return this;
  }

  public NioHttpServerBuilder addHandler(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
    return this;
  }

  public NioHttpServerBuilder addHandler(ResponseHandler responseHandler) {
    this.responseHandler = responseHandler;
    return this;
  }

  public NioHttpServer build() throws Exception {
    return new NioHttpServerImpl(inetSocketAddress, requestHandler, responseHandler);
  }
}
