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
 * An Option representing a choice or group of Options in the form "-a|-b|-c".
 */
public interface Group extends Option {

    /**
     * Appends usage information to the specified StringBuffer
     * 
     * @param buffer the buffer to append to
     * @param helpSettings a set of display settings @see DisplaySetting
     * @param comp a comparator used to sort the Options
     * @param separator the String used to separate member Options 
     */
    void appendUsage(
        final StringBuffer buffer,
        final Set helpSettings,
        final Comparator comp,
        final String separator);

    /**
     * Indicates whether group members must be present for the CommandLine to be
     * valid.
     *
     * @see #getMinimum()
     * @see #getMaximum()
     * @return true iff the CommandLine will be invalid without at least one 
     *         member option
     */
    boolean isRequired();

    /**
     * Retrieves the minimum number of members required for a valid Group
     *
     * @return the minimum number of members
     */
    int getMinimum();

    /**
     * Retrieves the maximum number of members acceptable for a valid Group
     *
     * @return the maximum number of members
     */
    int getMaximum();
}
