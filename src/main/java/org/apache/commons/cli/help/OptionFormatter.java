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


import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.String.format;

/**
 * The base class for an Option formatting.  This class implements the default formatting.  Alternative
 * formatting can be implemented by overriding some methods in this class.
 */
public final class OptionFormatter {

    public static final String[] DEFAULT_OPTIONAL_DELIMITERS = {"[", "]"};
    public static final String[] DEFAULT_ARG_DELIMITERS = {"<", ">"};
    public static final String DEFAULT_ARG_NAME = "arg";

    /**
     * The default A function to convert an option into a string with the description and a deprecated warning.
     */
    public static final Function<Option, String> DEFAULT_DEPRECATED_FORMAT = o -> "[Deprecated] " + Util.defaultValue(o.getDescription(), "");

    /**
     * Function to produce the option syntax.
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
     * The string to display at the beginning of the usage statement
     */
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

    /**
     * Default prefix for shortOpts
     */
    public static final String DEFAULT_OPT_PREFIX = "-";

    /**
     * Default prefix for long Option
     */
    public static final String DEFAULT_LONG_OPT_PREFIX = "--";

    public static final String DEFAULT_OPT_SEPARATOR = " ";

    public static final Comparator<Option> DEFAULT_COMPARATOR = (opt1, opt2) -> opt1.getKey().compareToIgnoreCase(opt2.getKey());

    public static final String DEFAULT_OPTION_GROUP_SEPARATOR = " | ";

    private final String[] argDelimiters;
    private final String argName;
    private final Comparator<Option> comparator;
    private final Function<Option, String> deprecatedFormatFunction;
    private final String longOptPrefix;
    private final String optPrefix;
    private final String optSeparator;
    private final String optionGroupSeparator;
    private final String[] optionalDelimiters;
    private final BiFunction<OptionFormatter, Boolean, String> syntaxFormatFunction;
    private final Option option;

    public static OptionFormatter from(Option option) {
        return new OptionFormatter.Builder().build(option);
    }

    public final static class Builder  {
        private final String[] argDelimiters;
        private String argName;
        private Comparator<Option> comparator;
        private Function<Option, String> deprecatedFormatFunction;
        private String longOptPrefix;
        private String optPrefix;
        private String optSeparator;
        private String optionGroupSeparator;
        private final String[] optionalDelimiters;
        private BiFunction<OptionFormatter, Boolean, String> syntaxFormatFunction;

        public Builder() {
            argDelimiters = Arrays.copyOf(DEFAULT_ARG_DELIMITERS, 2);
            argName = DEFAULT_ARG_NAME;
            comparator = DEFAULT_COMPARATOR;
            deprecatedFormatFunction = null;
            longOptPrefix = DEFAULT_LONG_OPT_PREFIX;
            optPrefix = DEFAULT_OPT_PREFIX;
            optSeparator = DEFAULT_OPT_SEPARATOR;
            optionGroupSeparator = DEFAULT_OPTION_GROUP_SEPARATOR;
            optionalDelimiters = Arrays.copyOf(DEFAULT_OPTIONAL_DELIMITERS, 2);
            syntaxFormatFunction = DEFAULT_SYNTAX_FORMAT;
        }

        public Builder(OptionFormatter of) {
            optionalDelimiters = Arrays.copyOf(of.optionalDelimiters, 2);
            argDelimiters = Arrays.copyOf(of.argDelimiters, 2);
            argName = of.argName;
            optPrefix = of.optPrefix;
            longOptPrefix = of.longOptPrefix;
            optSeparator = of.optSeparator;
            optionGroupSeparator = of.optionGroupSeparator;
            deprecatedFormatFunction = of.deprecatedFormatFunction;
            syntaxFormatFunction = of.syntaxFormatFunction;
            comparator = of.comparator;
        }

        public OptionFormatter build(Option option) {
            return new OptionFormatter(option, this);
        }

        /**
         * Sets the 'argName'.
         *
         * @param showSince {@code true} if the "since" value should be shown asa separate column.
         */
        public Builder setShowSince(final boolean showSince) {
            return this;
        }

        /**
         * Sets the 'argName'.
         *
         * @param name the new value of 'argName'
         */
        public Builder setArgName(final String name) {
            this.argName = Util.defaultValue(name, DEFAULT_ARG_NAME);
            return this;
        }

        public String asArgName(String argName) {
            return argDelimiters[0]+Util.defaultValue(argName, "")+argDelimiters[1];
        }

        /**
         * Sets the 'longOptPrefix'.
         *
         * @param prefix the new value of 'longOptPrefix'
         */
        public Builder setLongOptPrefix(final String prefix) {
            this.longOptPrefix = Util.defaultValue(prefix, "");
            return this;
        }

        /**
         * Sets the separator displayed between a long option and its value. Ensure that the separator specified is supported by
         * the parser used, typically ' ' or '='.
         *
         * @param optSeparator the separator, typically ' ' or '='.
         * @since 1.3
         */
        public Builder setOptSeparator(final String optSeparator) {
            this.optSeparator = Util.defaultValue(optSeparator, "");
            return this;
        }

        /**
         * Sets the separator displayed between a options in an option group listing.
         *
         * @param optionGroupSeparator the separator.
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
         * @param comparator the {@link Comparator} to use for sorting the options
         * @since 1.9
         */
        public Builder setComparator(final Comparator<Option> comparator) {
            this.comparator = comparator;
            return this;
        }

        public Builder setOptionalDelimiters(String begin, String end) {
            this.optionalDelimiters[0] = Util.defaultValue(begin, "");
            this.optionalDelimiters[1] = Util.defaultValue(end, "");
            return this;
        }

