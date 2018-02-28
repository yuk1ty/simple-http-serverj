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

import concurrent.WorkerThreadFactory;
import handler.request.impl.RequestHandlerImpl;
import handler.response.impl.ResponseHandlerImpl;
import server.NioHttpServer;
import server.context.NioHttpServerBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerBootstrap {

  public static void main(String[] args) throws IOException {
    NioHttpServer httpServer =
        NioHttpServerBuilder.of()
            .inetSocketAddress(new InetSocketAddress(8080))
            .responseHandler(new ResponseHandlerImpl())
            .requestHandler(new RequestHandlerImpl())
            .build();
    // TODO define thread pool size
    ExecutorService executor = Executors.newCachedThreadPool();
    executor.execute(new WorkerThreadFactory().newThread(httpServer));
  }
}
