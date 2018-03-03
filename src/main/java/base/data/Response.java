package base.data;

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

import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class Response {

  private final Status status;

  private final String contentType;

  private final int contentLength;

  private final byte[] content;

  public Response(Status status, String contentType, int contentLength, byte[] content) {
    this.status = status;
    this.contentType = contentType;
    this.contentLength = contentLength;
    this.content = content;
  }

  public ByteBuffer toByteBuf() {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    StringBuilder sb = new StringBuilder();
    sb.append("HTTP/1.1");
    sb.append(status);
    sb.append("aaa");
    // TODO define offset and length
    return ByteBuffer.wrap(sb.toString().getBytes());
  }
}
