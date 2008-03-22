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
package org.apache.commons.cli2.commandline;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.WriteableCommandLine;
import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.util.HelpFormatter;

/**
 * A class that implements the <code>Parser</code> interface can parse a
 * String array according to the {@link Group}specified and return a
 * {@link CommandLine}.
 *
 * @author John Keyes (john at integralsource.com)
 */
public class Parser {
    private HelpFormatter helpFormatter = new HelpFormatter();
    private Option helpOption = null;
    private String helpTrigger = null;
    private Group group = null;

    /**
     * Parse the arguments according to the specified options and properties.
     *
     * @param arguments
     *            the command line arguments
     *
     * @return the list of atomic option and value tokens
     * @throws OptionException
     *             if there are any problems encountered while parsing the
     *             command line tokens.
     */
    public CommandLine parse(final String[] arguments)
        throws OptionException {
        // build a mutable list for the arguments
        final List argumentList = new LinkedList();

        // copy the arguments into the new list
        for (int i = 0; i < arguments.length; i++) {
            final String argument = arguments[i];

            // ensure non intern'd strings are used
            // so that == comparisons work as expected
            argumentList.add(new String(argument));
        }

        // wet up a command line for this group
        final WriteableCommandLine commandLine = new WriteableCommandLineImpl(group, argumentList);

        // pick up any defaults from the model
        group.defaults(commandLine);

        // process the options as far as possible
        final ListIterator iterator = argumentList.listIterator();
        Object previous = null;

        while (group.canProcess(commandLine, iterator)) {
            // peek at the next item and backtrack
            final Object next = iterator.next();
            iterator.previous();

            // if we have just tried to process this instance
            if (next == previous) {
                // abort
                break;
            }

            // remember previous
            previous = next;

            group.process(commandLine, iterator);
        }

        // if there are more arguments we have a problem
        if (iterator.hasNext()) {
            final String arg = (String) iterator.next();
            throw new OptionException(group, ResourceConstants.UNEXPECTED_TOKEN, arg);
        }

        // no need to validate if the help option is present
        if (!commandLine.hasOption(helpOption) && !commandLine.hasOption(helpTrigger)) {
            group.validate(commandLine);
        }

        return commandLine;
    }

    /**
     * Parse the arguments according to the specified options and properties and
     * displays the usage screen if the CommandLine is not valid or the help
     * option was specified.
     *
     * @param arguments the command line arguments
     * @return a valid CommandLine or null if the parse was unsuccessful
     * @throws IOException if an error occurs while formatting help
     */
    public CommandLine parseAndHelp(final String[] arguments) {
        helpFormatter.setGroup(group);

        try {
            // attempt to parse the command line
            final CommandLine commandLine = parse(arguments);

            if (!commandLine.hasOption(helpOption) && !commandLine.hasOption(helpTrigger)) {
                return commandLine;
            }
        } catch (final OptionException oe) {
            // display help regarding the exception
            helpFormatter.setException(oe);
        }

        // print help
        helpFormatter.print();

        return null;
    }

    /**
     * Sets the Group of options to parse against
     * @param group the group of options to parse against
     */
    public void setGroup(final Group group) {
        this.group = group;
    }

    /**
     * Sets the HelpFormatter to use with the simplified parsing.
     * @see #parseAndHelp(String[])
     * @param helpFormatter the HelpFormatter to use with the simplified parsing
     */
    public void setHelpFormatter(final HelpFormatter helpFormatter) {
        this.helpFormatter = helpFormatter;
    }

    /**
     * Sets the help option to use with the simplified parsing.  For example
     * <code>--help</code>, <code>-h</code> and <code>-?</code> are often used.
     * @see #parseAndHelp(String[])
     * @param helpOption the help Option
     */
    public void setHelpOption(final Option helpOption) {
        this.helpOption = helpOption;
    }

    /**
     * Sets the help option to use with the simplified parsing.  For example
     * <code>--help</code>, <code>-h</code> and <code>-?</code> are often used.
     * @see #parseAndHelp(String[])
     * @param helpTrigger the trigger of the help Option
     */
    public void setHelpTrigger(final String helpTrigger) {
        this.helpTrigger = helpTrigger;
    }
}
