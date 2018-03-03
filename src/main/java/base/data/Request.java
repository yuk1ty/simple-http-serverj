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

public final class Request {

  private static final Request EMPTY = new Request(null, null, null);

  private final String method;

  private final String path;

  private final String httpVersion;

  public Request(String method, String path, String httpVersion) {
    this.method = method;
    this.path = path;
    this.httpVersion = httpVersion;
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public String getHttpVersion() {
    return httpVersion;
  }
}
