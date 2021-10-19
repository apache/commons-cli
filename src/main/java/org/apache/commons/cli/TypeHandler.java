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
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * This is a temporary implementation. TypeHandler will handle the pluggableness of OptionTypes and it will direct all
 * of these types of conversion functionalities to ConvertUtils component in Commons already. BeanUtils I think.
 */
public class TypeHandler {
    /**
     * Returns the class whose name is {@code classname}.
     *
     * @param classname the class name
     * @return The class if it is found
     * @throws ParseException if the class could not be found
     */
    public static Class<?> createClass(final String classname) throws ParseException {
        try {
            return Class.forName(classname);
        } catch (final ClassNotFoundException e) {
            throw new ParseException("Unable to find the class: " + classname);
        }
    }

    /**
     * Returns the date represented by {@code str}.
     * <p>
     * This method is not yet implemented and always throws an {@link UnsupportedOperationException}.
     *
     * @param str the date string
     * @return The date if {@code str} is a valid date string, otherwise return null.
     * @throws UnsupportedOperationException always
     */
    public static Date createDate(final String str) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns the File represented by {@code str}.
     *
     * @param str the File location
     * @return The file represented by {@code str}.
     */
    public static File createFile(final String str) {
        return new File(str);
    }

    /**
     * Returns the File[] represented by {@code str}.
     * <p>
     * This method is not yet implemented and always throws an {@link UnsupportedOperationException}.
     *
     * @param str the paths to the files
     * @return The File[] represented by {@code str}.
     * @throws UnsupportedOperationException always
     */
    public static File[] createFiles(final String str) {
        // to implement/port:
        // return FileW.findFiles(str);
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Create a number from a String. If a . is present, it creates a Double, otherwise a Long.
     *
     * @param str the value
     * @return the number represented by {@code str}
     * @throws ParseException if {@code str} is not a number
     */
    public static Number createNumber(final String str) throws ParseException {
        try {
            if (str.indexOf('.') != -1) {
                return Double.valueOf(str);
            }
            return Long.valueOf(str);
        } catch (final NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Create an Object from the classname and empty constructor.
     *
     * @param classname the argument value
     * @return the initialized object
     * @throws ParseException if the class could not be found or the object could not be created
     */
    public static Object createObject(final String classname) throws ParseException {
        final Class<?> cl;

        try {
            cl = Class.forName(classname);
        } catch (final ClassNotFoundException cnfe) {
            throw new ParseException("Unable to find the class: " + classname);
        }

        try {
            return cl.newInstance();
        } catch (final Exception e) {
            throw new ParseException(e.getClass().getName() + "; Unable to create an instance of: " + classname);
        }
    }

    /**
     * Returns the URL represented by {@code str}.
     *
     * @param str the URL string
     * @return The URL in {@code str} is well-formed
     * @throws ParseException if the URL in {@code str} is not well-formed
     */
    public static URL createURL(final String str) throws ParseException {
        try {
            return new URL(str);
        } catch (final MalformedURLException e) {
            throw new ParseException("Unable to parse the URL: " + str);
        }
    }

    /**
     * Returns the {@code Object} of type {@code clazz} with the value of {@code str}.
     *
     * @param str the command line value
     * @param clazz the class representing the type of argument
     * @param <T> type of argument
     * @return The instance of {@code clazz} initialized with the value of {@code str}.
     * @throws ParseException if the value creation for the given class failed
     */
    @SuppressWarnings("unchecked") // returned value will have type T because it is fixed by clazz
    public static <T> T createValue(final String str, final Class<T> clazz) throws ParseException {
        if (PatternOptionBuilder.STRING_VALUE == clazz) {
            return (T) str;
        }
        if (PatternOptionBuilder.OBJECT_VALUE == clazz) {
            return (T) createObject(str);
        }
        if (PatternOptionBuilder.NUMBER_VALUE == clazz) {
            return (T) createNumber(str);
        }
        if (PatternOptionBuilder.DATE_VALUE == clazz) {
            return (T) createDate(str);
        }
        if (PatternOptionBuilder.CLASS_VALUE == clazz) {
            return (T) createClass(str);
        }
        if (PatternOptionBuilder.FILE_VALUE == clazz) {
            return (T) createFile(str);
        }
        if (PatternOptionBuilder.EXISTING_FILE_VALUE == clazz) {
            return (T) openFile(str);
        }
        if (PatternOptionBuilder.FILES_VALUE == clazz) {
            return (T) createFiles(str);
        }
        if (PatternOptionBuilder.URL_VALUE == clazz) {
            return (T) createURL(str);
        }
        throw new ParseException("Unable to handle the class: " + clazz);
    }

    /**
     * Returns the {@code Object} of type {@code obj} with the value of {@code str}.
     *
     * @param str the command line value
     * @param obj the type of argument
     * @return The instance of {@code obj} initialized with the value of {@code str}.
     * @throws ParseException if the value creation for the given object type failed
     */
    public static Object createValue(final String str, final Object obj) throws ParseException {
        return createValue(str, (Class<?>) obj);
    }

    /**
     * Returns the opened FileInputStream represented by {@code str}.
     *
     * @param str the file location
     * @return The file input stream represented by {@code str}.
     * @throws ParseException if the file is not exist or not readable
     */
    public static FileInputStream openFile(final String str) throws ParseException {
        try {
            return new FileInputStream(str);
        } catch (final FileNotFoundException e) {
            throw new ParseException("Unable to find file: " + str);
        }
    }
}
