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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The definition of the functional interface to call when doing a conversion. Like {@code Function<String,T>} but can throw an Exception.
 *
 * @param <T> The return type for the function.
 * @param <E> The kind of thrown exception or error.
 * @since 1.7.0
 */
@FunctionalInterface
public interface Converter<T, E extends Exception> {
    // See also Apache Commons Lang FailableFunction

    /**
     * The default converter does nothing.
     */
    Converter<?, RuntimeException> DEFAULT = s -> s;

    /**
     * Converts a String to a {@link Class}. Calls {@link Class#forName(String, boolean, ClassLoader)} with {@code initialize} set to {@code false} so that
     * naming a class does not run its static initializer.
     */
    Converter<Class<?>, ClassNotFoundException> CLASS = s -> Class.forName(s, false, Converter.class.getClassLoader());

    /**
     * Converts a String to a {@link File}. Calls {@link File#File(String)}.
     */
    Converter<File, NullPointerException> FILE = File::new;

    /**
     * Converts a String to a {@link Path}. Calls {@link Paths#get(String, String...)}.
     */
    Converter<Path, InvalidPathException> PATH = Paths::get;

    /**
     * Converts a String to a {@link Number}. Converts to a Double if a decimal point ('.') is in the string or a Long otherwise.
     */
    Converter<Number, NumberFormatException> NUMBER = s -> s.indexOf('.') != -1 ? (Number) Double.valueOf(s) : (Number) Long.valueOf(s);

    /**
     * Converts a class name to an instance of the class. Uses the Class converter to find the class and then call the default constructor.
     *
     * @see #CLASS
     */
    Converter<Object, ReflectiveOperationException> OBJECT = s -> CLASS.apply(s).getConstructor().newInstance();

    /**
     * Converts a String to a {@link URL}. Calls {@link URL#URL(String)}.
     */
    Converter<URL, MalformedURLException> URL = URL::new;

    /**
     * Converts a String to a {@link Date} using the format string Form "EEE MMM dd HH:mm:ss zzz yyyy".
     */
    Converter<Date, java.text.ParseException> DATE = s -> {
        final String pattern = "EEE MMM dd HH:mm:ss zzz yyyy";
        final SimpleDateFormat format = new SimpleDateFormat(pattern);
        // reject out-of-range fields (for example "Feb 30") instead of silently rolling them over.
        format.setLenient(false);
        try {
            return format.parse(s);
        } catch (final java.text.ParseException e) {
            // Date.toString() always emits English month/day names, so fall back to Locale.ENGLISH
            // when the default locale rejects the documented format.
            final SimpleDateFormat englishFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
            englishFormat.setLenient(false);
            return englishFormat.parse(s);
        }
    };

    /**
     * Applies the conversion function to the String argument.
     *
     * @param string The String to convert.
     * @return The Object from the conversion.
     * @throws E on error.
     */
    T apply(String string) throws E;
}
