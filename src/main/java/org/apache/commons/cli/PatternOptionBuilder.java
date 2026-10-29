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

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Date;
import java.util.Map;

/**
 * Creates options from a single string.
 *
 * <p>The pattern contains single-character option names, each optionally followed by a punctuation character that specifies the expected value type.</p>
 *
 * <table border="1">
 * <caption>Overview of PatternOptionBuilder patterns</caption>
 * <tr>
 * <th scope="col">Pattern</th>
 * <th scope="col">Command-line option</th>
 * </tr>
 * <tr>
 * <td>a</td>
 * <td>-a flag</td>
 * </tr>
 * <tr>
 * <td>b@</td>
 * <td>-b [class name] (creates an object using its public no-argument constructor)</td>
 * </tr>
 * <tr>
 * <td>c&gt;</td>
 * <td>-c [file name]</td>
 * </tr>
 * <tr>
 * <td>d+</td>
 * <td>-d [class name]</td>
 * </tr>
 * <tr>
 * <td>e%</td>
 * <td>-e [number] (creates a Double or Long instance depending on whether the value contains a '.')</td>
 * </tr>
 * <tr>
 * <td>f/</td>
 * <td>-f [URL]</td>
 * </tr>
 * <tr>
 * <td>g:</td>
 * <td>-g [string]</td>
 * </tr>
 * <tr>
 * <td>h&lt;</td>
 * <td>-h [file name] (opens the existing file for reading when the value is converted; the caller must close the stream)</td>
 * </tr>
 * </table>
 *
 * <p>
 * For example, the following accepts command-line arguments such as {@code -v -p string-value -f https://example.com}. The exclamation mark makes the
 * preceding option mandatory, here {@code -p}. An exclamation mark at the start of the pattern makes the first option mandatory.
 * </p>
 *
 * <pre>
 * Options options = PatternOptionBuilder.parsePattern("vp:!f/");
 * </pre>
 *
 * <p>
 * When a converted option value is retrieved, for example through {@link CommandLine#getParsedOptionValue(String)}, the {@code @} and {@code +} codes
 * load the class named by the option value, and {@code @} also creates an instance by calling its public no-argument constructor. Do not use these codes
 * for values from an untrusted source.
 * </p>
 *
 * <p>
 * ODO Extract these types into OptionType and make them pluggable.</p>
 */
public class PatternOptionBuilder {

    /**
     * String class.
     */
    public static final Class<String> STRING_VALUE = String.class;

    /**
     * Object class.
     */
    public static final Class<Object> OBJECT_VALUE = Object.class;

    /**
     * Number class.
     */
    public static final Class<Number> NUMBER_VALUE = Number.class;

    /**
     * Date class.
     */
    public static final Class<Date> DATE_VALUE = Date.class;

    /**
     * Class class.
     */
    public static final Class<?> CLASS_VALUE = Class.class;

    /**
     * FileInputStream class.
     */
    public static final Class<FileInputStream> EXISTING_FILE_VALUE = FileInputStream.class;

    /// can we do this one??
    // is meant to check that the file exists, else it errors.
    // ie) it's for reading not writing.

    /**
     * File class.
     */
    public static final Class<File> FILE_VALUE = File.class;

    /**
     * File array class.
     */
    public static final Class<File[]> FILES_VALUE = File[].class;

    /**
     * URL class.
     */
    public static final Class<URL> URL_VALUE = URL.class;

    /**
     * The converter to use for unimplemented data types.
     */
    private static final Converter<?, UnsupportedOperationException> UNSUPPORTED = s -> {
        throw new UnsupportedOperationException("Not yet implemented");
    };

    /**
     * Gets the class that {@code ch} represents.
     *
     * @param ch The specified character.
     * @return The class that {@code ch} represents.
     * @deprecated Use {@link #getValueType(char)}.
     */
    @Deprecated // since="1.7.0"
    public static Object getValueClass(final char ch) {
        return getValueType(ch);
    }

    /**
     * Gets the class that {@code ch} represents.
     *
     * @param ch The specified character.
     * @return The class that {@code ch} represents.
     * @since 1.7.0
     */
    public static Class<?> getValueType(final char ch) {
        switch (ch) {
        case '@':
            return OBJECT_VALUE;
        case ':':
            return STRING_VALUE;
        case '%':
            return NUMBER_VALUE;
        case '+':
            return CLASS_VALUE;
        case '#':
            return DATE_VALUE;
        case '<':
            return EXISTING_FILE_VALUE;
        case '>':
            return FILE_VALUE;
        case '*':
            return FILES_VALUE;
        case '/':
            return URL_VALUE;
        }

        return null;
    }

    /**
     * Tests whether {@code ch} specifies a value type or marks an option as required in a pattern.
     *
     * @param ch The specified character.
     * @return true if {@code ch} is a value code, otherwise false.
     */
    public static boolean isValueCode(final char ch) {
        return ch == '@' || ch == ':' || ch == '%' || ch == '+' || ch == '#' || ch == '<' || ch == '>' || ch == '*' || ch == '/' || ch == '!';
    }

    /**
     * Parses the pattern into an {@link Options} instance.
     *
     * @param pattern The pattern string.
     * @return The {@link Options} instance.
     */
    public static Options parsePattern(final String pattern) {
        char opt = Char.SP;
        boolean required = false;
        Class<?> type = null;
        Converter<?, ?> converter = Converter.DEFAULT;

        final Options options = new Options();

        for (int i = 0; i < pattern.length(); i++) {
            final char ch = pattern.charAt(i);

            // a value code comes after an option and specifies
            // details about it
            if (!isValueCode(ch)) {
                if (opt != Char.SP) {
                    // @formatter:off
                    final Option option = Option.builder(String.valueOf(opt))
                            .hasArg(type != null)
                            .required(required)
                            .type(type)
                            .converter(converter)
                            .get();
                    // @formatter:on
                    // we have a previous one to deal with
                    options.addOption(option);
                    required = false;
                    type = null;
                    converter = Converter.DEFAULT;
                }

                opt = ch;
            } else if (ch == '!') {
                required = true;
            } else {
                type = getValueType(ch);
                final Map<Class<?>, Converter<?, ? extends Throwable>> map = TypeHandler.createDefaultMap();
                // Backward compatibility (probably).
                map.put(FILES_VALUE, unsupported());
                converter = new TypeHandler(map).getConverter(getValueType(ch));
            }
        }

        if (opt != Char.SP) {
            final Option option = Option.builder(String.valueOf(opt)).hasArg(type != null).required(required).type(type).get();

            // we have a final one to deal with
            options.addOption(option);
        }

        return options;
    }

    @SuppressWarnings("unchecked")
    static <T> T unsupported() {
        return (T) UNSUPPORTED;
    }

    /**
     * Constructs an instance of this utility class.
     *
     * @deprecated This constructor will be made private, or this class will be made final.
     */
    @Deprecated
    public PatternOptionBuilder() {
        // empty
    }
}
