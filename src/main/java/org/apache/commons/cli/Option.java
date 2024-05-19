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

package org.apache.commons.cli;

import static org.apache.commons.cli.Util.EMPTY_STRING_ARRAY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Describes a single command-line option. It maintains information regarding the short-name of the option, the long-name, if any exists, a flag indicating if
 * an argument is required for this option, and a self-documenting description of the option.
 * <p>
 * An Option is not created independently, but is created through an instance of {@link Options}. An Option is required to have at least a short or a long-name.
 * </p>
 * <p>
 * <b>Note:</b> once an {@link Option} has been added to an instance of {@link Options}, its required flag cannot be changed.
 * </p>
 *
 * @see org.apache.commons.cli.Options
 * @see org.apache.commons.cli.CommandLine
 */
public class Option implements Cloneable, Serializable {

    /**
     * Builds {@code Option} instances using descriptive methods.
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
     * Option option = Option.builder("a").required(true).longOpt("arg-name").build();
     * </pre>
     *
     * @since 1.3
     */
    public static final class Builder {

        /** The default type. */
        private static final Class<String> DEFAULT_TYPE = String.class;

        /**
         * Returns the input Class or the default type (String) if null.
         *
         * @param type the candidate Class.
         * @return the input Class or the default type (String) if null.
         */
        private static Class<?> toType(final Class<?> type) {
            return type != null ? type : DEFAULT_TYPE;
        }

        /** The number of argument values this option can have. */
        private int argCount = UNINITIALIZED;

        /** The name of the argument for this option. */
        private String argName;

        /** The converter to convert to type. **/
        private Converter<?, ?> converter;

        /** Specifies whether this option is deprecated. */
        private DeprecatedAttributes deprecated;

        /** Description of the option. */
        private String description;

        /** The long representation of the option. */
        private String longOption;

        /** The name of the option. */
        private String option;

        /** Specifies whether the argument value of this Option is optional. */
        private boolean optionalArg;

        /** Specifies whether this option is required to be present. */
        private boolean required;

        /** The type of this Option. */
        private Class<?> type = DEFAULT_TYPE;

        /** The character that is the value separator. */
        private char valueSeparator;

        /**
         * Constructs a new {@code Builder} with the minimum required parameters for an {@code Option} instance.
         *
         * @param option short representation of the option.
         * @throws IllegalArgumentException if there are any non valid Option characters in {@code opt}.
         */
        private Builder(final String option) throws IllegalArgumentException {
            option(option);
        }

        /**
         * Sets the display name for the argument value.
         *
         * @param argName the display name for the argument value.
         * @return this builder, to allow method chaining.
         */
        public Builder argName(final String argName) {
            this.argName = argName;
            return this;
        }

        /**
         * Constructs an Option with the values declared by this {@link Builder}.
         *
         * @return the new {@link Option}.
         * @throws IllegalArgumentException if neither {@code opt} or {@code longOpt} has been set.
         */
        public Option build() {
            if (option == null && longOption == null) {
                throw new IllegalArgumentException("Either opt or longOpt must be specified");
            }
            return new Option(this);
        }

        /**
         * Sets the converter for the option.
         *
         * <p>
         * Note: see {@link TypeHandler} for serialization discussion.
         * </p>
         *
         * @param converter the Converter to use.
         * @return this builder, to allow method chaining.
         * @since 1.7.0
         */
        public Builder converter(final Converter<?, ?> converter) {
            this.converter = converter;
            return this;
        }

        /**
         * Marks this Option as deprecated.
         *
         * @return this builder.
         * @since 1.7.0
         */
        public Builder deprecated() {
            return deprecated(DeprecatedAttributes.DEFAULT);
        }

        /**
         * Sets whether the Option is deprecated.
         *
         * @param deprecated specifies whether the Option is deprecated.
         * @return this builder.
         * @since 1.7.0
         */
        public Builder deprecated(final DeprecatedAttributes deprecated) {
            this.deprecated = deprecated;
            return this;
        }

