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

package org.apache.commons.cli;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents list of arguments parsed against a {@link Options} descriptor.
 * <p>
 * It allows querying of a boolean {@link #hasOption(String optionName)}, in addition to retrieving the
 * {@link #getOptionValue(String optionName)} for options requiring arguments.
 * </p>
 * <p>
 * Additionally, any left-over or unrecognized arguments, are available for further processing.
 * </p>
 */
public class CommandLine implements Serializable {

    /**
     * A nested builder class to create {@code CommandLine} instance using descriptive methods.
     *
     * @since 1.4
     */
    public static final class Builder implements Supplier<CommandLine> {

        /**
         * Prints an Option to {@link System#out}.
         */
        static final Consumer<Option> DEPRECATED_HANDLER = o -> System.out.println(o.toDeprecatedString());

        /** The unrecognized options/arguments */
        private final List<String> args = new LinkedList<>();

        /** The processed options */
        private final List<Option> options = new ArrayList<>();

        /**
         * Deprecated Option handler.
         */
        private Consumer<Option> deprecatedHandler = DEPRECATED_HANDLER;

        /**
         * Constructs a new instance.
         *
         * @deprecated Use {@link #builder()}.
         */
        @Deprecated
        public Builder() {
            // empty
        }

        /**
         * Adds left-over unrecognized option/argument.
         *
         * @param arg the unrecognized option/argument.
         * @return this Builder instance for method chaining.
         */
        public Builder addArg(final String arg) {
            if (arg != null) {
                args.add(arg);
            }
            return this;
        }

        /**
         * Adds an option to the command line. The values of the option are stored.
         *
         * @param option the processed option.
         * @return this Builder instance for method chaining.
         */
        public Builder addOption(final Option option) {
            if (option != null) {
                options.add(option);
            }
            return this;
        }

        /**
         * Creates a new instance.
         *
         * @return a new instance.
         * @deprecated Use {@link #get()}.
         */
        @Deprecated
        public CommandLine build() {
            return get();
        }

        /**
         * Creates a new instance.
         *
         * @return a new instance.
         * @since 1.10.0
         */
        @Override
        public CommandLine get() {
            return new CommandLine(args, options, deprecatedHandler);
        }

        /**
         * Sets the deprecated option handler.
         *
         * @param deprecatedHandler the deprecated option handler.
         * @return {@code this} instance.
         * @since 1.7.0
         */
        public Builder setDeprecatedHandler(final Consumer<Option> deprecatedHandler) {
            this.deprecatedHandler = deprecatedHandler;
            return this;
        }
    }

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new builder.
     *
     * @return a new builder.
     * @since 1.7.0
     */
    public static Builder builder() {
        return new Builder();
    }

    /** The unrecognized options/arguments */
    private final List<String> args;

    /** The processed options */
    private final List<Option> options;

    /**
     * The deprecated option handler.
     * <p>
     * If you want to serialize this field, use a serialization proxy.
     * </p>
     */
    private final transient Consumer<Option> deprecatedHandler;

    /**
     * Creates a command line.
     */
    protected CommandLine() {
        this(new LinkedList<>(), new ArrayList<>(), Builder.DEPRECATED_HANDLER);
    }

    /**
     * Creates a command line.
     */
    private CommandLine(final List<String> args, final List<Option> options, final Consumer<Option> deprecatedHandler) {
        this.args = Objects.requireNonNull(args, "args");
        this.options = Objects.requireNonNull(options, "options");
        this.deprecatedHandler = deprecatedHandler;
    }

    /**
     * Adds left-over unrecognized option/argument.
     *
     * @param arg the unrecognized option/argument.
     */
    protected void addArg(final String arg) {
        if (arg != null) {
            args.add(arg);
        }
    }

    /**
     * Adds an option to the command line. The values of the option are stored.
     *
     * @param option the processed option.
     */
    protected void addOption(final Option option) {
        if (option != null) {
            options.add(option);
        }
    }

    private <T> T get(final Supplier<T> supplier) {
        return supplier == null ? null : supplier.get();
    }

    /**
     * Gets any left-over non-recognized options and arguments
     *
     * @return remaining items passed in but not parsed as a {@code List}.
     */
    public List<String> getArgList() {
        return args;
    }

    /**
     * Gets any left-over non-recognized options and arguments
     *
     * @return remaining items passed in but not parsed as an array.
     */
    public String[] getArgs() {
        return args.toArray(Util.EMPTY_STRING_ARRAY);
    }

    /**
     * Gets the {@code Object} type of this {@code Option}.
     *
     * @deprecated due to System.err message. Instead use getParsedOptionValue(char)
     * @param optionChar the name of the option.
     * @return the type of opt.
     */
    @Deprecated
    public Object getOptionObject(final char optionChar) {
        return getOptionObject(String.valueOf(optionChar));
    }

    /**
     * Gets the {@code Object} type of this {@code Option}.
     *
     * @param optionName the name of the option.
     * @return the type of this {@code Option}.
     * @deprecated due to System.err message. Instead use getParsedOptionValue(String)
     */
    @Deprecated
    public Object getOptionObject(final String optionName) {
        try {
            return getParsedOptionValue(optionName);
        } catch (final ParseException pe) {
            System.err.println("Exception found converting " + optionName + " to desired type: " + pe.getMessage());
            return null;
        }
    }

    /**
     * Gets the map of values associated to the option. This is convenient for options specifying Java properties like
     * <code>-Dparam1=value1
     * -Dparam2=value2</code>. All odd numbered values are property keys
     * and even numbered values are property values.  If there are an odd number of values
     * the last value is assumed to be a boolean flag and the value is "true".
     *
     * @param option name of the option.
     * @return The Properties mapped by the option, never {@code null} even if the option doesn't exists.
     * @since 1.5.0
     */
    public Properties getOptionProperties(final Option option) {
        final Properties props = new Properties();
        for (final Option processedOption : options) {
            if (processedOption.equals(option)) {
                processPropertiesFromValues(props, processedOption.getValuesList());
            }
        }
        return props;
    }

    /**
     * Gets the map of values associated to the option. This is convenient for options specifying Java properties like
     * <code>-Dparam1=value1
     * -Dparam2=value2</code>. The first argument of the option is the key, and the 2nd argument is the value. If the option
     * has only one argument ({@code -Dfoo}) it is considered as a boolean flag and the value is {@code "true"}.
     *
     * @param optionName name of the option.
     * @return The Properties mapped by the option, never {@code null} even if the option doesn't exists.
     * @since 1.2
     */
    public Properties getOptionProperties(final String optionName) {
        final Properties props = new Properties();
        for (final Option option : options) {
            if (optionName.equals(option.getOpt()) || optionName.equals(option.getLongOpt())) {
                processPropertiesFromValues(props, option.getValuesList());
            }
        }
        return props;
    }

    /**
     * Gets an array of the processed {@link Option}s.
     *
     * @return an array of the processed {@link Option}s.
     */
    public Option[] getOptions() {
        return options.toArray(Option.EMPTY_ARRAY);
    }

    /**
     * Gets the first argument, if any, of this option.
     *
     * @param optionChar the character name of the option.
     * @return Value of the argument if option is set, and has an argument, otherwise null.
     */
    public String getOptionValue(final char optionChar) {
        return getOptionValue(String.valueOf(optionChar));
    }

    /**
     * Gets the argument, if any, of an option.
     *
     * @param optionChar character name of the option
     * @param defaultValue is the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     */
    public String getOptionValue(final char optionChar, final String defaultValue) {
        return getOptionValue(String.valueOf(optionChar), () -> defaultValue);
    }

    /**
     * Gets the argument, if any, of an option.
     *
     * @param optionChar character name of the option
     * @param defaultValue is a supplier for the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     * @since 1.7.0
     */
    public String getOptionValue(final char optionChar, final Supplier<String> defaultValue) {
        return getOptionValue(String.valueOf(optionChar), defaultValue);
    }

    /**
     * Gets the first argument, if any, of this option.
     *
     * @param option the option.
     * @return Value of the argument if option is set, and has an argument, otherwise null.
     * @since 1.5.0
     */
    public String getOptionValue(final Option option) {
        final String[] values = getOptionValues(option);
        return values == null ? null : values[0];
    }

    /**
     * Gets the first argument, if any, of an option.
     *
     * @param option the option.
     * @param defaultValue is the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     * @since 1.5.0
     */
    public String getOptionValue(final Option option, final String defaultValue) {
        return getOptionValue(option, () -> defaultValue);
    }

    /**
     * Gets the first argument, if any, of an option.
     *
     * @param option the option.
     * @param defaultValue is a supplier for the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     * @since 1.7.0
     */
    public String getOptionValue(final Option option, final Supplier<String> defaultValue) {
        final String answer = getOptionValue(option);
        return answer != null ? answer : get(defaultValue);
    }

    /**
     * Gets the first argument, if any, of this option group.
     *
     * @param optionGroup the option group.
     * @return Value of the argument if option group is selected, and has an argument, otherwise null.
     * @since 1.9.0
     */
    public String getOptionValue(final OptionGroup optionGroup) {
        final String[] values = getOptionValues(optionGroup);
        return values == null ? null : values[0];
    }

    /**
     * Gets the first argument, if any, of an option group.
     *
     * @param optionGroup the option group.
     * @param defaultValue is the default value to be returned if the option group is not selected.
     * @return Value of the argument if option group is selected, and has an argument, otherwise {@code defaultValue}.
     * @since 1.9.0
     */
    public String getOptionValue(final OptionGroup optionGroup, final String defaultValue) {
        return getOptionValue(optionGroup, () -> defaultValue);
    }

    /**
     * Gets the first argument, if any, of an option group.
     *
     * @param optionGroup the option group.
     * @param defaultValue is a supplier for the default value to be returned if the option group is not selected.
     * @return Value of the argument if option group is selected, and has an argument, otherwise {@code defaultValue}.
     * @since 1.9.0
     */
    public String getOptionValue(final OptionGroup optionGroup, final Supplier<String> defaultValue) {
        final String answer = getOptionValue(optionGroup);
        return answer != null ? answer : get(defaultValue);
    }

    /**
     * Gets the first argument, if any, of this option.
     *
     * @param optionName the name of the option.
     * @return Value of the argument if option is set, and has an argument, otherwise null.
     */
    public String getOptionValue(final String optionName) {
        return getOptionValue(resolveOption(optionName));
    }

    /**
     * Gets the first argument, if any, of an option.
     *
     * @param optionName name of the option.
     * @param defaultValue is the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     */
    public String getOptionValue(final String optionName, final String defaultValue) {
        return getOptionValue(resolveOption(optionName), () -> defaultValue);
    }

    /**
     * Gets the first argument, if any, of an option.
     *
     * @param optionName name of the option.
     * @param defaultValue is a supplier for the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     * @since 1.7.0
     */
    public String getOptionValue(final String optionName, final Supplier<String> defaultValue) {
        return getOptionValue(resolveOption(optionName), defaultValue);
    }

    /**
     * Gets the array of values, if any, of an option.
     *
     * @param optionChar character name of the option.
     * @return Values of the argument if option is set, and has an argument, otherwise null.
     */
    public String[] getOptionValues(final char optionChar) {
        return getOptionValues(String.valueOf(optionChar));
    }

    /**
     * Gets the array of values, if any, of an option.
     *
     * @param option the option.
     * @return Values of the argument if option is set, and has an argument, otherwise null.
     * @since 1.5.0
     */
    public String[] getOptionValues(final Option option) {
        if (option == null) {
            return null;
        }
        final List<String> values = new ArrayList<>();
        for (final Option processedOption : options) {
            if (processedOption.equals(option)) {
                if (option.isDeprecated()) {
                    handleDeprecated(option);
                }
                values.addAll(processedOption.getValuesList());
            }
        }
        return values.isEmpty() ? null : values.toArray(Util.EMPTY_STRING_ARRAY);
    }

    /**
     * Gets the array of values, if any, of an option group.
     *
     * @param optionGroup the option group.
     * @return Values of the argument if option group is selected, and has an argument, otherwise null.
     * @since 1.9.0
     */
    public String[] getOptionValues(final OptionGroup optionGroup) {
        if (optionGroup == null || !optionGroup.isSelected()) {
            return null;
        }
        return getOptionValues(optionGroup.getSelected());
    }

    /**
     * Gets the array of values, if any, of an option.
     *
     * @param optionName string name of the option.
     * @return Values of the argument if option is set, and has an argument, otherwise null.
     */
    public String[] getOptionValues(final String optionName) {
        return getOptionValues(resolveOption(optionName));
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param optionChar the name of the option.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or null if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.5.0
     */
    public <T> T getParsedOptionValue(final char optionChar) throws ParseException {
        return getParsedOptionValue(String.valueOf(optionChar));
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param optionChar the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or the defaultValue if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    public <T> T getParsedOptionValue(final char optionChar, final Supplier<T> defaultValue) throws ParseException {
        return getParsedOptionValue(String.valueOf(optionChar), defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param optionChar the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or the defaultValue if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    public <T> T getParsedOptionValue(final char optionChar, final T defaultValue) throws ParseException {
        return getParsedOptionValue(String.valueOf(optionChar), defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param option the option.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or null if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.5.0
     */
    public <T> T getParsedOptionValue(final Option option) throws ParseException {
        return getParsedOptionValue(option, () -> null);
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param option the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or the defaultValue if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    @SuppressWarnings("unchecked")
    public <T> T getParsedOptionValue(final Option option, final Supplier<T> defaultValue) throws ParseException {
        if (option == null) {
            return get(defaultValue);
        }
        final String res = getOptionValue(option);
        try {
            if (res == null) {
                return get(defaultValue);
            }
            return (T) option.getConverter().apply(res);
        } catch (final Exception e) {
            throw ParseException.wrap(e);
        }
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param option the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or the defaultValue if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    public <T> T getParsedOptionValue(final Option option, final T defaultValue) throws ParseException {
        return getParsedOptionValue(option, () -> defaultValue);
    }

    /**
     * Gets a version of this {@code OptionGroup} converted to a particular type.
     *
     * @param optionGroup the option group.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or null if no option in the OptionGroup is set.
     * @throws ParseException if there are problems turning the selected option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.9.0
     */
    public <T> T getParsedOptionValue(final OptionGroup optionGroup) throws ParseException {
        return getParsedOptionValue(optionGroup, () -> null);
    }

    /**
     * Gets a version of this {@code OptionGroup} converted to a particular type.
     *
     * @param optionGroup the option group.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or the defaultValue if no option in the OptionGroup is set.
     * @throws ParseException if there are problems turning the selected option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.9.0
     */
    public <T> T getParsedOptionValue(final OptionGroup optionGroup, final Supplier<T> defaultValue) throws ParseException {
        if (optionGroup == null || !optionGroup.isSelected()) {
            return get(defaultValue);
        }
        return getParsedOptionValue(optionGroup.getSelected(), defaultValue);
    }

    /**
     * Gets a version of this {@code OptionGroup} converted to a particular type.
     *
     * @param optionGroup the option group.
     * @param defaultValue the default value to return if an option is not selected.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or the defaultValue if no option in the OptionGroup is set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.9.0
     */
    public <T> T getParsedOptionValue(final OptionGroup optionGroup, final T defaultValue) throws ParseException {
        return getParsedOptionValue(optionGroup, () -> defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param optionName the name of the option.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or null if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.2
     */
    public <T> T getParsedOptionValue(final String optionName) throws ParseException {
        return getParsedOptionValue(resolveOption(optionName));
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param optionName the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or the defaultValue if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    public <T> T getParsedOptionValue(final String optionName, final Supplier<T> defaultValue) throws ParseException {
        return getParsedOptionValue(resolveOption(optionName), defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param optionName the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object or the defaultValue if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    public <T> T getParsedOptionValue(final String optionName, final T defaultValue) throws ParseException {
        return getParsedOptionValue(resolveOption(optionName), defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to an array of a particular type.
     *
     * @param optionChar the name of the option.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or null if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final char optionChar) throws ParseException {
        return getParsedOptionValues(String.valueOf(optionChar));
    }

    /**
     * Gets a version of this {@code Option} converted to an array of a particular type.
     *
     * @param optionChar the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or the defaultValue if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final char optionChar, final Supplier<T[]> defaultValue) throws ParseException {
        return getParsedOptionValues(String.valueOf(optionChar), defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to an array of a particular type.
     *
     * @param optionChar the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or the defaultValue if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final char optionChar, final T[] defaultValue) throws ParseException {
        return getParsedOptionValues(String.valueOf(optionChar), defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to an array of a particular type.
     *
     * @param option the option.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or null if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final Option option) throws ParseException {
        return getParsedOptionValues(option, () -> null);
    }

    /**
     * Gets a version of this {@code Option} converted to an array of a particular type.
     *
     * @param option the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or the defaultValue if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    @SuppressWarnings("unchecked")
    public <T> T[] getParsedOptionValues(final Option option, final Supplier<T[]> defaultValue) throws ParseException {
        if (option == null) {
            return get(defaultValue);
        }
        final Class<? extends T> clazz = (Class<? extends T>) option.getType();
        final String[] values = getOptionValues(option);
        if (values == null) {
            return get(defaultValue);
        }
        final T[] result = (T[]) Array.newInstance(clazz, values.length);
        try {
            for (int i = 0; i < values.length; i++) {
                result[i] = clazz.cast(option.getConverter().apply(values[i]));
            }
            return result;
        } catch (final Exception t) {
            throw ParseException.wrap(t);
        }
    }

    /**
     * Gets a version of this {@code Option} converted to an array of a particular type.
     *
     * @param option the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or the defaultValue if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final Option option, final T[] defaultValue) throws ParseException {
        return getParsedOptionValues(option, () -> defaultValue);
    }

    /**
     * Gets a version of this {@code OptionGroup} converted to an array of a particular type.
     *
     * @param optionGroup the option group.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or null if no option in the OptionGroup is set.
     * @throws ParseException if there are problems turning the selected option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final OptionGroup optionGroup) throws ParseException {
        return getParsedOptionValues(optionGroup, () -> null);
    }

    /**
     * Gets a version of this {@code OptionGroup} converted to an array of a particular type.
     *
     * @param optionGroup the option group.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or null if no option in the OptionGroup is set.
     * @throws ParseException if there are problems turning the selected option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final OptionGroup optionGroup, final Supplier<T[]> defaultValue) throws ParseException {
        if (optionGroup == null || !optionGroup.isSelected()) {
            return get(defaultValue);
        }
        return getParsedOptionValues(optionGroup.getSelected(), defaultValue);
    }

    /**
     * Gets a version of this {@code OptionGroup} converted to an array of a particular type.
     *
     * @param optionGroup the option group.
     * @param defaultValue the default value to return if an option is not selected.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or null if no option in the OptionGroup is set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final OptionGroup optionGroup, final T[] defaultValue) throws ParseException {
        return getParsedOptionValues(optionGroup, () -> defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to an array of a particular type.
     *
     * @param optionName the name of the option.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or null if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final String optionName) throws ParseException {
        return getParsedOptionValues(resolveOption(optionName));
    }

    /**
     * Gets a version of this {@code Option} converted to an array of a particular type.
     *
     * @param optionName the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or defaultValues if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final String optionName, final Supplier<T[]> defaultValue) throws ParseException {
        return getParsedOptionValues(resolveOption(optionName), defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to an array of a particular type.
     *
     * @param optionName the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The array type for the return value.
     * @return the values parsed into an array of objects or defaultValues if the option is not set.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.10.0
     */
    public <T> T[] getParsedOptionValues(final String optionName, final T[] defaultValue) throws ParseException {
        return getParsedOptionValues(resolveOption(optionName), defaultValue);
    }

    /**
     * Handles deprecated options.
     *
     * @param option a deprecated option.
     */
    private void handleDeprecated(final Option option) {
        if (deprecatedHandler != null) {
            deprecatedHandler.accept(option);
        }
    }

    /**
     * jkeyes - commented out until it is implemented properly
     * <p>
     * Dump state, suitable for debugging.
     * </p>
     *
     * @return Stringified form of this object.
     */

    /*
     * public String toString() { StringBuilder buf = new StringBuilder();
     *
     * buf.append("[ CommandLine: [ options: "); buf.append(options.toString()); buf.append(" ] [ args: ");
     * buf.append(args.toString()); buf.append(" ] ]");
     *
     * return buf.toString(); }
     */

    /**
     * Tests to see if an option has been set.
     *
     * @param optionChar character name of the option.
     * @return true if set, false if not.
     */
    public boolean hasOption(final char optionChar) {
        return hasOption(String.valueOf(optionChar));
    }

    /**
     * Tests to see if an option has been set.
     *
     * @param option the option to check.
     * @return true if set, false if not.
     * @since 1.5.0
     */
    public boolean hasOption(final Option option) {
        final boolean result = options.contains(option);
        if (result && option.isDeprecated()) {
            handleDeprecated(option);
        }
        return result;
    }

    /**
     * Tests to see if an option has been set.
     *
     * @param optionGroup the option group to check.
     * @return true if set, false if not.
     * @since 1.9.0
     */
    public boolean hasOption(final OptionGroup optionGroup) {
        if (optionGroup == null || !optionGroup.isSelected()) {
            return false;
        }
        return hasOption(optionGroup.getSelected());
    }

    /**
     * Tests to see if an option has been set.
     *
     * @param optionName Short name of the option.
     * @return true if set, false if not.
     */
    public boolean hasOption(final String optionName) {
        return hasOption(resolveOption(optionName));
    }

    /**
     * Returns an iterator over the Option members of CommandLine.
     *
     * @return an {@code Iterator} over the processed {@link Option} members of this {@link CommandLine}.
     */
    public Iterator<Option> iterator() {
        return options.iterator();
    }

    /**
     * Parses a list of values as properties.  All odd numbered values are property keys
     * and even numbered values are property values.  If there are an odd number of values
     * the last value is assumed to be a boolean with a value of "true".
     * @param props the properties to update.
     * @param values the list of values to parse.
     */
    private void processPropertiesFromValues(final Properties props, final List<String> values) {
        for (int i = 0; i < values.size(); i += 2) {
            if (i + 1 < values.size()) {
                props.put(values.get(i), values.get(i + 1));
            } else {
                props.put(values.get(i), "true");
            }
        }
    }

    /**
     * Retrieves the option object given the long or short option as a String
     *
     * @param optionName short or long name of the option, may be null.
     * @return Canonicalized option.
     */
    private Option resolveOption(final String optionName) {
        final String actual = Util.stripLeadingHyphens(optionName);
        if (actual != null) {
            for (final Option option : options) {
                if (actual.equals(option.getOpt()) || actual.equals(option.getLongOpt())) {
                    return option;
                }
            }
        }
        return null;
    }
}
