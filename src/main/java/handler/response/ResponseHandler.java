package handler.response;

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
import base.data.Status;
import base.exception.NioHttpServerException;
import handler.HttpServerHandler;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ResponseHandler implements HttpServerHandler<Request, Response> {

  @Override
  public Response apply(Request request) throws NioHttpServerException {
    if (!Objects.equals(request.getMethod(), "GET")) {
      return new Response(
          Status.BAD_REQUEST,
          "text/html;charset=utf8",
          String.format("Cannot handle %s method", request.getMethod())
              .getBytes(StandardCharsets.UTF_8));
    }

    if (Objects.equals(request.getPath(), "/hello")) {
      return new Response(
          Status.OK,
          "text/html;charset=utf8",
          "Hello, NioHttpServer!".getBytes(StandardCharsets.UTF_8));
    }

    return new Response(
        Status.NOT_FOUND, "text/html;charset=utf8", "NotFound".getBytes(StandardCharsets.UTF_8));
  }
}
