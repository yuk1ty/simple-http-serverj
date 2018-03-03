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

public enum Status {
  OK(200, "200 OK"),
  BAD_REQUEST(400, "400 BadRequest"),
  NOT_FOUND(404, "404 NotFound");

  private final int statusCode;

  private final String statusText;

  Status(int statusCode, String statusText) {
    this.statusCode = statusCode;
    this.statusText = statusText;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getStatusText() {
    return statusText;
  }
}
