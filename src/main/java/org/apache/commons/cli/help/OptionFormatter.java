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

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.cli.DeprecatedAttributes;
import org.apache.commons.cli.Option;

/**
 * The definition of how to display Option attributes.
 *
 * @since 1.10.0
 */
public final class OptionFormatter {

    /**
     * Builds instances of {@link OptionFormatter}.
     */
    public static final class Builder implements Supplier<OptionFormatter> {

        /** The argument name delimiters */
        private final String[] argNameDelimiters;

        /** The default argument name */
        private String defaultArgName;

        /** The function to create the deprecated message for an option */
        private Function<Option, String> deprecatedFormatFunction;

        /** The long option prefix */
        private String longOptPrefix;

        /** The option prefix */
        private String optPrefix;

        /** The separator between long and short options */
        private String optSeparator;

        /** The separator between the opt and/or longOpt and the argument name */
        private String optArgSeparator;

        /** The delimiters surrounding optional {@link Option} instances. */
        private final String[] optionalDelimiters;

        /** A function to convert the {@link OptionFormatter} into an entry in the syntax description. */
        private BiFunction<OptionFormatter, Boolean, String> syntaxFormatFunction;

        /**
         * Default constructor. Uses the defaults specified in {@link OptionFormatter}.
         */
        private Builder() {
            argNameDelimiters = Arrays.copyOf(DEFAULT_ARG_NAME_DELIMITERS, 2);
            defaultArgName = DEFAULT_ARG_NAME;
            deprecatedFormatFunction = NO_DEPRECATED_FORMAT;
            longOptPrefix = DEFAULT_LONG_OPT_PREFIX;
            optPrefix = DEFAULT_OPT_PREFIX;
            optSeparator = DEFAULT_OPT_SEPARATOR;
            optArgSeparator = DEFAULT_OPT_ARG_SEPARATOR;
            optionalDelimiters = Arrays.copyOf(DEFAULT_OPTIONAL_DELIMITERS, 2);
        }

        /**
         * Constructor that takes the arguments from the supplied {@link OptionFormatter}
         *
         * @param optionFormatter The option formatter to provide values for the builder.
         */
        public Builder(final OptionFormatter optionFormatter) {
            optionalDelimiters = Arrays.copyOf(optionFormatter.optionalDelimiters, 2);
            argNameDelimiters = Arrays.copyOf(optionFormatter.argNameDelimiters, 2);
            defaultArgName = optionFormatter.defaultArgName;
            optPrefix = optionFormatter.optPrefix;
            longOptPrefix = optionFormatter.longOptPrefix;
            optSeparator = optionFormatter.optSeparator;
            deprecatedFormatFunction = optionFormatter.deprecatedFormatFunction;
            syntaxFormatFunction = optionFormatter.syntaxFormatFunction;
        }

        /**
         * Build an OptionFormatter to format the specified option.
         *
         * @param option The Option to format.
         * @return An OptionFormatter to format the specified option.
         */
        public OptionFormatter build(final Option option) {
            return new OptionFormatter(option, this);
        }

