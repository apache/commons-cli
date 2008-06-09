/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.cli2;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.cli2.resource.ResourceHelper;

/**
 * A problem found while dealing with command line options.
 */
public class OptionException
    extends Exception {
    /**
     * The settings used when displaying the related Option.
     *
     * @see DisplaySetting
     */
    public static final Set HELP_SETTINGS =
        Collections.unmodifiableSet(Collections.singleton(DisplaySetting.DISPLAY_PROPERTY_OPTION));

    /** resource helper instance */
    private static final ResourceHelper helper = ResourceHelper.getResourceHelper();

    /** The Option the exception relates to */
    private final Option option;

    /** The message explaining the Exception */
    private final String message;

    /** The id of the message */
    private final String messageKey;

    /**
     * Creates a new OptionException.
     *
     * @param option
     *            The Option the exception relates to
     */
    public OptionException(final Option option) {
        this(option, null, null);
    }

    /**
     * Creates a new OptionException.
     * @param option the Option the exception relates to
     * @param messageKey the id of the message to display
     */
    public OptionException(final Option option,
                           final String messageKey) {
        this(option, messageKey, null);
    }

    /**
     * Creates a new OptionException.
     * @param option the Option the exception relates to
     * @param messageKey the id of the message to display
     * @param value a value to display with the message
     */
    public OptionException(final Option option,
                           final String messageKey,
                           final String value) {
        this.option = option;
        this.messageKey = messageKey;

        if (messageKey != null) {
            final StringBuffer buffer = new StringBuffer();

            if (value != null) {
                buffer.append(helper.getMessage(messageKey, value));
            } else {
                buffer.append(helper.getMessage(messageKey));
            }

            buffer.append(" ");

            option.appendUsage(buffer, HELP_SETTINGS, null);
            message = buffer.toString();
        } else {
            message = "";
        }
    }

    /**
     * Gets the Option the exception relates to
     *
     * @return The related Option
     */
    public Option getOption() {
        return option;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
