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
 * Base for Exceptions thrown during parsing of a command-line.
 */
public class ParseException extends Exception {

    /**
     * This exception {@code serialVersionUID}.
     */
    private static final long serialVersionUID = 9112808380089253192L;

    /**
     * Converts any exception except {@code UnsupportedOperationException} to a {@code ParseException}.
     * if {@code e} is an instance of {@code ParseException} it is returned, otherwise a {@code ParseException} is
     * created that wraps it.
     * <p>
     * Note: {@code UnsupportedOperationException} are not wrapped.  This is to solve a legacy expected exception problem and will be
     * removed in the future.</p>
     * @param e the exception to convert.
     * @return the ParseException.
     * @throws UnsupportedOperationException due to legacy expectations.  Will be removed in the future.
     * @since 1.7.0
     */
    public static ParseException wrap(final Throwable e) throws UnsupportedOperationException {
        if (e instanceof UnsupportedOperationException) {
            throw (UnsupportedOperationException) e;
        }

        if (e instanceof ParseException) {
            return (ParseException) e;
        }
        return new ParseException(e);
    }

    /**
     * Constructs a new {@code ParseException} with the specified detail message.
     *
     * @param message the detail message
     */
    public ParseException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ParseException} wrapping the specified exception.
     *
     * @param e the Exception to wrap.
     */
    public ParseException(final Throwable e) {
        super(e);
    }
}
