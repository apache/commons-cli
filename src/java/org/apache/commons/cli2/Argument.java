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

import java.util.ListIterator;

/**
 * An Option that can process values passed on the command line in the form
 * "--file README".
 */
public interface Argument extends Option {

    /**
     * Returns the initial separator character or
     * '\0' if no character has been set.
     * 
     * @return char the initial separator character
     */
    char getInitialSeparator();
    
    /**
     * Processes the "README" style element of the argument.
     *
     * Values identified should be added to the CommandLine object in
     * association with this Argument.
     *
     * @see WriteableCommandLine#addValue(Option,Object)
     *
     * @param commandLine The CommandLine object to store results in.
     * @param args The arguments to process.
     * @param option The option to register value against.
     * @throws OptionException if any problems occur.
     */
    void processValues(
        final WriteableCommandLine commandLine,
        final ListIterator args,
        final Option option)
            throws OptionException;
    
    /**
     * Adds defaults to a CommandLine.
     * 
     * @param commandLine
     *            The CommandLine object to store defaults in.
     * @param option
     *            The Option to store the defaults against.
     */
    void defaultValues(final WriteableCommandLine commandLine, final Option option);

    /**
     * Performs any necessary validation on the values added to the
     * CommandLine.
     *
     * Validation will typically involve using the
     * CommandLine.getValues(option) method to retrieve the values
     * and then either checking each value.  Optionally the String
     * value can be replaced by another Object such as a Number
     * instance or a File instance.
     *
     * @see CommandLine#getValues(Option)
     *
     * @param commandLine The CommandLine object to query.
     * @param option The option to lookup values with.
     * @throws OptionException if any problems occur.
     */
    void validate(final WriteableCommandLine commandLine, final Option option)
        throws OptionException;

    /**
     * Indicates whether argument values must be present for the CommandLine to
     * be valid.
     *
     * @see #getMinimum()
     * @see #getMaximum()
     * @return true iff the CommandLine will be invalid without at least one 
     *         value
     */
    boolean isRequired();

    /**
     * Retrieves the minimum number of values required for a valid Argument
     *
     * @return the minimum number of values
     */
    int getMinimum();

    /**
     * Retrieves the maximum number of values acceptable for a valid Argument
     *
     * @return the maximum number of values
     */
    int getMaximum();
}
