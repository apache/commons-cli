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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Represents list of arguments parsed against a {@link Options} descriptor.
 * <p>
 * It allows querying of a boolean {@link #hasOption(String opt)}, in addition to retrieving the
 * {@link #getOptionValue(String opt)} for options requiring arguments.
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
    public static final class Builder {

        /**
         * CommandLine that is being build by this Builder.
         */
        private final CommandLine commandLine = new CommandLine();

        /**
         * Adds left-over unrecognized option/argument.
         *
         * @param arg the unrecognized option/argument.
         *
         * @return this Builder instance for method chaining.
         */
        public Builder addArg(final String arg) {
            commandLine.addArg(arg);
            return this;
        }

        /**
         * Adds an option to the command line. The values of the option are stored.
         *
         * @param opt the processed option.
         *
         * @return this Builder instance for method chaining.
         */
        public Builder addOption(final Option opt) {
            commandLine.addOption(opt);
            return this;
        }

        /**
         * Returns the new instance.
         *
         * @return the new instance.
         */
        public CommandLine build() {
            return commandLine;
        }
    }

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The unrecognized options/arguments */
    private final List<String> args = new LinkedList<>();

    /** The processed options */
    private final List<Option> options = new ArrayList<>();

    /**
     * Creates a command line.
     */
    protected CommandLine() {
        // nothing to do
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
     * @param opt the processed option.
     */
    protected void addOption(final Option opt) {
        if (opt != null) {
            options.add(opt);
        }
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
     * @param opt the name of the option.
     * @return the type of opt.
     */
    @Deprecated
    public Object getOptionObject(final char opt) {
        return getOptionObject(String.valueOf(opt));
    }

    /**
     * Gets the {@code Object} type of this {@code Option}.
     *
     * @param opt the name of the option.
     * @return the type of this {@code Option}.
     * @deprecated due to System.err message. Instead use getParsedOptionValue(String)
     */
    @Deprecated
    public Object getOptionObject(final String opt) {
        try {
            return getParsedOptionValue(opt);
        } catch (final ParseException pe) {
            System.err.println("Exception found converting " + opt + " to desired type: " + pe.getMessage());
            return null;
        }
    }

    /**
     * Gets the map of values associated to the option. This is convenient for options specifying Java properties like
     * <code>-Dparam1=value1
     * -Dparam2=value2</code>. The first argument of the option is the key, and the 2nd argument is the value. If the option
     * has only one argument ({@code -Dfoo}) it is considered as a boolean flag and the value is {@code "true"}.
     *
     * @param option name of the option.
     * @return The Properties mapped by the option, never {@code null} even if the option doesn't exists.
     * @since 1.5.0
     */
    public Properties getOptionProperties(final Option option) {
        final Properties props = new Properties();

        for (final Option processedOption : options) {
            if (processedOption.equals(option)) {
                final List<String> values = processedOption.getValuesList();
                if (values.size() >= 2) {
                    // use the first 2 arguments as the key/value pair
                    props.put(values.get(0), values.get(1));
                } else if (values.size() == 1) {
                    // no explicit value, handle it as a boolean
                    props.put(values.get(0), "true");
                }
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
     * @param opt name of the option.
     * @return The Properties mapped by the option, never {@code null} even if the option doesn't exists.
     * @since 1.2
     */
    public Properties getOptionProperties(final String opt) {
        final Properties props = new Properties();

        for (final Option option : options) {
            if (opt.equals(option.getOpt()) || opt.equals(option.getLongOpt())) {
                final List<String> values = option.getValuesList();
                if (values.size() >= 2) {
                    // use the first 2 arguments as the key/value pair
                    props.put(values.get(0), values.get(1));
                } else if (values.size() == 1) {
                    // no explicit value, handle it as a boolean
                    props.put(values.get(0), "true");
                }
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
     * @param opt the character name of the option.
     * @return Value of the argument if option is set, and has an argument, otherwise null.
     */
    public String getOptionValue(final char opt) {
        return getOptionValue(String.valueOf(opt));
    }

    /**
     * Gets the argument, if any, of an option.
     *
     * @param opt character name of the option
     * @param defaultValue is the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     */
    public String getOptionValue(final char opt, final String defaultValue) {
        return getOptionValue(String.valueOf(opt), () -> defaultValue);
    }

    /**
     * Gets the argument, if any, of an option.
     *
     * @param opt character name of the option
     * @param defaultValue is a supplier for the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     * @since 1.7.0
     */
    public String getOptionValue(final char opt, final Supplier<String> defaultValue) {
        return getOptionValue(String.valueOf(opt), defaultValue);
    }

    /**
     * Gets the first argument, if any, of this option.
     *
     * @param option the name of the option.
     * @return Value of the argument if option is set, and has an argument, otherwise null.
     * @since 1.5.0
     */
    public String getOptionValue(final Option option) {
        if (option == null) {
            return null;
        }
        final String[] values = getOptionValues(option);
        return values == null ? null : values[0];
    }

    /**
     * Gets the first argument, if any, of an option.
     *
     * @param option name of the option.
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
     * @param option name of the option.
     * @param defaultValue is a supplier for the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     * @since 1.7.0
     */
    public String getOptionValue(final Option option, final Supplier<String> defaultValue) {
        final String answer = getOptionValue(option);
        return answer != null ? answer : defaultValue.get();
    }

    /**
     * Gets the first argument, if any, of this option.
     *
     * @param opt the name of the option.
     * @return Value of the argument if option is set, and has an argument, otherwise null.
     */
    public String getOptionValue(final String opt) {
        return getOptionValue(resolveOption(opt));
    }

    /**
     * Gets the first argument, if any, of an option.
     *
     * @param opt name of the option.
     * @param defaultValue is the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     */
    public String getOptionValue(final String opt, final String defaultValue) {
        return getOptionValue(resolveOption(opt), () -> defaultValue);
    }

    /**
     * Gets the first argument, if any, of an option.
     *
     * @param opt name of the option.
     * @param defaultValue is a supplier for the default value to be returned if the option is not specified.
     * @return Value of the argument if option is set, and has an argument, otherwise {@code defaultValue}.
     * @since 1.7.0
     */
    public String getOptionValue(final String opt, final Supplier<String> defaultValue) {
        return getOptionValue(resolveOption(opt), defaultValue);
    }


    /**
     * Gets the array of values, if any, of an option.
     *
     * @param opt character name of the option.
     * @return Values of the argument if option is set, and has an argument, otherwise null.
     */
    public String[] getOptionValues(final char opt) {
        return getOptionValues(String.valueOf(opt));
    }

    /**
     * Gets the array of values, if any, of an option.
     *
     * @param option string name of the option.
     * @return Values of the argument if option is set, and has an argument, otherwise null.
     * @since 1.5.0
     */
    public String[] getOptionValues(final Option option) {
        final List<String> values = new ArrayList<>();

        for (final Option processedOption : options) {
            if (processedOption.equals(option)) {
                values.addAll(processedOption.getValuesList());
            }
        }

        return values.isEmpty() ? null : values.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     * Gets the array of values, if any, of an option.
     *
     * @param opt string name of the option.
     * @return Values of the argument if option is set, and has an argument, otherwise null.
     */
    public String[] getOptionValues(final String opt) {
        return getOptionValues(resolveOption(opt));
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param opt the name of the option.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.5.0
     */
    public <T> T getParsedOptionValue(final char opt) throws ParseException {
        return getParsedOptionValue(String.valueOf(opt));
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param opt the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    public <T> T getParsedOptionValue(final char opt, final T defaultValue) throws ParseException {
        return getParsedOptionValue(String.valueOf(opt), defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param opt the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    public <T> T getParsedOptionValue(final char opt, final Supplier<T> defaultValue) throws ParseException {
        return getParsedOptionValue(String.valueOf(opt), defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param option the name of the option.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.5.0
     */
    public <T> T getParsedOptionValue(final Option option) throws ParseException {
        return  getParsedOptionValue(option, () -> null);
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param option the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    public <T> T getParsedOptionValue(final Option option, final T defaultValue) throws ParseException {
        return getParsedOptionValue(option, () -> defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param option the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    @SuppressWarnings("unchecked")
    public <T> T getParsedOptionValue(final Option option, final Supplier<T> defaultValue) throws ParseException {
        final String res = option == null ? null : getOptionValue(option);

        try {
            if (res == null) {
                return defaultValue == null ? null : defaultValue.get();
            }
            return (T) option.getConverter().apply(res);
        } catch (final Throwable e) {
            throw ParseException.wrap(e);
        }
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param opt the name of the option.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.2
     */
    public <T> T getParsedOptionValue(final String opt) throws ParseException {
        return getParsedOptionValue(resolveOption(opt));
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param opt the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    public <T> T getParsedOptionValue(final String opt, final T defaultValue) throws ParseException {
        return getParsedOptionValue(resolveOption(opt), defaultValue);
    }

    /**
     * Gets a version of this {@code Option} converted to a particular type.
     *
     * @param opt the name of the option.
     * @param defaultValue the default value to return if opt is not set.
     * @param <T> The return type for the method.
     * @return the value parsed into a particular object.
     * @throws ParseException if there are problems turning the option value into the desired type
     * @see PatternOptionBuilder
     * @since 1.7.0
     */
    public <T> T getParsedOptionValue(final String opt, final Supplier<T> defaultValue) throws ParseException {
        return getParsedOptionValue(resolveOption(opt), defaultValue);
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
     * @param opt character name of the option.
     * @return true if set, false if not.
     */
    public boolean hasOption(final char opt) {
        return hasOption(String.valueOf(opt));
    }

    /**
     * Tests to see if an option has been set.
     *
     * @param opt the option to check.
     * @return true if set, false if not.
     * @since 1.5.0
     */
    public boolean hasOption(final Option opt) {
        return options.contains(opt);
    }

    /**
     * Tests to see if an option has been set.
     *
     * @param opt Short name of the option.
     * @return true if set, false if not.
     */
    public boolean hasOption(final String opt) {
        return hasOption(resolveOption(opt));
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
     * Retrieves the option object given the long or short option as a String
     *
     * @param opt short or long name of the option, may be null.
     * @return Canonicalized option.
     */
    private Option resolveOption(final String opt) {
        final String actual = Util.stripLeadingHyphens(opt);
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
