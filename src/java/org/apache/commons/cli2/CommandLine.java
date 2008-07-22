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

import java.util.List;
import java.util.Set;

/**
 * Instances of CommandLine represent a command line that has been processed
 * according to the definition supplied to the parser.
 */
public interface CommandLine {

    /**
     * Detects the presence of an option with the specified trigger in this
     * CommandLine.
     *
     * @param trigger the trigger to search for
     * @return true iff an option with this trigger is present
     */
    boolean hasOption(final String trigger);

    /**
     * Detects the presence of an option in this CommandLine.
     *
     * @param option the Option to search for
     * @return true iff the option is present
     */
    boolean hasOption(final Option option);

    /**
     * Finds the Option with the specified trigger
     *
     * @param trigger the name of the option to retrieve
     * @return the Option matching the trigger or null if none exists
     */
    Option getOption(final String trigger);

    /**
     * Retrieves the Argument values associated with the specified Option
     *
     * @param trigger a trigger used to lookup the Option
     * @return a list of values or an empty List if none are found
     */
    List getValues(final String trigger);

    /**
     * Retrieves the Argument values associated with the specified Option
     *
     * @param trigger a trigger used to lookup the Option
     * @param defaultValues the result to return if no values are found
     * @return a list of values or defaultValues if none are found
     */
    List getValues(final String trigger, final List defaultValues);

    /**
     * Retrieves the Argument values associated with the specified Option
     *
     * @param option the Option associated with the values
     * @return a list of values or an empty List if none are found
     */
    List getValues(final Option option);

    /**
     * Retrieves the Argument values associated with the specified Option
     *
     * @param option the Option associated with the values
     * @param defaultValues the result to return if no values are found
     * @return a list of values or defaultValues if none are found
     */
    List getValues(final Option option, final List defaultValues);

    /**
     * Retrieves the single Argument value associated with the specified Option
     *
     * @param trigger a trigger used to lookup the Option
     * @return the matching value or null if none exists
     * @throws IllegalStateException if more than one values are found
     */
    Object getValue(final String trigger) throws IllegalStateException;

    /**
     * Retrieves the single Argument value associated with the specified Option
     *
     * @param trigger a trigger used to lookup the Option
     * @param defaultValue the result to use if no values are found
     * @return the matching value or defaultValue if none exists
     * @throws IllegalStateException if more than one values are found
     */
    Object getValue(final String trigger, final Object defaultValue) throws IllegalStateException;

    /**
     * Retrieves the single Argument value associated with the specified Option
     *
     * @param option the Option associated with the value
     * @return the matching value or null if none exists
     * @throws IllegalStateException if more than one values are found
     */
    Object getValue(final Option option) throws IllegalStateException;

    /**
     * Retrieves the single Argument value associated with the specified Option
     *
     * @param option the Option associated with the value
     * @param defaultValue the result to use if no values are found
     * @return the matching value or defaultValue if none exists
     * @throws IllegalStateException if more than one values are found
     */
    Object getValue(final Option option, final Object defaultValue) throws IllegalStateException;

    /**
     * Retrieves the Boolean value associated with the specified Switch
     *
     * @param trigger a trigger used to lookup the Option
     * @return the Boolean associated with trigger or null if none exists
     */
    Boolean getSwitch(final String trigger);

    /**
     * Retrieves the Boolean value associated with the specified Switch
     *
     * @param trigger a trigger used to lookup the Option
     * @param defaultValue the Boolean to use if none match
     * @return the Boolean associated with trigger or defaultValue if none exists
     */
    Boolean getSwitch(final String trigger, final Boolean defaultValue);

    /**
     * Retrieves the Boolean value associated with the specified Switch
     *
     * @param option the Option associated with the value
     * @return the Boolean associated with option or null if none exists
     */
    Boolean getSwitch(final Option option);

    /**
     * Retrieves the Boolean value associated with the specified Switch
     *
     * @param option the Option associated with the value
     * @param defaultValue the Boolean to use if none match
     * @return the Boolean associated with option or defaultValue if none exists
     */
    Boolean getSwitch(final Option option, final Boolean defaultValue);


    /**
     * Retrieves the value associated with the specified property for the default property set
     *
     * @param property the property name to lookup
     * @return the value of the property or null
     */
    String getProperty(final String property);

    /**
     * Retrieves the value associated with the specified property
     *
     * @param option the option i.e., -D
     * @param property the property name to lookup
     * @return the value of the property or null
     */
    String getProperty(final Option option, final String property);

    /**
     * Retrieves the value associated with the specified property
     *
     * @param option the option i.e., -D
     * @param property the property name to lookup
     * @param defaultValue the value to use if no other is found
     * @return the value of the property or defaultValue
     */
    String getProperty(final Option option, final String property, final String defaultValue);

    /**
     * Retrieves the set of all property names associated with this option
     *
     * @param option the option i.e., -D
     * @return a none null set of property names
     */
    Set getProperties(final Option option);

    /**
     * Retrieves the set of all property names associated with the default property option
     *
     * @return a none null set of property names
     */
    Set getProperties();

    /**
     * Retrieves the number of times the specified Option appeared in this
     * CommandLine
     *
     * @param trigger a trigger used to lookup the Option
     * @return the number of occurrences of the option
     */
    int getOptionCount(final String trigger);

    /**
     * Retrieves the number of times the specified Option appeared in this
     * CommandLine
     *
     * @param option the Option associated to check
     * @return the number of occurrences of the option
     */
    int getOptionCount(final Option option);

    /**
     * Retrieves a list of all Options found in this CommandLine
     *
     * @return a none null list of Options
     */
    List getOptions();

    /**
     * Retrieves a list of all Option triggers found in this CommandLine
     *
     * @return a none null list of Option triggers
     */
    Set getOptionTriggers();
}
