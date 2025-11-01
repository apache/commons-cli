/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package org.apache.commons.cli.help;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

/**
 * Helps formatters provides the framework to link a {@link HelpAppendable} with a {@link OptionFormatter} and a default {@link TableDefinition} so to produce
 * standardized format help output.
 *
 * @since 1.10.0
 */
public abstract class AbstractHelpFormatter {

    /**
     * Abstracts building instances for subclasses.
     * <ul>
     * <li>helpAppendable = a {@link TextHelpAppendable} writing to {@code System.out}</li>
     * <li>optionFormatter.Builder = the default {@link OptionFormatter.Builder}</li>
     * </ul>
     *
     * @param <B> The builder type.
     * @param <T> The type to build.
     */
    public abstract static class Builder<B extends Builder<B, T>, T extends AbstractHelpFormatter> implements Supplier<T> {

        /** The comparator to sort lists of options */
        private Comparator<Option> comparator = DEFAULT_COMPARATOR;

        /** The {@link HelpAppendable}. */
        private HelpAppendable helpAppendable = TextHelpAppendable.systemOut();

        /** The {@link OptionFormatter.Builder} to use to format options in the table. */
        private OptionFormatter.Builder optionFormatBuilder = OptionFormatter.builder();

        /** The string to separate option groups. */
        private String optionGroupSeparator = DEFAULT_OPTION_GROUP_SEPARATOR;

        /**
         * Constructs a new instance.
         * <p>
         * Sets {@code showSince} to {@code true}.
         * </p>
         */
        protected Builder() {
            // empty
        }

        /**
         * Returns this instance cast to {@code B}.
         *
         * @return {@code this} instance cast to {@code B}.
         */
        @SuppressWarnings("unchecked")
        protected B asThis() {
            return (B) this;
        }

        /**
         * Gets the comparator to sort lists of options.
         *
         * @return the comparator to sort lists of options.
         */
        protected Comparator<Option> getComparator() {
            return comparator;
        }

        /**
         * Gets {@link HelpAppendable}.
         *
         * @return the {@link HelpAppendable}.
         */
        protected HelpAppendable getHelpAppendable() {
            return helpAppendable;
        }

        /**
         * Gets {@link OptionFormatter.Builder} to use to format options in the table.
         *
         * @return the {@link OptionFormatter.Builder} to use to format options in the table.
         */
        protected OptionFormatter.Builder getOptionFormatBuilder() {
            return optionFormatBuilder;
        }

        /**
         * Gets string to separate option groups.
         *
         * @return the string to separate option groups.
         */
        protected String getOptionGroupSeparator() {
            return optionGroupSeparator;
        }

        /**
         * Sets the comparator to use for sorting options. If set to {@code null} no sorting is performed.
         *
         * @param comparator The comparator to use for sorting options.
         * @return this
         */
        public B setComparator(final Comparator<Option> comparator) {
            this.comparator = comparator;
            return asThis();
        }

        /**
         * Sets the {@link HelpAppendable}.
         *
         * @param helpAppendable the {@link HelpAppendable} to use.
         * @return this
         */
        public B setHelpAppendable(final HelpAppendable helpAppendable) {
            this.helpAppendable = helpAppendable != null ? helpAppendable : TextHelpAppendable.systemOut();
            return asThis();
        }

        /**
         * Sets the {@link OptionFormatter.Builder}.
         *
         * @param optionFormatBuilder the {@link OptionFormatter.Builder} to use.
         * @return this
         */
        public B setOptionFormatBuilder(final OptionFormatter.Builder optionFormatBuilder) {
            this.optionFormatBuilder = optionFormatBuilder != null ? optionFormatBuilder : OptionFormatter.builder();
            return asThis();
        }

        /**
         * Sets the OptionGroup separator. Normally " | " or something similar to denote that only one option may be chosen.
         *
         * @param optionGroupSeparator the string to separate option group elements with.
         * @return this
         */
        public B setOptionGroupSeparator(final String optionGroupSeparator) {
            this.optionGroupSeparator = Util.defaultValue(optionGroupSeparator, "");
            return asThis();
        }

    }

    /**
     * The default comparator for {@link Option} implementations.
     */
    public static final Comparator<Option> DEFAULT_COMPARATOR = (opt1, opt2) -> opt1.getKey().compareToIgnoreCase(opt2.getKey());

    /**
     * The default separator between {@link OptionGroup} elements: {@value}.
     */
    public static final String DEFAULT_OPTION_GROUP_SEPARATOR = " | ";

    /**
     * The string to display at the beginning of the usage statement: {@value}.
     */
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

    /** The comparator for sorting {@link Option} collections */
    private final Comparator<Option> comparator;
    /**
     * The {@link HelpAppendable} that produces the final output.
     */
    private final HelpAppendable helpAppendable;

