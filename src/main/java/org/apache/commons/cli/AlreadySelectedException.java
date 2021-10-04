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
 * Thrown when more than one option in an option group has been provided.
 */
public class AlreadySelectedException extends ParseException {

    /**
     * This exception {@code serialVersionUID}.
     */
    private static final long serialVersionUID = 3674381532418544760L;

    /** The option group selected. */
    private final OptionGroup group;

    /** The option that triggered the exception. */
    private final Option option;

    /**
     * Constructs a new {@code AlreadySelectedException} for the specified option group.
     *
     * @param group the option group already selected
     * @param option the option that triggered the exception
     * @since 1.2
     */
    public AlreadySelectedException(final OptionGroup group, final Option option) {
        this("The option '" + option.getKey() + "' was specified but an option from this group " + "has already been selected: '" + group.getSelected() + "'",
            group, option);
    }

    /**
     * Constructs a new {@code AlreadySelectedException} with the specified detail message.
     *
     * @param message the detail message
     */
    public AlreadySelectedException(final String message) {
        this(message, null, null);
    }

    private AlreadySelectedException(final String message, final OptionGroup group, final Option option) {
        super(message);
        this.group = group;
        this.option = option;
    }

    /**
     * Gets the option that was added to the group and triggered the exception.
     *
     * @return the related option
     * @since 1.2
     */
    public Option getOption() {
        return option;
    }

    /**
     * Gets the option group where another option has been selected.
     *
     * @return the related option group
     * @since 1.2
     */
    public OptionGroup getOptionGroup() {
        return group;
    }
}
