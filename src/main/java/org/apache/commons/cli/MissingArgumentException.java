/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

/**
 * Thrown when an option requiring an argument is not provided with an argument.
 */
public class MissingArgumentException extends CliException {
    private static final long serialVersionUID = -7098538588704965017L;

    /**
     * Constructs a new {@code MissingArgumentException} with the specified detail message.
     *
     * @param option the option requiring an argument
     * @since 1.2
     */
    public MissingArgumentException(final Option option) {
        super("Missing argument for option: " + option.getKey());
        this.option = option;
    }

    /**
     * Constructs a new {@code MissingArgumentException} with the specified detail message.
     *
     * @param message the detail message
     */
    public MissingArgumentException(final String message) {
        super(message);
    }
}
