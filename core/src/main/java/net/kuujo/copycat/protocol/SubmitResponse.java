/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kuujo.copycat.protocol;

/**
 * Protocol submit response.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class SubmitResponse extends AbstractResponse {
  public static final int TYPE = -14;

  /**
   * Returns a new submit response builder.
   *
   * @return A new submit response builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  private Object result;

  /**
   * Returns the submission result.
   *
   * @return The submission result.
   */
  public Object result() {
    return result;
  }

  @Override
  public String toString() {
    return String.format("%s[id=%s]", getClass().getSimpleName(), id);
  }

  /**
   * Submit response builder.
   */
  public static class Builder extends AbstractResponse.Builder<Builder, SubmitResponse> {
    private Builder() {
      super(new SubmitResponse());
    }

    /**
     * Sets the response result.
     *
     * @param result The response result.
     * @return The response builder.
     */
    public Builder setResult(Object result) {
      response.result = result;
      return this;
    }

  }

}