        /**
         * Sets the description for this option.
         *
         * @param description the description of the option.
         * @return this builder, to allow method chaining.
         */
        public Builder desc(final String description) {
            this.description = description;
            return this;
        }

        /**
         * Tests whether the Option will require an argument.
         *
         * @return this builder, to allow method chaining.
         */
        public Builder hasArg() {
            return hasArg(true);
        }

        /**
         * Tests whether the Option has an argument or not.
         *
         * @param hasArg specifies whether the Option takes an argument or not.
         * @return this builder, to allow method chaining.
         */
        public Builder hasArg(final boolean hasArg) {
            // set to UNINITIALIZED when no arg is specified to be compatible with OptionBuilder
            argCount = hasArg ? 1 : UNINITIALIZED;
            return this;
        }

        /**
         * Tests whether the Option can have unlimited argument values.
         *
         * @return this builder.
         */
        public Builder hasArgs() {
            argCount = UNLIMITED_VALUES;
            return this;
        }

        /**
         * Sets the long name of the Option.
         *
         * @param longOpt the long name of the Option
         * @return this builder.
         */
        public Builder longOpt(final String longOpt) {
            this.longOption = longOpt;
            return this;
        }

        /**
         * Sets the number of argument values the Option can take.
         *
         * @param argCount the number of argument values
         * @return this builder.
         */
        public Builder numberOfArgs(final int argCount) {
            this.argCount = argCount;
            return this;
        }

        /**
         * Sets the name of the Option.
         *
         * @param option the name of the Option.
         * @return this builder.
         * @throws IllegalArgumentException if there are any non valid Option characters in {@code opt}.
         * @since 1.5.0
         */
        public Builder option(final String option) throws IllegalArgumentException {
            this.option = OptionValidator.validate(option);
            return this;
        }

        /**
         * Sets whether the Option can have an optional argument.
         *
         * @param optionalArg specifies whether the Option can have an optional argument.
         * @return this builder.
         */
        public Builder optionalArg(final boolean optionalArg) {
            if (optionalArg && this.argCount == UNINITIALIZED) {
                this.argCount = 1;
            }
            this.optionalArg = optionalArg;
            return this;
        }

        /**
         * Marks this Option as required.
         *
         * @return this builder.
         */
        public Builder required() {
            return required(true);
        }

        /**
         * Sets whether the Option is required.
         *
         * @param required specifies whether the Option is required.
         * @return this builder.
         */
        public Builder required(final boolean required) {
            this.required = required;
            return this;
        }

        /**
         * Sets the type of the Option.
         *
         * @param type the type of the Option.
         * @return this builder.
         */
        public Builder type(final Class<?> type) {
            this.type = toType(type);
            return this;
        }

        /**
         * The Option will use '=' as a means to separate argument value.
         *
         * @return this builder.
         */
        public Builder valueSeparator() {
            return valueSeparator(Char.EQUAL);
        }

        /**
         * The Option will use {@code sep} as a means to separate argument values.
         * <p>
         * <b>Example:</b>
         * </p>
         *
         * <pre>
         * Option opt = Option.builder("D").hasArgs().valueSeparator('=').build();
         * Options options = new Options();
         * options.addOption(opt);
         * String[] args = { "-Dkey=value" };
         * CommandLineParser parser = new DefaultParser();
         * CommandLine line = parser.parse(options, args);
         * String propertyName = line.getOptionValues("D")[0]; // will be "key"
         * String propertyValue = line.getOptionValues("D")[1]; // will be "value"
         * </pre>
         *
         * @param valueSeparator The value separator.
         * @return this builder.
         */
        public Builder valueSeparator(final char valueSeparator) {
            this.valueSeparator = valueSeparator;
            return this;
        }

    }

    /** Empty array. */
    static final Option[] EMPTY_ARRAY = {};

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Specifies the number of argument values has not been specified. */
    public static final int UNINITIALIZED = -1;

    /** Specifies the number of argument values is infinite. */
    public static final int UNLIMITED_VALUES = -2;

