package handler.request;

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
import base.exception.NioHttpServerException;
import fj.data.Option;
import handler.HttpServerHandler;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RequestHandler implements HttpServerHandler<ByteBuffer, Option<Request>> {

  private static final Pattern PATTERN =
      Pattern.compile("(?<method>.*) (?<path>.*?) (?<version>.*?)");

  private final Charset charset;

  public RequestHandler() {
    this.charset = Charset.forName("UTF-8");
  }

  @Override
  public Option<Request> apply(ByteBuffer buf) throws NioHttpServerException {
    Matcher matcher = PATTERN.matcher(charset.decode(buf));

    if (!matcher.find()) {
      return Option.none();
    }

    return Option.some(
        new Request(matcher.group("method"), matcher.group("path"), matcher.group("version")));
  }
}
