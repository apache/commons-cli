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
package org.apache.commons.cli.help;

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Util;

/**
 * The class for help formatters provides the framework to link the {@link HelpWriter} with the {@link OptionFormatter}
 * and a default {@link TableDef} so to produce a standard format help page.
 */
public abstract class AbstractHelpFormatter {

    /** The string to display at the beginning of the usage statement */
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

    /**
     * The default separator between {@link OptionGroup} elements.
     */
    public static final String DEFAULT_OPTION_GROUP_SEPARATOR = " | ";

    /**
     * The default comparator for {@link Option} implementations.
     */
     public static final Comparator<Option> DEFAULT_COMPARATOR = (opt1, opt2) -> opt1.getKey().compareToIgnoreCase(opt2.getKey());

    /**
     * The {@link HelpWriter} that produces the final output.
     */
    protected final HelpWriter helpWriter;
    /**
     * The OptionFormatter.Builder used to display options within the help page
     */
    protected final OptionFormatter.Builder optionFormatBuilder;
    /**
     * The phrase printed before the syntax line.
     */
    protected String syntaxPrefix = DEFAULT_SYNTAX_PREFIX;
    /**
     * A function to convert a collection of Options into a {@link TableDef} for display within the page
     */
    protected Function<Iterable<Option>, TableDef> tableDefBuilder;

    /** The comparator for sorting {@link Option} collections */
    protected Comparator<Option> comparator;

    /** The separator between {@link OptionGroup} components.*/
    protected final String optionGroupSeparator;


    /**
     * Constructs the base formatter.
     * @param helpWriter the helpWriter to output with
     * @param optionFormatBuilder the builder of {@link OptionFormatter} to format options for display.
     * @param tableDefBuilder A function to build a {@link TableDef} from a collection of {@link Option}s.
     * @param comparator The comparator to use for sorting options.
     * @param optionGroupSeparator the string to separate option groups.
     */
    protected AbstractHelpFormatter(final HelpWriter helpWriter, final OptionFormatter.Builder optionFormatBuilder,
                                    final Function<Iterable<Option>, TableDef> tableDefBuilder,
                                    final Comparator<Option> comparator,
                                    final String optionGroupSeparator) {
        this.helpWriter = Objects.requireNonNull(helpWriter, "helpWriter");
        this.optionFormatBuilder = Objects.requireNonNull(optionFormatBuilder, "optionFormatBuilder");
        this.tableDefBuilder = tableDefBuilder;
        this.comparator = Objects.requireNonNull(comparator, "comparator");
        this.optionGroupSeparator = Util.defaultValue(optionGroupSeparator, "");
    }

    /**
     * Sets the syntax prefix.  This is the phrase that is printed before the syntax line.
     *
     * @param prefix the new value for the syntax prefix.
     */
    public final void setSyntaxPrefix(final String prefix) {
        this.syntaxPrefix = prefix;
    }

    /**
     * Gets the currently set syntax prefix.
     * @return The currently set syntax prefix.
     */
    public final String getSyntaxPrefix() {
        return syntaxPrefix;
    }

    /**
     * Gets the {@link HelpWriter} associated with this help formatter.
     * @return The {@link HelpWriter} associated with this help formatter.
     */
    public final HelpWriter getSerializer() {
        return helpWriter;
    }

    /**
     * Constructs an {@link OptionFormatter} for the specified {@link Option}.
     * @param option The Option to format.
     * @return an {@link OptionFormatter} for the specified {@link Option}.
     */
    public final OptionFormatter getOptionFormatter(final Option option) {
        return optionFormatBuilder.build(option);
    }

    /**
     * Gets the comparator used by this HelpFormatter.
     * @return The comparator used by this HelpFormatter.
     */
    public Comparator<Option> getComparator() {
        return comparator;
    }

    /**
     * Prints the help for {@link Options} with the specified command line syntax.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the {@link Options} to print
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     * @throws IOException If the output could not be written to the {@link HelpWriter}
     */
    public final void printHelp(final String cmdLineSyntax, final String header, final Options options,
                                final String footer, final boolean autoUsage) throws IOException {
        printHelp(cmdLineSyntax, header, options.getOptions(), footer, autoUsage);
    }

    /**
     * Prints the help for a collection of {@link Option}s with the specified command line syntax.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the collection of {@link Option} objects to print.
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     * @throws IOException If the output could not be written to the {@link HelpWriter}
     */
    public void printHelp(final String cmdLineSyntax, final String header, final Iterable<Option> options,
                          final String footer, final boolean autoUsage) throws IOException {
        if (Util.isEmpty(cmdLineSyntax)) {
            throw new IllegalArgumentException("cmdLineSyntax not provided");
        }

        if (autoUsage) {
            helpWriter.writePara(format("%s %s %s", syntaxPrefix, cmdLineSyntax, asSyntaxOptions(options)));
        } else {
            helpWriter.writePara(format("%s %s", syntaxPrefix, cmdLineSyntax));
        }

        if (!Util.isEmpty(header)) {
            helpWriter.writePara(header);
        }

        helpWriter.writeTable(tableDefBuilder.apply(options));

        if (!Util.isEmpty(footer)) {
            helpWriter.writePara(footer);
        }
    }

