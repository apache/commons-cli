/**
 * Copyright 2003-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

/**
 * A CommandLine that detected values and options can be written to.
 */
public interface WriteableCommandLine extends CommandLine {
    
    /**
     * Adds an Option to the CommandLine
     * @param option the Option to add
     */
    void addOption(final Option option);
    
    /**
     * Adds a value to an Option in the CommandLine.
     * @param option the Option to add to
     * @param value the value to add
     */
    void addValue(final Option option, final Object value);
    
    /**
     * Adds a switch value to an Option in the CommandLine.
     * @param option the Option to add to
     * @param value the switch value to add
     * @throws IllegalStateException if the switch has already been added
     */
    void addSwitch(final Option option, final boolean value) throws IllegalStateException;
    
    /**
     * Adds a property value to a name in the CommandLine.
     * Replaces any existing value for the property.
     * 
     * @param property the name of the property
     * @param value the value of the property
     */
    void addProperty(final String property, final String value);
    
    /**
     * Detects whether the argument looks like an Option trigger 
     * @param argument the argument to test
     * @return true if the argument looks like an Option trigger
     */
    boolean looksLikeOption(final String argument);
}
