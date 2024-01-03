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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.converters.Converter;
import org.apache.commons.cli.converters.Verifier;

/**
 * This is a temporary implementation. TypeHandler will handle the pluggableness
 * of OptionTypes and it will direct all of these types of conversion
 * functionalities to ConvertUtils component in Commons already. BeanUtils I
 * think.
 */
public class TypeHandler {

    /** Map of classes to converters. */
    private static Map<Class<?>, Converter<?>> converterMap = new HashMap<>();
    
    /** Map of classes to verifiers. */
    private static Map<Class<?>, Verifier> verifierMap = new HashMap<>();

    static {
        resetConverters();
        resetVerifiers();
    }

    public static void resetConverters() {
        converterMap.clear();
        converterMap.put(Object.class, Converter.OBJECT);
        converterMap.put(Class.class, Converter.CLASS);
        converterMap.put(Date.class, Converter.DATE);
        converterMap.put(File.class, Converter.FILE);
        converterMap.put(Number.class, Converter.NUMBER);
        converterMap.put(URL.class, Converter.URL);
        converterMap.put(FileInputStream.class, s -> new FileInputStream(s));
        converterMap.put(Long.class, Long::parseLong);
        converterMap.put(Integer.class, Integer::parseInt);
        converterMap.put(Short.class, Short::parseShort);
        converterMap.put(Byte.class, Byte::parseByte);
        converterMap.put(Character.class, s -> s.charAt(0));
        converterMap.put(Double.class, Double::parseDouble);
        converterMap.put(Float.class, Float::parseFloat);
        converterMap.put(BigInteger.class, s -> new BigInteger(s));
        converterMap.put(BigDecimal.class, s -> new BigDecimal(s));
    }

    public static void resetVerifiers() {
        verifierMap.clear();
        verifierMap.put(Object.class, Verifier.CLASS);
        verifierMap.put(Class.class, Verifier.CLASS);
        verifierMap.put(Number.class, Verifier.NUMBER);

        Verifier intVerifier = s -> s.matches("-?\\d+");
        verifierMap.put(Long.class, intVerifier);
        verifierMap.put(Integer.class, intVerifier);
        verifierMap.put(Short.class, intVerifier);
        verifierMap.put(Byte.class, intVerifier);

        verifierMap.put(Double.class, Verifier.NUMBER);
        verifierMap.put(Float.class, Verifier.NUMBER);
        verifierMap.put(BigInteger.class, intVerifier);
        verifierMap.put(BigDecimal.class, Verifier.NUMBER);
    }

    public static void register(Class<?> clazz, Converter<?> converter, Verifier verifier) {
        if (converter == null) {
            converterMap.remove(clazz);
        } else {
            converterMap.put(clazz, converter);
        }

        if (verifier == null) {
            verifierMap.remove(clazz);
        } else {
            verifierMap.put(clazz, verifier);
        }
    }

    public static Converter<?> getConverter(Class<?> clazz) {
        Converter<?> converter = converterMap.get(clazz);
        return converter == null ? Converter.DEFAULT : converter;
    }

    public static Verifier getVerifier(Class<?> clazz) {
        Verifier verifier = verifierMap.get(clazz);
        return verifier == null ? Verifier.DEFAULT : verifier;
    }

    /**
     * Returns the class whose name is {@code className}.
     *
     * @param      className      the class name
     * @return                    The class if it is found
     * @throws     ParseException if the class could not be found
     * @deprecated     use {@link #createValue(String, Class)}
     */
    @Deprecated // sinze 1.7
    public static Class<?> createClass(final String className) throws ParseException {
        return createValue(className, Class.class);
    }

    /**
     * Returns the date represented by {@code str}. <p> This method is not yet
     * implemented and always throws an {@link UnsupportedOperationException}.
     *
     * @param      str the date string
     * @return         The date if {@code str} is a valid date string, otherwise
     *                 return null.
     * @deprecated     use {@link #createValue(String, Class)}
     */
    @Deprecated // sinze 1.7
    public static Date createDate(final String str) {
        try {
            return createValue(str, Date.class);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the File represented by {@code str}.
     *
     * @param      str the File location
     * @return         The file represented by {@code str}.
     * @deprecated     use {@link #createValue(String, Class)}
     */
    @Deprecated // sinze 1.7
    public static File createFile(final String str) {
        try {
            return createValue(str, File.class);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the File[] represented by {@code str}. <p> This method is not yet
     * implemented and always throws an {@link UnsupportedOperationException}.
     *
     * @param      str                           the paths to the files
     * @return                                   The File[] represented by
     *                                           {@code str}.
     * @throws     UnsupportedOperationException always
     * @deprecated                               with no replacement
     */
    @Deprecated // sinze 1.7
    public static File[] createFiles(final String str) {
        // to implement/port:
        // return FileW.findFiles(str);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Create a number from a String. If a '.' is present, it creates a Double,
     * otherwise a Long.
     *
     * @param      str            the value
     * @return                    the number represented by {@code str}
     * @throws     ParseException if {@code str} is not a number
     */
    @Deprecated // sinze 1.7
    public static Number createNumber(final String str) throws ParseException {
        return createValue(str, Number.class);
    }

    /**
     * Create an Object from the class name and empty constructor.
     *
     * @param      className      the argument value
     * @return                    the initialized object
     * @throws     ParseException if the class could not be found or the object
     *                            could not be created
     * @deprecated     use {@link #createValue(String, Class)}
     */
    @Deprecated // sinze 1.7
    public static Object createObject(final String className) throws ParseException {
        return createValue(className, Object.class);
    }

    /**
     * Returns the URL represented by {@code str}.
     *
     * @param      str            the URL string
     * @return                    The URL in {@code str} is well-formed
     * @throws     ParseException if the URL in {@code str} is not well-formed
     * @deprecated     use {@link #createValue(String, Class)}
     */
    @Deprecated // sinze 1.7
    public static URL createURL(final String str) throws ParseException {
        return createValue(str, URL.class);
    }

    /**
     * Returns the {@code Object} of type {@code clazz} with the value of
     * {@code str}.
     *
     * @param  str            the command line value
     * @param  clazz          the class representing the type of argument
     * @param  <T>            type of argument
     * @return                The instance of {@code clazz} initialized with the
     *                        value of {@code str}.
     * @throws ParseException if the value creation for the given class failed
     */
    @SuppressWarnings("unchecked") // returned value will have type T because it is fixed by clazz
    public static <T> T createValue(final String str, final Class<T> clazz) throws ParseException {
        try {
            return (T) getConverter(clazz).apply(str);
        } catch (Exception e) {
            throw ParseException.wrap(e);
        }
    }

    /**
     * Returns the {@code Object} of type {@code obj} with the value of {@code str}.
     *
     * @param      str            the command line value
     * @param      obj            the type of argument
     * @return                    The instance of {@code obj} initialized with the
     *                            value of {@code str}.
     * @throws     ParseException if the value creation for the given object type
     *                            failed
     * @deprecated     use {@link #createValue(String, Class)}
     */
    @Deprecated // since 1.7
    public static Object createValue(final String str, final Object obj) throws ParseException {
        return createValue(str, (Class<?>) obj);
    }

    /**
     * Returns the opened FileInputStream represented by {@code str}.
     *
     * @param      str            the file location
     * @return                    The file input stream represented by {@code str}.
     * @throws     ParseException if the file is not exist or not readable
     * @deprecated     use {@link #createValue(String, Class)}
     */
    @Deprecated // since 1.7
    public static FileInputStream openFile(final String str) throws ParseException {
        return createValue(str, FileInputStream.class);
    }
}