        @Override
        public OptionFormatter get() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * Specifies the starting and ending argument name delimiters for {@link Option} instances.
         *
         * @param begin the beginning delimiter.
         * @param end   the ending delimiter.
         * @return {@code this} instance.
         */
        public Builder setArgumentNameDelimiters(final String begin, final String end) {
            this.argNameDelimiters[0] = Util.defaultValue(begin, "");
            this.argNameDelimiters[1] = Util.defaultValue(end, "");
            return this;
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
         * Specifies the function to construct the deprecated massage for the Option. Should include the description text if desired.
         *
         * @param deprecatedFormatFunction the function to specify the deprecated message for the option.
         * @return {@code this} instance.
         */
        public Builder setDeprecatedFormatFunction(final Function<Option, String> deprecatedFormatFunction) {
            this.deprecatedFormatFunction = deprecatedFormatFunction;
            return this;
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
         * Sets the separator displayed between a options and the argument name. Typically ' ' or '='.
         *
         * @param optArgSeparator the separator.
         * @return this
         * @since 1.3
         */
        public Builder setOptArgSeparator(final String optArgSeparator) {
            this.optArgSeparator = Util.defaultValue(optArgSeparator, "");
            return this;
        }

        /**
         * Specifies the starting and ending delimiters for optional {@link Option} instances.
         *
         * @param begin the beginning delimiter.
         * @param end   the ending delimiter.
         * @return {@code this} instance.
         */
        public Builder setOptionalDelimiters(final String begin, final String end) {
            this.optionalDelimiters[0] = Util.defaultValue(begin, "");
            this.optionalDelimiters[1] = Util.defaultValue(end, "");
            return this;
        }

        /**
         * Specifies the short option prefix.
         *
         * @param optPrefix the prefix for short options.
         * @return {@code this} instance.
         */
        public Builder setOptPrefix(final String optPrefix) {
            this.optPrefix = Util.defaultValue(optPrefix, "");
            return this;
        }

        /**
         * Sets the separator displayed between a long option and short options. Typically ',' or ' '.
         *
         * @param optSeparator the separator.
         * @return this
         * @since 1.3
         */
        public Builder setOptSeparator(final String optSeparator) {
            this.optSeparator = Util.defaultValue(optSeparator, "");
            return this;
        }

        /**
         * Specifies the function to convert an {@link OptionFormatter} into the syntax format for the option.
         *
         * @param syntaxFormatFunction The function to convert an {@link OptionFormatter} into the syntax format for the option.
         * @return this
         */
        public Builder setSyntaxFormatFunction(final BiFunction<OptionFormatter, Boolean, String> syntaxFormatFunction) {
            this.syntaxFormatFunction = syntaxFormatFunction;
            return this;
        }

        /**
         * A helper method to format any string as an argument name based on this builder.
         *
         * @param argName the name of the argument.
         * @return the formatted argument.
         */
        public String toArgName(final String argName) {
            return argNameDelimiters[0] + Util.defaultValue(argName, "") + argNameDelimiters[1];
        }
    }

    /** The default delimiters for optional arguments */
    private static final String[] DEFAULT_OPTIONAL_DELIMITERS = { "[", "]" };

    /** The default delimiters for an argument name */
    private static final String[] DEFAULT_ARG_NAME_DELIMITERS = { "<", ">" };

    /**
     * The default argument name: {@value}.
     */
    public static final String DEFAULT_ARG_NAME = "arg";

    /**
     * A function to display a deprecated option with the "[Deprecated]" prefix.
     */
    public static final Function<Option, String> SIMPLE_DEPRECATED_FORMAT = o -> "[Deprecated] " + Util.defaultValue(o.getDescription(), "");

    /**
     * A function to display a deprecated option with a "Deprecated" prefix that displays all deprecation information.
     */
    public static final Function<Option, String> COMPLEX_DEPRECATED_FORMAT = o -> {
        final StringBuilder sb = new StringBuilder("[Deprecated");
        final DeprecatedAttributes attr = o.getDeprecated();
        if (attr.isForRemoval()) {
            sb.append(" for removal");
        }
        if (!Util.isEmpty(attr.getSince())) {
            sb.append(" since ").append(attr.getSince());
        }
        if (!Util.isEmpty(attr.getDescription())) {
            sb.append(". ").append(attr.getDescription());
        }
        sb.append("]");
        if (!Util.isEmpty(o.getDescription())) {
            sb.append(" ").append(o.getDescription());
        }
        return sb.toString();
    };

    /**
     * A function to display a deprecated option with the "[Deprecated]" prefix.
     */
    public static final Function<Option, String> NO_DEPRECATED_FORMAT = o -> Util.defaultValue(o.getDescription(), "");

    /**
     * The string to display at the beginning of the usage statement: {@value}.
     */
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

    /**
     * Default prefix for short options: {@value}.
     */
    public static final String DEFAULT_OPT_PREFIX = "-";

    /**
     * Default prefix for long options: {@value}.
     */
    public static final String DEFAULT_LONG_OPT_PREFIX = "--";

    /**
     * The default separator between options: {@value}.
     */
    public static final String DEFAULT_OPT_SEPARATOR = ", ";

    /**
     * The default separator between the opt and/or longOpt and the argument name: {@value}.
     */
    public static final String DEFAULT_OPT_ARG_SEPARATOR = " ";

    /**
     * Creates a new builder.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Construct the {@link OptionFormatter} from an {@link Option} using the default {@link OptionFormatter.Builder}.
     *
     * @param option the option to format.
     * @return an OptionFormatter for the specified @{code option}.
     */
    public static OptionFormatter from(final Option option) {
        return new Builder().build(option);
    }

    /**
     * The delimiters around argument names.
     */
    private final String[] argNameDelimiters;

    /** The default argument name. */
    private final String defaultArgName;

    /** The function to display the deprecated option message. */
    private final Function<Option, String> deprecatedFormatFunction;

    /** The prefix for the long option text. */
    private final String longOptPrefix;

    /** The prefix for the short option text. */
    private final String optPrefix;

    /** The separator between the options. */
    private final String optSeparator;

    /** the separator between the opt and/or longOpt and the argument name. */
    private final String optArgSeparator;

    /** The delimiters for optional {@link Option}s. */
    private final String[] optionalDelimiters;

    /** The method to convert an Option formatter into a syntax notation. */
    private final BiFunction<OptionFormatter, Boolean, String> syntaxFormatFunction;

    /** The {@link Option} being formatted. */
    private final Option option;

    /**
     * An OptionFormatter applies formatting options to various {@link Option} attributes for textual display.
     *
     * @param option  the Option to apply formatting to.
     * @param builder The Builder that specifies the various formatting options.
     */
    private OptionFormatter(final Option option, final Builder builder) {
        this.optionalDelimiters = builder.optionalDelimiters;
        this.argNameDelimiters = builder.argNameDelimiters;
        this.defaultArgName = builder.defaultArgName;
        this.optPrefix = builder.optPrefix;
        this.longOptPrefix = builder.longOptPrefix;
        this.optSeparator = builder.optSeparator;
        this.optArgSeparator = builder.optArgSeparator;
        this.deprecatedFormatFunction = builder.deprecatedFormatFunction;
        this.option = option;
        this.syntaxFormatFunction = builder.syntaxFormatFunction != null ? builder.syntaxFormatFunction : (o, required) -> {
            final StringBuilder buff = new StringBuilder();
            final String argName = o.getArgName();
            buff.append(Util.defaultValue(o.getOpt(), o.getLongOpt()));
            if (!Util.isEmpty(argName)) {
                buff.append(optArgSeparator).append(argName);
            }
            final boolean requiredFlg = required == null ? o.isRequired() : required;
            return requiredFlg ? buff.toString() : o.toOptional(buff.toString());
        };
    }

    /**
     * Gets the argument name wrapped in the argument name delimiters.
     * <ul>
     * <li>If option has no arguments an empty string is returned</li>
     * <li>If the argument name is not set the default argument name is used.</li>
     * </ul>
     *
     * @return The argument name wrapped in the argument name delimiters or an empty string.
     */
    public String getArgName() {
        return option.hasArg() ? argNameDelimiters[0] + Util.defaultValue(option.getArgName(), defaultArgName) + argNameDelimiters[1] : "";
    }

    /**
     * Gets both options separated by the specified option separator. Correctly handles the case where one option is not specified.
     *
     * @return The one or both of the short and/or long Opt with the associate prefixes.
     */
    public String getBothOpt() {
        final String lOpt = getLongOpt();

        final StringBuilder sb = new StringBuilder(getOpt());
        if (sb.length() > 0 && !Util.isEmpty(lOpt)) {
            sb.append(optSeparator);
        }
        // sb will not be empty as Option requries at least one of opt or longOpt.
        return sb.append(getLongOpt()).toString();
    }

    /**
     * Gets the description for the option. This will include any deprecation notices if the deprecated format function has been set.
     *
     * @return The Description from the option or an empty string is no description was provided and the option is not deprecated.
     */
    public String getDescription() {
        return option.isDeprecated() ? deprecatedFormatFunction.apply(option) : Util.defaultValue(option.getDescription(), "");
    }

    /**
     * Gets the long Opt from the @{link Option} with the associate prefix.
     *
     * @return The long Opt from the @{link Option} with the associate prefix or an empty string.
     */
    public String getLongOpt() {
        return Util.isEmpty(option.getLongOpt()) ? "" : longOptPrefix + option.getLongOpt();
    }

    /**
     * Gets the Opt from the @{link Option} with the associate prefix.
     *
     * @return The Opt from the @{link Option} with the associate prefix or an empty string.
     */
    public String getOpt() {
        return Util.isEmpty(option.getOpt()) ? "" : optPrefix + option.getOpt();
    }

    /**
     * Gets the "since" value from the Option.
     *
     * @return The since valeu from the option or "--" if no since value was set.
     */
    public String getSince() {
        return Util.defaultValue(option.getSince(), DEFAULT_LONG_OPT_PREFIX);
    }

    /**
     * Gets the required flag from the enclosed {@link Option}.
     *
     * @return The required flag from the enclosed {@link Option}.
     */
    public boolean isRequired() {
        return option.isRequired();
    }

    /**
     * Wraps the provided text in the optional delimiters.
     *
     * @param text the text to wrap.
     * @return The text wrapped in the optional delimiters or an eppty string of the text is null or an empty string.
     */
    public String toOptional(final String text) {
        if (Util.isEmpty(text)) {
            return "";
        }
        return optionalDelimiters[0] + text + optionalDelimiters[1];
    }

    /**
     * Gets the syntax format for this option.
     *
     * @return the syntax format for this option as specified by the syntaxFormatFunction.
     */
    public String toSyntaxOption() {
        return toSyntaxOption(isRequired());
    }

    /**
     * Gets the syntax format for this option.
     *
     * @param isRequired if {@code true} the options is printed as a required option, otherwise it is optional.
     * @return the syntax format for this option as specified by the syntaxFormatFunction.
     */
    public String toSyntaxOption(final boolean isRequired) {
        return syntaxFormatFunction.apply(this, isRequired);
    }
}
