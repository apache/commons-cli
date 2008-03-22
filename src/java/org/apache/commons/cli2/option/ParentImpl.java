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
package org.apache.commons.cli2.option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.Parent;
import org.apache.commons.cli2.WriteableCommandLine;

/**
 * A base implementation of Parent providing limited ground work for further
 * Parent implementations.
 */
public abstract class ParentImpl
    extends OptionImpl implements Parent {
    private static final char NUL = '\0';
    private final Group children;
    private final Argument argument;
    private final String description;

    protected ParentImpl(final Argument argument,
                         final Group children,
                         final String description,
                         final int id,
                         final boolean required) {
        super(id, required);
        this.children = children;
        this.argument = argument;
        this.description = description;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.Option#process(org.apache.commons.cli2.CommandLine,
     *      java.util.ListIterator)
     */
    public void process(final WriteableCommandLine commandLine,
                        final ListIterator arguments)
        throws OptionException {
        if (argument != null) {
            handleInitialSeparator(arguments, argument.getInitialSeparator());
        }

        processParent(commandLine, arguments);

        if (argument != null) {
            argument.processValues(commandLine, arguments, this);
        }

        if ((children != null) && children.canProcess(commandLine, arguments)) {
            children.process(commandLine, arguments);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.Option#canProcess(java.lang.String)
     */
    public boolean canProcess(final WriteableCommandLine commandLine,
                              final String arg) {
        final Set triggers = getTriggers();

        if (argument != null) {
            final char separator = argument.getInitialSeparator();

            // if there is a valid separator character
            if (separator != NUL) {
                final int initialIndex = arg.indexOf(separator);

                // if there is a separator present
                if (initialIndex > 0) {
                    return triggers.contains(arg.substring(0, initialIndex));
                }
            }
        }

        return triggers.contains(arg);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.Option#prefixes()
     */
    public Set getPrefixes() {
        return (children == null) ? Collections.EMPTY_SET : children.getPrefixes();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.Option#validate(org.apache.commons.cli2.CommandLine)
     */
    public void validate(WriteableCommandLine commandLine)
        throws OptionException {
        if (commandLine.hasOption(this)) {
            if (argument != null) {
                argument.validate(commandLine, this);
            }

            if (children != null) {
                children.validate(commandLine);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.Option#appendUsage(java.lang.StringBuffer,
     *      java.util.Set, java.util.Comparator)
     */
    public void appendUsage(final StringBuffer buffer,
                            final Set helpSettings,
                            final Comparator comp) {
        final boolean displayArgument =
            (this.argument != null) &&
            helpSettings.contains(DisplaySetting.DISPLAY_PARENT_ARGUMENT);
        final boolean displayChildren =
            (this.children != null) &&
            helpSettings.contains(DisplaySetting.DISPLAY_PARENT_CHILDREN);

        if (displayArgument) {
            buffer.append(' ');
            argument.appendUsage(buffer, helpSettings, comp);
        }

        if (displayChildren) {
            buffer.append(' ');
            children.appendUsage(buffer, helpSettings, comp);
        }
    }

    /**
     * @return a description of this parent option
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.Option#helpLines(int, java.util.Set,
     *      java.util.Comparator)
     */
    public List helpLines(final int depth,
                          final Set helpSettings,
                          final Comparator comp) {
        final List helpLines = new ArrayList();
        helpLines.add(new HelpLineImpl(this, depth));

        if (helpSettings.contains(DisplaySetting.DISPLAY_PARENT_ARGUMENT) && (argument != null)) {
            helpLines.addAll(argument.helpLines(depth + 1, helpSettings, comp));
        }

        if (helpSettings.contains(DisplaySetting.DISPLAY_PARENT_CHILDREN) && (children != null)) {
            helpLines.addAll(children.helpLines(depth + 1, helpSettings, comp));
        }

        return helpLines;
    }

    /**
     * @return Returns the argument.
     */
    public Argument getArgument() {
        return argument;
    }

    /**
     * @return Returns the children.
     */
    public Group getChildren() {
        return children;
    }

    /**
     * Split the token using the specified separator character.
     * @param arguments the current position in the arguments iterator
     * @param separator the separator char to split on
     */
    private void handleInitialSeparator(final ListIterator arguments,
                                        final char separator) {
        // next token
        final String newArgument = (String) arguments.next();

        // split the token
        final int initialIndex = newArgument.indexOf(separator);

        if (initialIndex > 0) {
            arguments.remove();
            arguments.add(newArgument.substring(0, initialIndex));
            String value = newArgument.substring(initialIndex + 1);
            // The value obviously isn't an option, so we need to quote it if looks like an option.
            // The quotes will be removed later
            if (value.startsWith("-")) {
                value = '"' + value + '"';
            }
            arguments.add(value);
            arguments.previous();
        }

        arguments.previous();
    }

    /*
     * @see org.apache.commons.cli2.Option#findOption(java.lang.String)
     */
    public Option findOption(final String trigger) {
        final Option found = super.findOption(trigger);

        if ((found == null) && (children != null)) {
            return children.findOption(trigger);
        } else {
            return found;
        }
    }

    public void defaults(final WriteableCommandLine commandLine) {
        super.defaults(commandLine);

        if (argument != null) {
            argument.defaultValues(commandLine, this);
        }

        if (children != null) {
            children.defaults(commandLine);
        }
    }
}
