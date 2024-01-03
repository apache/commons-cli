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
     * Converts any exception except UnsupportedOperationException to a ParseException.
     * if {@code e} is an instance of ParseException it is  returned, otherwise a ParseException is 
     * created that wraps it.
     * <p>
     * Note: UnsupportedOperationExceptions are not wrapped.  This is to solve a legacy expected exception problem and will  be 
     * removed in the future.</p> 
     * @param e the exception to convert.
     * @return the ParseException.
     * @throws UnsupportedOperationException due to legacy expectations.  Will be removed in the future.
     */
    public static ParseException wrap(final Exception e) throws UnsupportedOperationException {
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
    
    public ParseException(final Exception e) {
        super(e);
    }
    
    public ParseException(String message, Throwable e) {
        super(message, e);
    }

}
