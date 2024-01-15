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

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The definition of the functional interface to call when verifying a string
 * for input Like {@code Predicate<String>} but can throw a RuntimeException.
 * @since 1.7.0
 */
public final class Verifier {
    
    private  Verifier() { 
        // do not instantiate
    }

    /**
     * Default verifier. Always returns {@code true}.
     */
    public static final Predicate<String> DEFAULT = s -> true;

    /**
     * The Regex Pattern for the number matching.
     */
    public static final Pattern NUMBER_PATTERN = Pattern.compile("[-+]?(\\d*\\.)?(\\d+)$");

    /**
     * Verifies that a number string is either a valid real number (e.g. may have a
     * decimal point) or an integer.
     */
    public static final Predicate<String> NUMBER = s -> NUMBER_PATTERN.matcher(s).matches();

    /**
     * The Regex Pattern for the integer matching.
     */
    public static final Pattern INTEGER_PATTERN = Pattern.compile("[-+]?\\d+");

    /**
     * Verifies that a number string is an integer.
     */
    public static final Predicate<String> INTEGER = s -> INTEGER_PATTERN.matcher(s).matches();

    /**
     * The Regex Pattern that matches valid class names.
     */
    public static final Pattern CLAZZ_PATTERN = Pattern.compile(
            "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(\\.\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)*");

    /**
     * Verifies that a class name is valid.
     */
    public static final Predicate<String> CLASS = s -> CLAZZ_PATTERN.matcher(s).matches();

    /**
     * Constructs a {@code Predicate<String>} from an exemplar of an Enum. For
     * example {@code new EnumVerifier(java.time.format.TextStyle.FULL)} would
     * create an {@code Predicate<String>} that would accept the names for any of
     * the {@code java.time.format.TextStyle} values.
     * @param exemplar One of the values from the accepted Enum.
     * @return A {@code Predicate<String>} that matches the Enum names.
     */
    public static Predicate<String> enumVerifier(final Enum<?> exemplar) {
        return new Predicate<String>() {
            /** The list of valid names */
            private final List<String> names = Arrays.stream(exemplar.getDeclaringClass().getEnumConstants())
                    .map(t -> ((Enum<?>) t).name()).collect(Collectors.toList());
            @Override
            public boolean test(final String str) throws RuntimeException {
                return names.contains(str);
            }
        };
    }

}
