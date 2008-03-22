/**
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

import java.util.Comparator;
import java.util.Set;

/**
 * Represents a line of help for a particular Option.
 */
public interface HelpLine {

    /**
     * @return The description of the option
     */
    String getDescription();

    /**
     * @return The level of indentation for this line
     */
    int getIndent();

    /**
     * @return The Option that the help line relates to
     */
    Option getOption();

    /**
     * Builds a usage string for the option using the specified settings and
     * comparator.
     *
     * @param helpSettings
     *            the settings to apply
     * @param comparator
     *            a comparator to sort options when applicable
     * @return the usage string
     */
    String usage(final Set helpSettings, final Comparator comparator);
}