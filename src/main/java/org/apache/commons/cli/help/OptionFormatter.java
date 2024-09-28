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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Util;

/**
 * The definition of how to display Option attributes.
 */
public final class OptionFormatter {

    /** The default delimiters for optional arguments */
    private static final String[] DEFAULT_OPTIONAL_DELIMITERS = {"[", "]"};
    /** The default delimiters for an argument name */
    private static final String[] DEFAULT_ARG_NAME_DELIMITERS = {"<", ">"};
    /** The default argument name */
    public static final String DEFAULT_ARG_NAME = "arg";

    /**
     * The default A function to convert an option into a string with the description and a deprecated warning.
     */
    public static final Function<Option, String> DEFAULT_DEPRECATED_FORMAT = o -> "[Deprecated] " + Util.defaultValue(o.getDescription(), "");

    /**
     * The default {@link BiFunction} to produce the option syntax component from an {@link OptionFormatter} and a
     * required flag.  The default implementation returns string like:
     * <ul>
     *      <li>-o &lt;arg&gt; -- for required.</li>
     *     <li>[-o &lt;arg&gt;] -- for optional.</li>
     *      <li>--opt &lt;arg&gt; -- for required with long option only.</li>
     *     <li>[--opt &lt;arg&gt;] -- for optional with long option only.</li>
     * </ul>
     * <p>long or shot options will not be displayed if they are not set in the Option, argument name will be as specified in
     * in the argument or </p>
     * @see Builder#setSyntaxFormatFunction(BiFunction)
     */
    public static final BiFunction<OptionFormatter, Boolean, String> DEFAULT_SYNTAX_FORMAT = (o, required) -> {
        StringBuilder buff = new StringBuilder();
        String argName = o.getArgName();
        buff.append(Util.defaultValue(o.getOpt(), o.getLongOpt()));
        if (!Util.isEmpty(argName)) {
            buff.append(' ').append(argName);
        }
        boolean requiredFlg = required == null ? o.isRequired() : required;
        return requiredFlg ? buff.toString() : o.asOptional(buff.toString());
    };

    /**
     * The string to display at the beginning of the usage statement.
     */
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

    /**
     * Default prefix for short options.
     */
    public static final String DEFAULT_OPT_PREFIX = "-";

    /**
     * Default prefix for long options.
     */
    public static final String DEFAULT_LONG_OPT_PREFIX = "--";

    /**
     * The default separator between options.
     */
    public static final String DEFAULT_OPT_SEPARATOR = " ";

    /**
     * The default comparator for {@link Option} implementations.
     */
    public static final Comparator<Option> DEFAULT_COMPARATOR = (opt1, opt2) -> opt1.getKey().compareToIgnoreCase(opt2.getKey());

    /**
     * The default separator between {@link OptionGroup} elements.
     */
    public static final String DEFAULT_OPTION_GROUP_SEPARATOR = " | ";

    /**
     * The delimiters around argument names.
     */
    private final String[] argNameDelimiters;
    /** The default argument name */
    private final String defaultArgName;
    /** The comparator for sorting {@link Option} collections */
    private final Comparator<Option> comparator;
    /** The function to display the deprecated option message */
    private final Function<Option, String> deprecatedFormatFunction;
    /** The prefix for the long option text */
    private final String longOptPrefix;
    /** The prefix for the short option text */
    private final String optPrefix;
    /** The separator between the options */
    private final String optSeparator;
    /** The separator between {@link OptionGroup} components.*/
    private final String optionGroupSeparator;
    /** The delimiters for optional {@link Option}s. */
    private final String[] optionalDelimiters;
    /** The method to convert an Option formatter into a syntax notation.*/
    private final BiFunction<OptionFormatter, Boolean, String> syntaxFormatFunction;
    /** The {@link Option} being formatted */
    private final Option option;

    /**
     * Construct the {@link OptionFormatter} from an {@link Option} using the default {@link OptionFormatter.Builder}.
     * @param option the option to format.
     * @return an OptionFormatter for the specified @{code option}.
     */
    public static OptionFormatter from(final Option option) {
        return new OptionFormatter.Builder().build(option);
    }

    /**
     * A Builder for OptionFormatters.
     */
    public static final class Builder  {
        /** the argument name delimiters */
        private final String[] argNameDelimiters;
        /** The default argument name */
        private String defaultArgName;
        /** The {@link Option} comparator for sorting collections */
        private Comparator<Option> comparator;
        /** The function to create the deprecated message for an option */
        private Function<Option, String> deprecatedFormatFunction;
        /** The long option prefix */
        private String longOptPrefix;
        /** the option prefix */
        private String optPrefix;
        /** The separator between long and short options*/
        private String optSeparator;
        /** The separator between {@link OptionGroup} elements.*/
        private String optionGroupSeparator;
        /** The delimiters surrounding optional {@link Option} instances. */
        private final String[] optionalDelimiters;
        /** A function to convert the {@link OptionFormatter} into an entry in the syntax description. */
        private BiFunction<OptionFormatter, Boolean, String> syntaxFormatFunction;

