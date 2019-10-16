/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.commons.cli.config;

/**
 * Represents an exception for incorrect definition and usage of a configuration
 * file.
 */
public class ConfigurationException extends Exception
{

    /**
     * Constructs an instance of <code>ConfigException</code> with the specified
     * detail message.
     *
     * @param message the detail message.
     */
    public ConfigurationException(final String message)
    {
        super(message);
    }

    /**
     * Constructs an instance of <code>ConfigException</code> with the specified
     * detail message.
     *
     * @param lineNo line number
     *
     * @param message the detail message.
     */
    public ConfigurationException(final Integer lineNo, final String message)
    {
        super("Line no. " + lineNo + ": " + message);
    }

    /**
     * Creates a new instance of <code>ConfigException</code> with the specified
     * detail message and cause.
     *
     * @param message the message detail.
     *
     * @param cause the cause of the exception.
     */
    public ConfigurationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
