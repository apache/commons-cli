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
package org.apache.commons.cli.converters;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The definition of the functional interface to call when doing a conversion.
 * Like {@code Function<String,T>} but can throw an Exception.
 *
 * @param <T> The return type for the function.
 */
@FunctionalInterface
public interface Converter<T> {

    /**
     * The default converter. Does nothing.
     */
    Converter<?> DEFAULT = s -> s;

    /**
     * Class name converter. Calls {@code Class.forName}.
     */
    Converter<Class<?>> CLASS = s -> Class.forName(s);

    /**
     * File name converter. Calls @{code new File(s)}
     */
    Converter<File> FILE = s -> new File(s);

    /**
     * Number converter. Converts to a Double if a decimal point ('.') is in the
     * string or a Long otherwise.
     */
    Converter<Number> NUMBER = s -> {
        if (s.indexOf('.') != -1) {
            return Double.valueOf(s);
        }
        return Long.valueOf(s);
    };

    /**
     * Converts a class name to an instance of the class. Uses the Class converter
     * to find the class and then call the default constructor.
     * @see #CLASS
     */
    Converter<Object> OBJECT = s -> CLASS.apply(s).getConstructor().newInstance();

    /**
     * Creates a URL. Calls {@code new URL(s)}.
     */
    Converter<URL> URL = s -> new URL(s);

    /**
     * The simple date format used for the Date conversion. Format is: "EEE MMM dd
     * HH:mm:ss zzz yyyy"
     */
    SimpleDateFormat DATE_FMT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

    /**
     * Converts to a date using the {@code DATE_FMT}.
     * @see #DATE_FMT
     */
    Converter<Date> DATE = s -> DATE_FMT.parse(s);

    /**
     * Applies the conversion function to the String argument.
     * @param  str       the String to convert
     * @return           the Object from the conversion.
     * @throws Exception on error.
     */
    T apply(String str) throws Exception;
}