        /**
         * Default constructor.  Uses the defaults specified in {@link OptionFormatter}.
         */
        public Builder() {
            argNameDelimiters = Arrays.copyOf(DEFAULT_ARG_NAME_DELIMITERS, 2);
            defaultArgName = DEFAULT_ARG_NAME;
            comparator = DEFAULT_COMPARATOR;
            deprecatedFormatFunction = null;
            longOptPrefix = DEFAULT_LONG_OPT_PREFIX;
            optPrefix = DEFAULT_OPT_PREFIX;
            optSeparator = DEFAULT_OPT_SEPARATOR;
            optionGroupSeparator = DEFAULT_OPTION_GROUP_SEPARATOR;
            optionalDelimiters = Arrays.copyOf(DEFAULT_OPTIONAL_DELIMITERS, 2);
            syntaxFormatFunction = DEFAULT_SYNTAX_FORMAT;
        }

        /**
         * Constructor that takes the arguments from the supplied {@link OptionFormatter}
         * @param optionFormatter The option formatter to provide values for the builder.
         */
        public Builder(final OptionFormatter optionFormatter) {
            optionalDelimiters = Arrays.copyOf(optionFormatter.optionalDelimiters, 2);
            argNameDelimiters = Arrays.copyOf(optionFormatter.argNameDelimiters, 2);
            defaultArgName = optionFormatter.defaultArgName;
            optPrefix = optionFormatter.optPrefix;
            longOptPrefix = optionFormatter.longOptPrefix;
            optSeparator = optionFormatter.optSeparator;
            optionGroupSeparator = optionFormatter.optionGroupSeparator;
            deprecatedFormatFunction = optionFormatter.deprecatedFormatFunction;
            syntaxFormatFunction = optionFormatter.syntaxFormatFunction;
            comparator = optionFormatter.comparator;
        }

        /**
         * Build an OptionFormatter to format the specified option.
         * @param option The Option to format.
         * @return An OptionFormatter to format the specified option.
         */
        public OptionFormatter build(final Option option) {
            return new OptionFormatter(option, this);
        }

        /**
         * Sets the default argument name.
         *
         * @param name the new value of default argument name.
         * @return this
         */
        public Builder setDefaultArgName(final String name) {
            this.defaultArgName = Util.defaultValue(name, DEFAULT_ARG_NAME);
            return this;
        }

        /**
         * A helper method to format any string as an argument name based on this builder.
         * @param argName the name of the argument.
         * @return the formatted argument.
         */
        public String asArgName(final String argName) {
            return argNameDelimiters[0] + Util.defaultValue(argName, "") + argNameDelimiters[1];
        }

        /**
         * Sets the long option prefix.
         *
         * @param prefix prefix for long options.
         * @return this
         */
        public Builder setLongOptPrefix(final String prefix) {
            this.longOptPrefix = Util.defaultValue(prefix, "");
            return this;
        }

        /**
         * Sets the separator displayed between a long option and short options.  Typically ',' or ' '.
         *
         * @param optSeparator the separator.
         * @since 1.3
         * @return this
         */
        public Builder setOptSeparator(final String optSeparator) {
            this.optSeparator = Util.defaultValue(optSeparator, "");
            return this;
        }

        /**
         * Sets the separator displayed between {@link Option}s in an {@link OptionGroup} listing.
         *
         * @param optionGroupSeparator the separator.
         * @return this
         * @since 1.9
         */
        public Builder setOptionGroupSeparator(final String optionGroupSeparator) {
            this.optionGroupSeparator = Util.defaultValue(optionGroupSeparator, "");
            return this;
        }

        /**
         * Sets the comparator used to sort the options when they output in help text. Passing in a null comparator will keep the
         * options in the order they were declared.
         *
         * @param comparator the {@link Comparator} to use for sorting the options, may be {@code null}.
         * @return this
         * @since 1.9
         */
        public Builder setComparator(final Comparator<Option> comparator) {
            this.comparator = comparator;
            return this;
        }

        /**
         * Specifies the starting and ending delimiters for optional {@link Option} instances.
         * @param begin the beginning delimiter.
         * @param end the ending delimiter.
         * @return this.
         */
        public Builder setOptionalDelimiters(final String begin, final String end) {
            this.optionalDelimiters[0] = Util.defaultValue(begin, "");
            this.optionalDelimiters[1] = Util.defaultValue(end, "");
            return this;
        }