    /**
     * Prints the option table for the specified {@link Options} to the {@link HelpWriter}.
     * @param options the Options to print in the table.
     * @throws IOException If the output could not be written to the {@link HelpWriter}
     */
    public final void printOptions(final Options options) throws IOException {
        printOptions(options.getOptions());
    }

    /**
     * Prints the option table for a collection of {@link Option} objects to the {@link HelpWriter}.
     * @param options the collection of Option objects to print in the table.
     * @throws IOException If the output could not be written to the {@link HelpWriter}
     */
    public final void printOptions(final Iterable<Option> options) throws IOException {
        printOptions(tableDefBuilder.apply(options));
    }
    /**
     * Prints a {@link TableDef} to the {@link HelpWriter}.
     * @param tableDef the {@link TableDef} to print.
     * @throws IOException If the output could not be written to the {@link HelpWriter}
     */
    public final void printOptions(final TableDef tableDef) throws IOException {
        helpWriter.writeTable(tableDef);
    }

    /**
     * Formats the {@code argName} as an argument a defined in the enclosed {@link OptionFormatter.Builder}
     * @param argName the string to format as an argument.
     * @return the {@code argName} formatted as an argument.
     */
    public final String asArgName(final String argName) {
        return optionFormatBuilder.asArgName(argName);
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param options The {@link Options} to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     */
    public String asSyntaxOptions(final Options options) {
        return asSyntaxOptions(options.getOptions(), options::getOptionGroup);
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param options The collection of {@link Option} instances to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     */
    public String asSyntaxOptions(final Iterable<Option> options) {
        return asSyntaxOptions(options, o -> null);
    }

    /**
     * Creates a new list of options ordered by the comparator.
     * @param options the Options to sort.
     * @return a new list of options ordered by the comparator.
     */
    public List<Option> sortedOptions(final Options options) {
        return sortedOptions(options == null ? null : options.getOptions());
    }

    /**
     * Creates a new list of options ordered by the comparator.
     * @param options the Options to sort.
     * @return a new list of options ordered by the comparator.
     */
    public List<Option> sortedOptions(final Iterable<Option> options) {
        List<Option> result = new ArrayList<>();
        if (options != null) {
            options.forEach(result::add);
            result.sort(comparator);
        }
        return result;
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param options The options to create the string representation for.
     * @param lookup a function to determine if the Option is part of an OptionGroup that has already been processed.
     * @return the string representation of the options as used in the syntax display.
     */
    protected String asSyntaxOptions(final Iterable<Option> options,
                                          final Function<Option, OptionGroup> lookup) {
        // list of groups that have been processed.
        final Collection<OptionGroup> processedGroups = new ArrayList<>();
        final List<Option> optList = sortedOptions(options);
        StringBuilder buff = new StringBuilder();
        String pfx = "";
        // iterate over the options
        for (final Option option : optList) {
            // get the next Option
            // check if the option is part of an OptionGroup
            final OptionGroup group = lookup.apply(option);
            // if the option is part of a group
            if (group != null) {
                // and if the group has not already been processed
                if (!processedGroups.contains(group)) {
                    // add the group to the processed list
                    processedGroups.add(group);
                    // add the usage clause
                    buff.append(pfx).append(asSyntaxOptions(group));
                    pfx = " ";
                }
                // otherwise the option was displayed in the group previously so ignore it.
            }
            // if the Option is not part of an OptionGroup
            else {
                buff.append(pfx).append(optionFormatBuilder.build(option).asSyntaxOption());
                pfx = " ";
            }
        }
        return buff.toString();
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param group The OptionGroup to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     */
    public String asSyntaxOptions(final OptionGroup group) {
        StringBuilder buff = new StringBuilder();
        final List<Option> optList = sortedOptions(group.getOptions());
        OptionFormatter formatter = null;
        // for each option in the OptionGroup
        Iterator<Option> iter = optList.iterator();
        while (iter.hasNext()) {
            formatter = optionFormatBuilder.build(iter.next());
            // whether the option is required or not is handled at group level
            buff.append(formatter.asSyntaxOption(true));

            if (iter.hasNext()) {
                buff.append(optionGroupSeparator);
            }
        }
        if (formatter != null) {
            return group.isRequired() ? buff.toString() : formatter.asOptional(buff.toString());
        }
        return ""; // there were no entries in the group.
    }
}
