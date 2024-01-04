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

import java.util.regex.Pattern;

/**
 * The definition of the functional interface to call when verifying a string
 * for input Like {@code Predicate<String>} but can throw a RuntimeException.
 * 
 */
@FunctionalInterface
public interface Verifier {

    /**
     * Default verifier. Always returns {@code true}.
     */
    Verifier DEFAULT = s -> true;

    /**
     * The Regex Pattern for the number matching.
     */
    Pattern NUMBER_PATTERN = Pattern.compile("-?([0-9]*\\.)?([0-9]+)$");

    /**
     * Verifies that a number string is either a valid real number (e.g. may have a decimal
     * point) or an integer.
     */
    Verifier NUMBER = s -> NUMBER_PATTERN.matcher(s).matches();
    
    /**
     * The Regex Pattern for the integer matching.
     */
    Pattern INTEGER_PATTERN = Pattern.compile("-?\\d+");

    /**
     * Verifies that a number string is an integer.
     */
    Verifier INTEGER = s -> INTEGER_PATTERN.matcher(s).matches();

    /**
     * The Regex Pattern that matches valid class names.
     */
    Pattern CLAZZ_PATTERN = Pattern.compile(
            "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(\\.\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)*");

    /**
     * Verifies that a class name is valid.
     */
    Verifier CLASS = s -> CLAZZ_PATTERN.matcher(s).matches();

    /**
     * Applies the verification function to the String argument.
     * @param  str              the String to convert
     * @return                  {@code true} if the string is valid, {@code false}
     *                          otherwise.
     * @throws RuntimeException on error.
     */
    boolean test(String str) throws RuntimeException;
}