    /**
     * Returns a {@link Builder} to create an {@link Option} using descriptive methods.
     *
     * @return a new {@link Builder} instance.
     * @since 1.3
     */
    public static Builder builder() {
        return builder(null);
    }

    /**
     * Returns a {@link Builder} to create an {@link Option} using descriptive methods.
     *
     * @param option short representation of the option.
     * @return a new {@link Builder} instance.
     * @throws IllegalArgumentException if there are any non valid Option characters in {@code opt}.
     * @since 1.3
     */
    public static Builder builder(final String option) {
        return new Builder(option);
    }

    /** The number of argument values this option can have. */
    private int argCount = UNINITIALIZED;

    /** The name of the argument for this option. */
    private String argName;

    /** The explicit converter for this option. May be null. */
    private transient Converter<?, ?> converter;

    /**
     * Specifies whether this option is deprecated, may be null.
     * <p>
     * If you want to serialize this field, use a serialization proxy.
     * </p>
     */
    private final transient DeprecatedAttributes deprecated;

    /** Description of the option. */
    private String description;

    /** The long representation of the option. */
    private String longOption;

    /** The name of the option. */
    private final String option;

    /** Specifies whether the argument value of this Option is optional. */
    private boolean optionalArg;

    /** Specifies whether this option is required to be present. */
    private boolean required;

    /** The type of this Option. */
    private Class<?> type = String.class;

    /** The list of argument values. **/
    private List<String> values = new ArrayList<>();

    /** The character that is the value separator. */
    private char valuesep;

    /**
     * Private constructor used by the nested Builder class.
     *
     * @param builder builder used to create this option.
     */
    private Option(final Builder builder) {
        this.argName = builder.argName;
        this.description = builder.description;
        this.longOption = builder.longOption;
        this.argCount = builder.argCount;
        this.option = builder.option;
        this.optionalArg = builder.optionalArg;
        this.deprecated = builder.deprecated;
        this.required = builder.required;
        this.type = builder.type;
        this.valuesep = builder.valueSeparator;
        this.converter = builder.converter;
    }

    /**
     * Creates an Option using the specified parameters.
     *
     * @param option      short representation of the option.
     * @param hasArg      specifies whether the Option takes an argument or not.
     * @param description describes the function of the option.
     *
     * @throws IllegalArgumentException if there are any non valid Option characters in {@code opt}.
     */
    public Option(final String option, final boolean hasArg, final String description) throws IllegalArgumentException {
        this(option, null, hasArg, description);
    }

    /**
     * Creates an Option using the specified parameters. The option does not take an argument.
     *
     * @param option      short representation of the option.
     * @param description describes the function of the option.
     *
     * @throws IllegalArgumentException if there are any non valid Option characters in {@code opt}.
     */
    public Option(final String option, final String description) throws IllegalArgumentException {
        this(option, null, false, description);
    }

    /**
     * Creates an Option using the specified parameters.
     *
     * @param option      short representation of the option.
     * @param longOption  the long representation of the option.
     * @param hasArg      specifies whether the Option takes an argument or not.
     * @param description describes the function of the option.
     *
     * @throws IllegalArgumentException if there are any non valid Option characters in {@code opt}.
     */
    public Option(final String option, final String longOption, final boolean hasArg, final String description) throws IllegalArgumentException {
        // ensure that the option is valid
        this.deprecated = null;
        this.option = OptionValidator.validate(option);
        this.longOption = longOption;
        // if hasArg is set then the number of arguments is 1
        if (hasArg) {
            this.argCount = 1;
        }
        this.description = description;
    }

    /**
     * Tests whether the option can accept more arguments.
     *
     * @return false if the maximum number of arguments is reached.
     * @since 1.3
     */
    boolean acceptsArg() {
        return (hasArg() || hasArgs() || hasOptionalArg()) && (argCount <= 0 || values.size() < argCount);
    }

