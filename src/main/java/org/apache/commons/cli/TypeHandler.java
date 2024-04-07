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

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * TypeHandler will handle the pluggable conversion and verification of Option types. It handles the mapping of classes to bot converters and verifiers. It
 * provides the default conversion and verification methods when converters and verifiers are not explicitly set.
 * <p>
 * If Options are serialized and deserialized their converters and verifiers will revert to the defaults defined in this class. To correctly de-serialize
 * Options with custom converters and/or verifiers, using the default serialization methods, this class should be properly configured with the custom converters
 * and verifiers for the specific class.
 * </p>
 */
public class TypeHandler {

    /** Value of hex conversion of strings */
    private static final int HEX_RADIX = 16;

    /**
     * Map of Class to Converter.
     * <p>
     * The Class type parameter matches the Converter's first generic type.
     * </p>
     */
    private static Map<Class<?>, Converter<?, ? extends Throwable>> converterMap = new HashMap<>();

    static {
        resetConverters();
    }

    /**
     * Unregisters all Converters.
     *
     * @since 1.7.0
     */
    public static void clear() {
        converterMap.clear();
    }

    /**
     * Returns the class whose name is {@code className}.
     *
     * @param className the class name
     * @return The class if it is found
     * @throws ParseException if the class could not be found
     * @deprecated use {@link #createValue(String, Class)}
     */
    @Deprecated // since 1.7.0
    public static Class<?> createClass(final String className) throws ParseException {
        return createValue(className, Class.class);
    }

    /**
     * Returns the date represented by {@code string}.
     * <p>
     * This method is not yet implemented and always throws an {@link UnsupportedOperationException}.
     * </p>
     *
     * @param string the date string
     * @return The date if {@code string} is a valid date string, otherwise return null.
     * @deprecated use {@link #createValue(String, Class)}
     */
    @Deprecated // since 1.7.0
    public static Date createDate(final String string) {
        return createValueUnchecked(string, Date.class);
    }

    /**
     * Returns the File represented by {@code string}.
     *
     * @param string the File location
     * @return The file represented by {@code string}.
     * @deprecated use {@link #createValue(String, Class)}
     */
    @Deprecated // since 1.7.0
    public static File createFile(final String string) {
        return createValueUnchecked(string, File.class);
    }

