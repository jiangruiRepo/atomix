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
package net.kuujo.copycat.spi;

import net.kuujo.copycat.protocol.ProtocolClient;
import net.kuujo.copycat.protocol.ProtocolServer;

import java.net.URI;

/**
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public interface Protocol {

  /**
   * Returns a boolean indicating whether the given URI is valid.
   *
   * @param uri The member URI to validate.
   * @return Indicates whether the given URI is valid.
   */
  default boolean validUri(URI uri) {
    return true;
  }

  /**
   * Creates a new protocol client.
   *
   * @param uri The member URI.
   * @return The protocol client.
   */
  ProtocolClient createClient(URI uri);

  /**
   * Creates a new protocol server.
   *
   * @param uri The member URI.
   * @return The protocol server.
   */
  ProtocolServer createServer(URI uri);

}
