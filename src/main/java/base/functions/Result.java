package base.functions;

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

import java.util.Objects;
import java.util.stream.Stream;

public final class Result<R, E extends Throwable> {

  private final E error;

  private final R result;

  public Result(R result, E error) {
    this.result = result;
    this.error = error;
  }

  public R getResult() {
    return result;
  }

  public E getError() {
    return error;
  }

  public ResultStatus status() {
    if (Objects.isNull(result)) {
      return ResultStatus.ERR;
    } else {
      return ResultStatus.OK;
    }
  }

  public Stream<Result<R, E>> toStream() {
    return Stream.of(this);
  }
}