    /**
     * Creates the File[] represented by {@code string}.
     *
     * <p>
     * This method is not yet implemented and always throws an {@link UnsupportedOperationException}.
     * </p>
     *
     * @param string the paths to the files
     * @return The File[] represented by {@code string}.
     * @throws UnsupportedOperationException always
     * @deprecated with no replacement
     */
    @Deprecated // since 1.7.0
    public static File[] createFiles(final String string) {
        // to implement/port:
        // return FileW.findFiles(string);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Creates a number from a String. If a '.' is present, it creates a Double, otherwise a Long.
     *
     * @param string the value
     * @return the number represented by {@code string}
     * @throws ParseException if {@code string} is not a number
     */
    @Deprecated // since 1.7.0
    public static Number createNumber(final String string) throws ParseException {
        return createValue(string, Number.class);
    }

    /**
     * Creates an Object from the class name and empty constructor.
     *
     * @param className the argument value
     * @return the initialized object
     * @throws ParseException if the class could not be found or the object could not be created
     * @deprecated use {@link #createValue(String, Class)}
     */
    @Deprecated // since 1.7.0
    public static Object createObject(final String className) throws ParseException {
        return createValue(className, Object.class);
    }

    /**
     * Creates the URL represented by {@code string}.
     *
     * @param string the URL string
     * @return The URL in {@code string} is well-formed
     * @throws ParseException if the URL in {@code string} is not well-formed
     * @deprecated use {@link #createValue(String, Class)}
     */
    @Deprecated // since 1.7.0
    public static URL createURL(final String string) throws ParseException {
        return createValue(string, URL.class);
    }

    /**
     * Creates the @code Object} of type {@code clazz} with the value of {@code string}.
     *
     * @param string the command line value
     * @param clazz  the class representing the type of argument
     * @param <T>    type of argument
     * @return The instance of {@code clazz} initialized with the value of {@code string}.
     * @throws ParseException if the value creation for the given class threw an exception.
     */
    @SuppressWarnings("unchecked") // returned value will have type T because it is fixed by clazz
    public static <T> T createValue(final String string, final Class<T> clazz) throws ParseException {
        try {
            return (T) getConverter(clazz).apply(string);
        } catch (final Throwable e) {
            throw ParseException.wrap(e);
        }
    }

    /**
     * Creates the {@code Object} of type {@code obj} with the value of {@code string}.
     *
     * @param string the command line value
     * @param obj    the type of argument
     * @return The instance of {@code obj} initialized with the value of {@code string}.
     * @throws ParseException if the value creation for the given object type failed
     * @deprecated use {@link #createValue(String, Class)}
     */
    @Deprecated // since 1.7.0
    public static Object createValue(final String string, final Object obj) throws ParseException {
        return createValue(string, (Class<?>) obj);
    }

    /**
     * Delegates to {@link #createValue(String, Class)} throwing IllegalArgumentException instead of ParseException.
     *
     * @param string the command line value
     * @param clazz  the class representing the type of argument
     * @param <T>    type of argument
     * @return The instance of {@code clazz} initialized with the value of {@code string}.
     * @throws IllegalArgumentException if the value creation for the given class threw an exception.
     */
    private static <T> T createValueUnchecked(final String string, final Class<T> clazz) {
        try {
            return createValue(string, clazz);
        } catch (final ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Gets the registered converter for the the Class, or {@link Converter#DEFAULT} if absent.
     *
     * @param clazz The Class to get the Converter for.
     * @return the registered converter if any, {@link Converter#DEFAULT} otherwise.
     * @since 1.7.0
     */
    public static Converter<?, ?> getConverter(final Class<?> clazz) {
        return converterMap.getOrDefault(clazz, Converter.DEFAULT);
    }

    /**
     * Returns the opened FileInputStream represented by {@code string}.
     *
     * @param string the file location
     * @return The file input stream represented by {@code string}.
     * @throws ParseException if the file is not exist or not readable
     * @deprecated use {@link #createValue(String, Class)}
     */
    @Deprecated // since 1.7.0
    public static FileInputStream openFile(final String string) throws ParseException {
        return createValue(string, FileInputStream.class);
    }

    /**
     * Registers a Converter for a Class. If {@code converter} is null registration is cleared for {@code clazz}, and no converter will be used in processing.
     *
     * @param clazz     the Class to register the Converter and Verifier to.
     * @param converter The Converter to associate with Class. May be null.
     * @since 1.7.0
     */
    public static void register(final Class<?> clazz, final Converter<?, ? extends Throwable> converter) {
        if (converter == null) {
            converterMap.remove(clazz);
        } else {
            converterMap.put(clazz, converter);
        }
    }

    /**
     * Resets the registered Converters to the default state.
     *
     * @since 1.7.0
     */
    public static void resetConverters() {
        converterMap.clear();
        converterMap.put(Object.class, Converter.OBJECT);
        converterMap.put(Class.class, Converter.CLASS);
        converterMap.put(Date.class, Converter.DATE);
        converterMap.put(File.class, Converter.FILE);
        converterMap.put(Path.class, Converter.PATH);
        converterMap.put(Number.class, Converter.NUMBER);
        converterMap.put(URL.class, Converter.URL);
        converterMap.put(FileInputStream.class, FileInputStream::new);
        converterMap.put(Long.class, Long::parseLong);
        converterMap.put(Integer.class, Integer::parseInt);
        converterMap.put(Short.class, Short::parseShort);
        converterMap.put(Byte.class, Byte::parseByte);
        converterMap.put(Character.class, s -> s.startsWith("\\u") ? Character.toChars(Integer.parseInt(s.substring(2), HEX_RADIX))[0] : s.charAt(0));
        converterMap.put(Double.class, Double::parseDouble);
        converterMap.put(Float.class, Float::parseFloat);
        converterMap.put(BigInteger.class, BigInteger::new);
        converterMap.put(BigDecimal.class, BigDecimal::new);
    }
}