    /**
     * The OptionFormatter.Builder used to display options within the help page.
     */
    private final OptionFormatter.Builder optionFormatBuilder;

    /** The separator between {@link OptionGroup} components. */
    private final String optionGroupSeparator;

    /**
     * The phrase printed before the syntax line.
     */
    private String syntaxPrefix = DEFAULT_SYNTAX_PREFIX;

    /**
     * Constructs the base formatter.
     *
     * @param builder the builder.
     */
    protected AbstractHelpFormatter(final Builder<?, ?> builder) {
        this.helpAppendable = Objects.requireNonNull(builder.getHelpAppendable(), "helpAppendable");
        this.optionFormatBuilder = Objects.requireNonNull(builder.getOptionFormatBuilder(), "optionFormatBuilder");
        this.comparator = Objects.requireNonNull(builder.getComparator(), "comparator");
        this.optionGroupSeparator = Util.defaultValue(builder.getOptionGroupSeparator(), "");
    }

    /**
     * Gets the comparator for sorting options.
     *
     * @return The comparator for sorting options.
     */
    protected Comparator<Option> getComparator() {
        return comparator;
    }

    /**
     * Gets the help appendable.
     *
     * @return The help appendable.
     */
    protected HelpAppendable getHelpAppendable() {
        return helpAppendable;
    }

    /**
     * Gets the option formatter builder.
     *
     * @return The option formatter builder.
     */
    protected OptionFormatter.Builder getOptionFormatBuilder() {
        return optionFormatBuilder;
    }

    /**
     * Constructs an {@link OptionFormatter} for the specified {@link Option}.
     *
     * @param option The Option to format.
     * @return an {@link OptionFormatter} for the specified {@link Option}.
     */
    public final OptionFormatter getOptionFormatter(final Option option) {
        return optionFormatBuilder.build(option);
    }

    /**
     * Gets the option group separator.
     *
     * @return The option group separator.
     */
    protected String getOptionGroupSeparator() {
        return optionGroupSeparator;
    }

    /**
     * Gets the {@link HelpAppendable} associated with this help formatter.
     *
     * @return The {@link HelpAppendable} associated with this help formatter.
     */
    public final HelpAppendable getSerializer() {
        return helpAppendable;
    }

    /**
     * Gets the currently set syntax prefix.
     *
     * @return The currently set syntax prefix.
     */
    public final String getSyntaxPrefix() {
        return syntaxPrefix;
    }

    /**
     * Converts a collection of {@link Option}s into a {@link TableDefinition}.
     *
     * @param options The options to create a table for.
     * @return the TableDefinition.
     */
    protected abstract TableDefinition getTableDefinition(Iterable<Option> options);

    /**
     * Prints the help for a collection of {@link Option}s with the specified command line syntax.
     *
     * @param cmdLineSyntax the syntax for this application.
     * @param header        the banner to display at the beginning of the help.
     * @param options       the collection of {@link Option} objects to print.
     * @param footer        the banner to display at the end of the help.
     * @param autoUsage     whether to print an automatically generated usage statement.
     * @throws IOException If the output could not be written to the {@link HelpAppendable}.
     */
    public void printHelp(final String cmdLineSyntax, final String header, final Options options, final String footer, final boolean autoUsage)
            throws IOException {
        if (Util.isEmpty(cmdLineSyntax)) {
            throw new IllegalArgumentException("cmdLineSyntax not provided");
        }
        if (autoUsage) {
            helpAppendable.appendParagraphFormat("%s %s %s", syntaxPrefix, cmdLineSyntax, toSyntaxOptions(options));
        } else {
            helpAppendable.appendParagraphFormat("%s %s", syntaxPrefix, cmdLineSyntax);
        }
        if (!Util.isEmpty(header)) {
            helpAppendable.appendParagraph(header);
        }
        helpAppendable.appendTable(getTableDefinition(options.getOptions()));
        if (!Util.isEmpty(footer)) {
            helpAppendable.appendParagraph(footer);
        }
    }

    /**
     * Prints the help for {@link Options} with the specified command line syntax.
     *
     * @param cmdLineSyntax the syntax for this application.
     * @param header        the banner to display at the beginning of the help.
     * @param options       the collection of {@link Option} objects to print.
     * @param footer        the banner to display at the end of the help.
     * @param autoUsage     whether to print an automatically generated usage statement.
     * @throws IOException If the output could not be written to the {@link HelpAppendable}.
     */
    public final void printHelp(final String cmdLineSyntax, final String header, final Iterable<Option> options, final String footer, final boolean autoUsage)
            throws IOException {
        Options optionsObject = new Options();
        options.forEach(optionsObject::addOption);
        printHelp(cmdLineSyntax, header, optionsObject, footer, autoUsage);
    }