    /**
     * Adds the value to this Option. If the number of arguments is greater than zero and there is enough space in the list then add the value. Otherwise, throw
     * a runtime exception.
     *
     * @param value The value to be added to this Option.
     */
    private void add(final String value) {
        if (!acceptsArg()) {
            throw new IllegalArgumentException("Cannot add value, list full.");
        }
        // store value
        values.add(value);
    }

    /**
     * This method is not intended to be used. It was a piece of internal API that was made public in 1.0. It currently throws an UnsupportedOperationException.
     *
     * @param value the value to add.
     * @return always throws an {@link UnsupportedOperationException}.
     * @throws UnsupportedOperationException always.
     * @deprecated Unused.
     */
    @Deprecated
    public boolean addValue(final String value) {
        throw new UnsupportedOperationException(
                "The addValue method is not intended for client use. Subclasses should use the processValue method instead.");
    }

    /**
     * Clears the Option values. After a parse is complete, these are left with data in them and they need clearing if another parse is done.
     *
     * See: <a href="https://issues.apache.org/jira/browse/CLI-71">CLI-71</a>
     */
    void clearValues() {
        values.clear();
    }

    /**
     * A rather odd clone method - due to incorrect code in 1.0 it is public and in 1.1 rather than throwing a CloneNotSupportedException it throws a
     * RuntimeException so as to maintain backwards compatible at the API level.
     *
     * After calling this method, it is very likely you will want to call clearValues().
     *
     * @return a clone of this Option instance.
     * @throws RuntimeException if a {@link CloneNotSupportedException} has been thrown by {@code super.clone()}.
     */
    @Override
    public Object clone() {
        try {
            final Option option = (Option) super.clone();
            option.values = new ArrayList<>(values);
            return option;
        } catch (final CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e.getMessage(), e);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Option)) {
            return false;
        }
        final Option other = (Option) obj;
        return Objects.equals(longOption, other.longOption) && Objects.equals(option, other.option);
    }

    /**
     * Gets the display name for the argument value.
     *
     * @return the display name for the argument value.
     */
    public String getArgName() {
        return argName;
    }

    /**
     * Gets the number of argument values this Option can take.
     *
     * <p>
     * A value equal to the constant {@link #UNINITIALIZED} (= -1) indicates the number of arguments has not been specified. A value equal to the constant
     * {@link #UNLIMITED_VALUES} (= -2) indicates that this options takes an unlimited amount of values.
     * </p>
     *
     * @return num the number of argument values.
     * @see #UNINITIALIZED
     * @see #UNLIMITED_VALUES
     */
    public int getArgs() {
        return argCount;
    }

    /**
     * Gets the value to type converter.
     *
     * @return the value to type converter.
     * @since 1.7.0
     */
    public Converter<?, ?> getConverter() {
        return converter == null ? TypeHandler.getDefault().getConverter(type) : converter;
    }

    /**
     * Gets deprecated attributes if any.
     *
     * @return boolean deprecated attributes or null.
     * @since 1.7.0
     */
    public DeprecatedAttributes getDeprecated() {
        return deprecated;
    }

    /**
     * Gets the self-documenting description of this Option.
     *
     * @return The string description of this option.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the id of this Option. This is only set when the Option shortOpt is a single character. This is used for switch statements.
     *
     * @return the id of this Option.
     */
    public int getId() {
        return getKey().charAt(0);
    }

    /**
     * Gets the 'unique' Option identifier. This is the option value if set or the long value if the options value is not set.
     *
     * @return the 'unique' Option identifier.
     * @since 1.7.0
     */
    public String getKey() {
        // if 'opt' is null, then it is a 'long' option
        return option == null ? longOption : option;
    }

    /**
     * Gets the long name of this Option.
     *
     * @return Long name of this option, or null, if there is no long name.
     */
    public String getLongOpt() {
        return longOption;
    }

    /**
     * Gets the name of this Option.
     *
     * It is this String which can be used with {@link CommandLine#hasOption(String opt)} and {@link CommandLine#getOptionValue(String opt)} to check for
     * existence and argument.
     *
     * @return The name of this option.
     */
    public String getOpt() {
        return option;
    }

    /**
     * Gets the type of this Option.
     *
     * @return The type of this option.
     */
    public Object getType() {
        return type;
    }

    /**
     * Gets the specified value of this Option or {@code null} if there is no value.
     *
     * @return the value/first value of this Option or {@code null} if there is no value.
     */
    public String getValue() {
        return hasNoValues() ? null : values.get(0);
    }

    /**
     * Gets the specified value of this Option or {@code null} if there is no value.
     *
     * @param index The index of the value to be returned.
     *
     * @return the specified value of this Option or {@code null} if there is no value.
     *
     * @throws IndexOutOfBoundsException if index is less than 1 or greater than the number of the values for this Option.
     */
    public String getValue(final int index) throws IndexOutOfBoundsException {
        return hasNoValues() ? null : values.get(index);
    }

    /**
     * Gets the value/first value of this Option or the {@code defaultValue} if there is no value.
     *
     * @param defaultValue The value to be returned if there is no value.
     *
     * @return the value/first value of this Option or the {@code defaultValue} if there are no values.
     */
    public String getValue(final String defaultValue) {
        final String value = getValue();
        return value != null ? value : defaultValue;
    }

    /**
     * Gets the values of this Option as a String array or null if there are no values.
     *
     * @return the values of this Option as a String array or null if there are no values.
     */
    public String[] getValues() {
        return hasNoValues() ? null : values.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     * Gets the value separator character.
     *
     * @return the value separator character.
     */
    public char getValueSeparator() {
        return valuesep;
    }

    /**
     * Gets the values of this Option as a List or null if there are no values.
     *
     * @return the values of this Option as a List or null if there are no values.
     */
    public List<String> getValuesList() {
        return values;
    }

    /**
     * Tests whether this Option requires an argument.
     *
     * @return boolean flag indicating if an argument is required.
     */
    public boolean hasArg() {
        return argCount > 0 || argCount == UNLIMITED_VALUES;
    }

    /**
     * Tests whether the display name for the argument value has been set.
     *
     * @return if the display name for the argument value has been set.
     */
    public boolean hasArgName() {
        return argName != null && !argName.isEmpty();
    }

    /**
     * Tests whether this Option can take many values.
     *
     * @return boolean flag indicating if multiple values are allowed.
     */
    public boolean hasArgs() {
        return argCount > 1 || argCount == UNLIMITED_VALUES;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longOption, option);
    }

    /**
     * Tests whether this Option has a long name.
     *
     * @return boolean flag indicating existence of a long name.
     */
    public boolean hasLongOpt() {
        return longOption != null;
    }

    /**
     * Tests whether this Option has any values.
     *
     * @return whether this Option has any values.
     */
    private boolean hasNoValues() {
        return values.isEmpty();
    }

    /**
     * Tests whether this Option can have an optional argument.
     *
     * @return whether this Option can have an optional argument.
     */
    public boolean hasOptionalArg() {
        return optionalArg;
    }

    /**
     * Tests whether this Option has specified a value separator.
     *
     * @return whether this Option has specified a value separator.
     * @since 1.1
     */
    public boolean hasValueSeparator() {
        return valuesep > 0;
    }

    /**
     * Tests whether this Option is deprecated.
     *
     * @return boolean flag indicating whether this Option is deprecated.
     * @since 1.7.0
     */
    public boolean isDeprecated() {
        return deprecated != null;
    }

    /**
     * Tests whether this Option is required.
     *
     * @return boolean flag indicating whether this Option is required.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Processes the value. If this Option has a value separator the value will have to be parsed into individual tokens. When n-1 tokens have been processed
     * and there are more value separators in the value, parsing is ceased and the remaining characters are added as a single token.
     *
     * @param value The String to be processed.
     */
    void processValue(final String value) {
        if (argCount == UNINITIALIZED) {
            throw new IllegalArgumentException("NO_ARGS_ALLOWED");
        }
        String add = value;
        // this Option has a separator character
        if (hasValueSeparator()) {
            // get the separator character
            final char sep = getValueSeparator();
            // store the index for the value separator
            int index = add.indexOf(sep);
            // while there are more value separators
            while (index != -1) {
                // next value to be added
                if (values.size() == argCount - 1) {
                    break;
                }
                // store
                add(add.substring(0, index));
                // parse
                add = add.substring(index + 1);
                // get new index
                index = add.indexOf(sep);
            }
        }
        // store the actual value or the last value that has been parsed
        add(add);
    }

    /**
     * Tests whether the option requires more arguments to be valid.
     *
     * @return false if the option doesn't require more arguments.
     */
    boolean requiresArg() {
        if (optionalArg) {
            return false;
        }
        if (argCount == UNLIMITED_VALUES) {
            return values.isEmpty();
        }
        return acceptsArg();
    }

    /**
     * Sets the display name for the argument value.
     *
     * @param argName the display name for the argument value.
     */
    public void setArgName(final String argName) {
        this.argName = argName;
    }

    /**
     * Sets the number of argument values this Option can take.
     *
     * @param num the number of argument values.
     */
    public void setArgs(final int num) {
        this.argCount = num;
    }

    /**
     * Sets the value to type converter.
     *
     * @param converter The converter to convert the string value to the type.
     * @since 1.7.0
     */
    public void setConverter(final Converter<?, ?> converter) {
        this.converter = converter;
    }

    /**
     * Sets the self-documenting description of this Option.
     *
     * @param description The description of this option.
     * @since 1.1
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Sets the long name of this Option.
     *
     * @param longOpt the long name of this Option.
     */
    public void setLongOpt(final String longOpt) {
        this.longOption = longOpt;
    }

    /**
     * Sets whether this Option can have an optional argument.
     *
     * @param optionalArg specifies whether the Option can have an optional argument.
     */
    public void setOptionalArg(final boolean optionalArg) {
        this.optionalArg = optionalArg;
    }

    /**
     * Sets whether this Option is mandatory.
     *
     * @param required specifies whether this Option is mandatory.
     */
    public void setRequired(final boolean required) {
        this.required = required;
    }

    /**
     * Sets the type of this Option.
     *
     * @param type the type of this Option.
     * @since 1.3
     */
    public void setType(final Class<?> type) {
        this.type = Builder.toType(type);
    }

    /**
     * Sets the type of this Option.
     * <p>
     * <b>Note:</b> this method is kept for binary compatibility and the input type is supposed to be a {@link Class} object.
     * </p>
     *
     * @param type the type of this Option.
     * @deprecated since 1.3, use {@link #setType(Class)} instead.
     */
    @Deprecated
    public void setType(final Object type) {
        setType((Class<?>) type);
    }

    /**
     * Sets the value separator. For example if the argument value was a Java property, the value separator would be '='.
     *
     * @param sep The value separator.
     */
    public void setValueSeparator(final char sep) {
        this.valuesep = sep;
    }

    String toDeprecatedString() {
        if (!isDeprecated()) {
            return "";
        }
        // @formatter:off
        final StringBuilder buf = new StringBuilder()
                .append("Option '")
                .append(option)
                .append(Char.APOS);
        // @formatter:on
        if (longOption != null) {
            buf.append(Char.APOS).append(longOption).append(Char.APOS);
        }
        buf.append(": ").append(deprecated);
        return buf.toString();
    }

    /**
     * Creates a String suitable for debugging.
     *
     * @return a String suitable for debugging.
     */
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder().append("[ ");
        buf.append("Option ");
        buf.append(option);
        if (longOption != null) {
            buf.append(Char.SP).append(longOption);
        }
        if (isDeprecated()) {
            buf.append(Char.SP);
            buf.append(deprecated.toString());
        }
        if (hasArgs()) {
            buf.append("[ARG...]");
        } else if (hasArg()) {
            buf.append(" [ARG]");
        }
        // @formatter:off
        return buf.append(" :: ")
            .append(description)
            .append(" :: ")
            .append(type)
            .append(" ]")
            .toString();
        // @formatter:on
    }
}
