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
import java.net.URL;
import java.util.Date;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean2;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.ConverterFacade;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.cli.converters.Func;
import org.apache.commons.cli.converters.SimpleConverter;

/**
 * TypeHandler will handles the configuration of the Option type processing using
 * the Commons BeanUtils ConvertUtilsBean class.
 */
public class TypeHandler {

    /**
     * The conversion utilities from BeanUtils.
     */
    private static ConvertUtilsBean2 convertUtils;

    /**
     * Setup the convertUtils object
     */
    static {
        convertUtils = new ConvertUtilsBean2();
        convertUtils.register(true, true, 0);
        /*
         * this can not be a ternary because the unboxing operations will result in it
         * always being a Double.
         * https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.25-300-A
         */
        Func<Number> fn = new Func<Number>() {

            @Override
            public Number apply(String str) throws ConversionException {
                if (str.indexOf('.') == -1) {
                    return Long.valueOf(str);
                }
                return Double.valueOf(str);
            }
        };
        convertUtils.register(new SimpleConverter<>(fn, Number.class), Number.class);

        Func<Object> fo = str -> {
                final Class<?> cl;

                try {
                    cl = Class.forName(str);
                } catch (final ClassNotFoundException cnfe) {
                    throw new ConversionException("Unable to find the class: " + str);
                }

                try {
                    return cl.getConstructor().newInstance();
                } catch (final Exception e) {
                    throw new ConversionException(e.getClass().getName() + "; Unable to create an instance of: " + str);
                }
            };
        convertUtils.register(new SimpleConverter<>(fo, Object.class), Object.class);

        convertUtils.register(
                new SimpleConverter<>(str -> new FileInputStream(str), FileInputStream.class),
                FileInputStream.class);

        // fixup date parsing
        DateConverter dc = new DateConverter();
        // should match "Thu Jun 06 17:48:57 EDT 2002"
        dc.setPattern("EEE MMM dd HH:mm:ss zzz yyyy");
        convertUtils.register(new ConverterFacade(dc), Date.class);
    }
    
    /**
     * Registers a Converter for a class so that the class can be returned as the value of the command line option.
     * Note: the converter will override any existing converter for the class.
     *
     * @param converter The converter to associate with the class.
     * @param clazz the class that the converter handles.
     */
    public static void register(Converter converter, Class<?> clazz) {
        convertUtils.register(converter, clazz);
    }

    /**
     * Returns the class whose name is {@code className}.
     *
     * @param      className      the class name
     * @return                    The class if it is found
     * @throws     ParseException if the class could not be found
     * @deprecated                use createValue(className,Class.class)
     */
    @Deprecated // (since="1.7")
    public static Class<?> createClass(final String className) throws ParseException {
        return createValue(className, Class.class);
    }

    /**
     * Returns the date represented by {@code str}. <p> This method is not yet
     * implemented and always throws an {@link UnsupportedOperationException}.
     *
     * @param      str                           the date string
     * @return                                   The date if {@code str} is a valid
     *                                           date string, otherwise return null.
     * @throws     UnsupportedOperationException always
     * @deprecated                               use createValue(str, Date.class);
     */
    @Deprecated // (since="1.7")
    public static Date createDate(final String str) {
        return createValueNoException(str, Date.class);
    }

    /**
     * Returns the File represented by {@code str}.
     *
     * @param      str the File location
     * @return         The file represented by {@code str}.
     * @deprecated     use createValue(str, File.class);
     */
    @Deprecated // (since="1.7")
    public static File createFile(final String str) {
        return createValueNoException(str, File.class);
    }

    /**
     * Returns the File[] represented by {@code str}. <p> This method is not yet
     * implemented and always throws an {@link UnsupportedOperationException}.
     *
     * @param  str the paths to the files
     * @return     The File[] represented by {@code str}.
     */
    @Deprecated // (since="1.7")
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
     * @deprecated                use createValue(str, Number.class);
     */
    @Deprecated // (since="1.7")
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
     * @deprecated                use createValue(str, Object.class);
     */
    @Deprecated // (since="1.7")
    public static Object createObject(final String className) throws ParseException {
        return createValue(className, Object.class);
    }

    /**
     * Returns the URL represented by {@code str}.
     *
     * @param      str            the URL string
     * @return                    The URL in {@code str} is well-formed
     * @throws     ParseException if the URL in {@code str} is not well-formed
     * @deprecated                use createValue(str, URL.class);
     */
    @Deprecated // (since="1.7")
    public static URL createURL(final String str) throws ParseException {
        return createValue(str, URL.class);
    }

    private static <T> T createValueNoException(final String str, Class<T> clazz) {
        try {
            return createValue(str, clazz);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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
    public static <T> T createValue(final String str, Class<T> clazz) throws ParseException {
        //  BeanUtils convertUtils handles File[] but looks for the string to parse into file names.
        if (PatternOptionBuilder.FILES_VALUE == clazz) {
            return (T) createFiles(str);
        }
        Converter converter = convertUtils.lookup(String.class, clazz);
        if (converter == null) {
            throw new ParseException(String.format("No registered converter for %s found", clazz));
        }
        try {
            return converter.convert(clazz, str);
        } catch (ConversionException e) {
            throw new ParseException(e);
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
     * @deprecated                use {@link #createValue(String, Class)};
     */
    @Deprecated // (since="1.7")
    public static Object createValue(final String str, final Object obj) throws ParseException {
        return createValue(str, (Class<?>) obj);
    }

    /**
     * Returns the opened FileInputStream represented by {@code str}.
     *
     * @param      str            the file location
     * @return                    The file input stream represented by {@code str}.
     * @throws     ParseException if the file is not exist or not readable
     * @deprecated                use createValue(str, URL.class);
     */
    @Deprecated // (since="1.7")
    public static FileInputStream openFile(final String str) throws ParseException {
        return createValue(str, FileInputStream.class);
    }
}