    /**
     * Prints the option table for a collection of {@link Option} objects to the {@link HelpAppendable}.
     *
     * @param options the collection of Option objects to print in the table.
     * @throws IOException If the output could not be written to the {@link HelpAppendable}.
     */
    public final void printOptions(final Iterable<Option> options) throws IOException {
        printOptions(getTableDefinition(options));
    }

    /**
     * Prints the option table for the specified {@link Options} to the {@link HelpAppendable}.
     *
     * @param options the Options to print in the table.
     * @throws IOException If the output could not be written to the {@link HelpAppendable}.
     */
    public final void printOptions(final Options options) throws IOException {
        printOptions(options.getOptions());
    }

    /**
     * Prints a {@link TableDefinition} to the {@link HelpAppendable}.
     *
     * @param tableDefinition the {@link TableDefinition} to print.
     * @throws IOException If the output could not be written to the {@link HelpAppendable}.
     */
    public final void printOptions(final TableDefinition tableDefinition) throws IOException {
        helpAppendable.appendTable(tableDefinition);
    }

    /**
     * Sets the syntax prefix. This is the phrase that is printed before the syntax line.
     *
     * @param prefix the new value for the syntax prefix.
     */
    public final void setSyntaxPrefix(final String prefix) {
        this.syntaxPrefix = prefix;
    }

    /**
     * Creates a new list of options ordered by the comparator.
     *
     * @param options the Options to sort.
     * @return a new list of options ordered by the comparator.
     */
    public List<Option> sort(final Iterable<Option> options) {
        final List<Option> result = new ArrayList<>();
        if (options != null) {
            options.forEach(result::add);
            result.sort(comparator);
        }
        return result;
    }

    /**
     * Creates a new list of options ordered by the comparator.
     *
     * @param options the Options to sort.
     * @return a new list of options ordered by the comparator.
     */
    public List<Option> sort(final Options options) {
        return sort(options == null ? null : options.getOptions());
    }

    /**
     * Formats the {@code argName} as an argument a defined in the enclosed {@link OptionFormatter.Builder}.
     *
     * @param argName the string to format as an argument.
     * @return the {@code argName} formatted as an argument.
     */
    public final String toArgName(final String argName) {
        return optionFormatBuilder.toArgName(argName);
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     *
     * @param options The collection of {@link Option} instances to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     */
    public String toSyntaxOptions(final Iterable<Option> options) {
        return toSyntaxOptions(options, o -> null);
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     *
     * @param options The options to create the string representation for.
     * @param lookup  a function to determine if the Option is part of an OptionGroup that has already been processed.
     * @return the string representation of the options as used in the syntax display.
     */
    protected String toSyntaxOptions(final Iterable<Option> options, final Function<Option, OptionGroup> lookup) {
        // list of groups that have been processed.
        final Collection<OptionGroup> processedGroups = new ArrayList<>();
        final List<Option> optList = sort(options);
        final StringBuilder buff = new StringBuilder();
        String prefix = "";
        // iterate over the options
        for (final Option option : optList) {
            // get the next Option
            // check if the option is part of an OptionGroup
            final OptionGroup optionGroup = lookup.apply(option);
            // if the option is part of a group
            if (optionGroup != null) {
                // and if the group has not already been processed
                if (!processedGroups.contains(optionGroup)) {
                    // add the group to the processed list
                    processedGroups.add(optionGroup);
                    // add the usage clause
                    buff.append(prefix).append(toSyntaxOptions(optionGroup));
                    prefix = " ";
                }
                // otherwise the option was displayed in the group previously so ignore it.
            }
            // if the Option is not part of an OptionGroup
            else {
                buff.append(prefix).append(optionFormatBuilder.build(option).toSyntaxOption());
                prefix = " ";
            }
        }
        return buff.toString();
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     *
     * @param group The OptionGroup to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     */
    public String toSyntaxOptions(final OptionGroup group) {
        final StringBuilder buff = new StringBuilder();
        final List<Option> optList = sort(group.getOptions());
        OptionFormatter formatter = null;
        // for each option in the OptionGroup
        final Iterator<Option> iter = optList.iterator();
        while (iter.hasNext()) {
            formatter = optionFormatBuilder.build(iter.next());
            // whether the option is required or not is handled at group level
            buff.append(formatter.toSyntaxOption(true));

            if (iter.hasNext()) {
                buff.append(optionGroupSeparator);
            }
        }
        if (formatter != null) {
            return group.isRequired() ? buff.toString() : formatter.toOptional(buff.toString());
        }
        return ""; // there were no entries in the group.
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     *
     * @param options The {@link Options} to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     */
    public String toSyntaxOptions(final Options options) {
        return toSyntaxOptions(options.getOptions(), options::getOptionGroup);
    }
}