        /**
         * Specifies the starting and ending argument name delimiters for {@link Option} instances.
         * @param begin the beginning delimiter.
         * @param end the ending delimiter.
         * @return this.
         */
        public Builder setArgumentNameDelimiters(final String begin, final String end) {
            this.argNameDelimiters[0] = Util.defaultValue(begin, "");
            this.argNameDelimiters[1] = Util.defaultValue(end, "");
            return this;
        }

        /**
         * Specifies the short option prefix.
         * @param optPrefix the prefix for short options.
         * @return this.
         */
        public Builder setOptPrefix(final String optPrefix) {
            this.optPrefix = Util.defaultValue(optPrefix, "");
            return this;
        }

        /**
         * Specifies the function to construct the deprecated massage for the Option.
         * Should include the description text if desired.
         * @param deprecatedFormatFunction the function to specify the deprecated message for the option.
         * @return this.
         */
        public Builder setDeprecatedFormatFunction(final Function<Option, String> deprecatedFormatFunction) {
            this.deprecatedFormatFunction = deprecatedFormatFunction;
            return this;
        }

        /**
         * Specifies the function to convert an {@link OptionFormatter} into the syntax format for the option.
         * @param syntaxFormatFunction The function to convert an {@link OptionFormatter} into the syntax format for the option.
         * @return this
         */
        public Builder setSyntaxFormatFunction(final BiFunction<OptionFormatter, Boolean, String> syntaxFormatFunction) {
            this.syntaxFormatFunction = syntaxFormatFunction == null ? DEFAULT_SYNTAX_FORMAT : syntaxFormatFunction;
            return this;
        }
    }

    /**
     * An OptionFormatter applies formatting options to various {@link Option} attributes for textual display.
     * @param option the Option to apply formatting to.
     * @param builder The Builder that specifies the various formatting options.
     */
    private OptionFormatter(final Option option, final Builder builder) {
        this.optionalDelimiters = builder.optionalDelimiters;
        this.argNameDelimiters = builder.argNameDelimiters;
        this.defaultArgName = builder.defaultArgName;
        this.optPrefix = builder.optPrefix;
        this.longOptPrefix = builder.longOptPrefix;
        this.optSeparator = builder.optSeparator;
        this.optionGroupSeparator = builder.optionGroupSeparator;
        this.comparator = builder.comparator;
        this.deprecatedFormatFunction = builder.deprecatedFormatFunction;
        this.syntaxFormatFunction = builder.syntaxFormatFunction;
        this.option = option;
    }

    /**
     * Gets the required flag from the enclosed {@link Option}.
     * @return The required flag from the enclosed {@link Option}.
     */
    public boolean isRequired() {
        return option.isRequired();
    }

    /**
     * Gets the Opt from the @{link Option} with the associate prefix.
     * @return The Opt from the @{link Option} with the associate prefix or an empty string.
     */
    public String getOpt() {
        return Util.isEmpty(option.getOpt()) ? "" : optPrefix + option.getOpt();
    }

    /**
     * Gets the long Opt from the @{link Option} with the associate prefix.
     * @return The long Opt from the @{link Option} with the associate prefix or an empty string.
     */
    public String getLongOpt() {
        return Util.isEmpty(option.getLongOpt()) ? "" : longOptPrefix + option.getLongOpt();
    }

    /**
     * Gets both options separated by the specified option separator.  Correctly handles the case where
     * one option is not specified.
     * @return The one or both of the short and/or long Opt with the associate prefixes.
     */
    public String getBothOpt() {
        String lOpt = getLongOpt();

        StringBuilder sb = new StringBuilder(getOpt());
        if (sb.length() > 0 && lOpt.length() > 0) {
            sb.append(",");
        }
        sb.append(getLongOpt());
        return sb.length() == 0 ? "" : sb.toString();
    }

    /**
     * Gets the argument name wrapped in the argument name delimiters.
     * <ul>
     *     <li>If option has no arguments an empty string is returned</li>
     *     <li>If the argument name is not set the default argument name is used.</li>
     * </ul>
     * @return The argument name wrapped in the argument name delimiters or an empty string.
     */
    public String getArgName() {
        return option.hasArg() ? argNameDelimiters[0] + Util.defaultValue(option.getArgName(), defaultArgName) + argNameDelimiters[1] : "";
    }

    /**
     * Wraps the provided text in the optional delimiters.
     * @param text the text to wrap.
     * @return The text wrapped in the optional delimiters or an eppty string of the text is null or an empty string.
     */
    public String asOptional(final String text) {
        if (Util.isEmpty(text)) {
            return "";
        }
        return optionalDelimiters[0] + text + optionalDelimiters[1];
    }