        public Builder setArgDelimiters(String begin, String end) {
            this.argDelimiters[0] = Util.defaultValue(begin, "");
            this.argDelimiters[1] = Util.defaultValue(end, "");
            return this;
        }

        public Builder setOptPrefix(String optPrefix) {
            this.optPrefix = Util.defaultValue(optPrefix, "");
            return this;
        }

        public Builder setDeprecatedFormatFunction(Function<Option, String> deprecatedFormatFunction) {
            this.deprecatedFormatFunction = deprecatedFormatFunction;
            return this;
        }

        public Builder setSyntaxFormatFunction(BiFunction<OptionFormatter, Boolean, String> syntaxFormatFunction) {
            this.syntaxFormatFunction = syntaxFormatFunction == null ? DEFAULT_SYNTAX_FORMAT : syntaxFormatFunction;
            return this;
        }
    }

    private OptionFormatter(final Option option, final Builder builder) {
        this.optionalDelimiters = builder.optionalDelimiters;
        this.argDelimiters = builder.argDelimiters;
        this.argName = builder.argName;
        this.optPrefix = builder.optPrefix;
        this.longOptPrefix = builder.longOptPrefix;
        this.optSeparator = builder.optSeparator;
        this.optionGroupSeparator = builder.optionGroupSeparator;
        this.comparator = builder.comparator;
        this.deprecatedFormatFunction = builder.deprecatedFormatFunction;
        this.syntaxFormatFunction = builder.syntaxFormatFunction;
        this.option = option;
    }

    public boolean isRequired() {
        return option.isRequired();
    }

    public String getOpt() {
        return Util.isEmpty(option.getOpt()) ? "" : optPrefix+option.getOpt();
    }

    public String getLongOpt() {
        return Util.isEmpty(option.getLongOpt()) ? "" : longOptPrefix+option.getLongOpt();
    }

    public String getBothOpt() {
        String lOpt = getLongOpt();

        StringBuilder sb = new StringBuilder(getOpt());
        if (sb.length() > 0 && lOpt.length() > 0) {
            sb.append(",");
        }
        sb.append(getLongOpt());
        return sb.length() == 0 ? "" : sb.toString();
    }

    public String getArgName() {
        return option.hasArg() ? argDelimiters[0]+Util.defaultValue(option.getArgName(), argName)+argDelimiters[1] : "";
    }

    public String asOptional(String text) {
        if (Util.isEmpty(text)) {
            return "";
        }
        return optionalDelimiters[0]+text+optionalDelimiters[1];
    }

    public String getDescription() {
        return Util.defaultValue(option.getDescription(), "");
    }

    public String getDeprecated() {
        if (deprecatedFormatFunction == null) {
            return "";
        }
        return option.isDeprecated() ? deprecatedFormatFunction.apply(option) : "";
    }

    public String getSince() {
        return Util.defaultValue(option.getSince(), "--");
    }

    /**
     * Appends the usage clause for an Option to a StringBuffer.
     *
     */
    public String asSyntaxOption() throws IOException {
        return syntaxFormatFunction.apply(this, this.isRequired());
    }


    /**
     * Returns a sorted list of Options if the comparator has been set.
     * @param options the options to sort.
     * @return A sorted list of Options.
     */
    public List<Option> sortedOptions(Options options) {
        return sortedOptions(options.getOptions());
    }

    /**
     * Returns a sorted list of Options if the comparator has been set.
     * @param options the options to sort.
     * @return A sorted list of Options.
     */
    public List<Option> sortedOptions(Iterable<Option> options) {
        return sortedOptions(comparator, options);
    }

    private static List<Option> sortedOptions(Comparator<Option> comparator, Iterable<Option> options) {
        final List<Option> optList = new ArrayList<>();
        options.forEach(optList::add);
        if (comparator != null) {
            optList.sort(comparator);
        }
        return optList;
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param options The options to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     * @throws IOException on output error.
     */
    public static String asSyntaxOptions(OptionFormatter.Builder builder, Options options) throws IOException {
        return asSyntaxOptions(builder, options.getOptions(), options::getOptionGroup);
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param options The options to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     * @throws IOException on output error.
     */
    public static String asSyntaxOptions(OptionFormatter.Builder builder, Iterable<Option> options) throws IOException {
        return asSyntaxOptions(builder, options, o -> null);
    }

    /**
     * Return the string representation of the options as used in the syntax display.
     * @param options The options to create the string representation for.
     * @return the string representation of the options as used in the syntax display.
     * @throws IOException on output error.
     */
    private static String asSyntaxOptions(OptionFormatter.Builder builder, Iterable<Option> options, Function<Option,OptionGroup> lookup) throws IOException {
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
                // otherwise the option was displayed in the group
                // previously so ignore it.
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
     * Appends the usage clause for an OptionGroup to a StringBuffer. The clause is wrapped in square brackets if the group
     * is required. The display of the options is handled by appendOption
     *
     * @param group the group to append
     */
    public static String asSyntaxOptions(OptionFormatter.Builder builder, final OptionGroup group) throws IOException {
        StringBuilder buff = new StringBuilder();
        final List<Option> optList = sortedOptions(builder.comparator, group.getOptions());
        OptionFormatter formatter = null;
        // for each option in the OptionGroup
        for (final Iterator<Option> it = optList.iterator(); it.hasNext(); ) {
            formatter = builder.build(it.next());
            // whether the option is required or not is handled at group level
            buff.append(formatter.syntaxFormatFunction.apply(formatter, true));

            if (it.hasNext()) {
                buff.append(formatter.optionGroupSeparator);
            }
        }
        if (formatter != null) {
            return group.isRequired() ? buff.toString() : formatter.asOptional(buff.toString());
        }
        return ""; // there were no entries in the group.
    }
}