    /**
     * Gets the description from the option.
     * @return The Description from the option or an empty string is no description was provided.
     */
    public String getDescription() {
        return Util.defaultValue(option.getDescription(), "");
    }

    /**
     * Gets the deprecated message for the option.
     * @return the deprecated message for the option or an empty string if no deprecatedFormatFunction was specified.
     */
    public String getDeprecated() {
        if (deprecatedFormatFunction == null) {
            return "";
        }
        return option.isDeprecated() ? deprecatedFormatFunction.apply(option) : "";
    }

    /**
     * Gets the "since" value from the Option.
     * @return The since valeu from the option or "--" if no since value was set.
     */
    public String getSince() {
        return Util.defaultValue(option.getSince(), "--");
    }

    /**
     * Gets the syntax format for this option.
     * @return the syntax format for this option as specified by the syntaxFormatFunction.
     */
    public String asSyntaxOption() {
        return syntaxFormatFunction.apply(this, this.isRequired());
    }

    /**
     * Returns a sorted list of Options if the comparator has been set.
     * <p>
     *     If the comparator has not been set a list of the options in the
     *     original order are returned.
     * </p>
     * @param options the options to sort.
     * @return A sorted list of Options.
     */
    public List<Option> sortedOptions(final Options options) {
        return sortedOptions(options.getOptions());
    }

    /**
     * Returns a sorted list of Options if the comparator has been set.
     * <p>
     *     If the comparator has not been set a list of the options in the
     *     original order are returned.
     * </p>
     * @param options the options to sort.
     * @return A sorted list of Options if the comparator has been set.
     */
    public List<Option> sortedOptions(final Iterable<Option> options) {
        return sortedOptions(comparator, options);
    }

    /**
     * Sorts the options if the comparator has been set.
     * <p>
     *     If the comparator has not been set a list of the options in the
     *     original order are returned.
     * </p>
     * @param comparator The comparator to sort with.
     * @param options the options to sort.
     * @return a new List of the options.
     */
    private static List<Option> sortedOptions(final Comparator<Option> comparator, final Iterable<Option> options) {
        final List<Option> optList = new ArrayList<>();
        options.forEach(optList::add);
        if (comparator != null) {
            optList.sort(comparator);
        }
        return optList;
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param builder The Builder to create OptionFormatters to format the options.
     * @param options The {@link Options} to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     */
    public static String asSyntaxOptions(final OptionFormatter.Builder builder, final Options options) {
        return asSyntaxOptions(builder, options.getOptions(), options::getOptionGroup);
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param builder The Builder to create OptionFormatters to format the options.
     * @param options The collection of {@link Option} instances to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     */
    public static String asSyntaxOptions(final OptionFormatter.Builder builder, final Iterable<Option> options) {
        return asSyntaxOptions(builder, options, o -> null);
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param builder The Builder to create OptionFormatters to format the options.
     * @param options The options to create the string representation for.
     * @param lookup a function to determine if the Option is part of an OptionGroup that has already been processed.
     * @return the string representation of the options as used in the syntax display.
     */
    private static String asSyntaxOptions(final OptionFormatter.Builder builder, final Iterable<Option> options,
                                          final Function<Option, OptionGroup> lookup) {
        // list of groups that have been processed.
        final Collection<OptionGroup> processedGroups = new ArrayList<>();
        final List<Option> optList = sortedOptions(builder.comparator, options);
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
                    buff.append(pfx).append(OptionFormatter.asSyntaxOptions(builder, group));
                    pfx = " ";
                }
                // otherwise the option was displayed in the group previously so ignore it.
            }
            // if the Option is not part of an OptionGroup
            else {
                buff.append(pfx).append(builder.build(option).asSyntaxOption());
                pfx = " ";
            }
        }
        return buff.toString();
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param builder The Builder to create OptionFormatters to format the options.
     * @param group The OptionGroup to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     */
    public static String asSyntaxOptions(final OptionFormatter.Builder builder, final OptionGroup group) {
        StringBuilder buff = new StringBuilder();
        final List<Option> optList = sortedOptions(builder.comparator, group.getOptions());
        OptionFormatter formatter = null;
        // for each option in the OptionGroup
        Iterator<Option> iter = optList.iterator();
        while (iter.hasNext()) {
            formatter = builder.build(iter.next());
            // whether the option is required or not is handled at group level
            buff.append(formatter.syntaxFormatFunction.apply(formatter, true));

            if (iter.hasNext()) {
                buff.append(formatter.optionGroupSeparator);
            }
        }
        if (formatter != null) {
            return group.isRequired() ? buff.toString() : formatter.asOptional(buff.toString());
        }
        return ""; // there were no entries in the group.
    }
}
