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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class OptionValidatorTest {

    /*
     * Exemplars of various types of characters
     */

    private static final String LETTERS = "a\u00D1"; // a and Ñ

    // '\u0660' through '\u0669', Arabic-Indic digits, '\u06F0' through '\u06F9',
    // Extended Arabic-Indic digits
    // '\u0966' through '\u096F', Devanagari digits, '\uFF10' through '\uFF19',
    // Fullwidth digits
    private static final String DIGITS = "1\u0661\u06f2\u0968\uFF14";

    private static final String CURRENCY = "€$";

    // this is the complete puncutation set do not modify it as Character.isJavaIdentifierPart filters
    // the good and bad ones out in the setup.
    private static final String PUNCTUATION = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private static final String COMBINING_MARK = "\u0303";

    private static final String NON_SPACING_MARK = "\u0CBF";

    private static final String IDENTIFIER_IGNORABLE = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008";

    private static String acceptablePunctuation;

    private static String notAcceptablePunctuation;

    private static String additionalOptonChars;
    private static String additionalLongChars;

    private static String firstChars;
    private static String notFirstChars;

    private static String restChars;
    private static String notRestChars;

    private static Stream<Arguments> optionParameters() {

        final List<Arguments> args = new ArrayList<>();

        args.add(Arguments.of("CamelCase", true, "Camel case error"));
        args.add(Arguments.of("Snake_case", true, "Snake case error"));
        args.add(Arguments.of("_leadingUnderscore", true, "Leading underscore error"));
        args.add(Arguments.of("kabob-case", true, "Kabob case error"));
        args.add(Arguments.of("-leadingDash", false, "Leading dash error"));
        args.add(Arguments.of("lowercase", true, "Lower case error"));
        args.add(Arguments.of("UPPERCASE", true, "Upper case error"));

        // build passing test cases
        for (final char c : firstChars.toCharArray()) {
            final String s = String.format("%sMoreText", c);
            args.add(Arguments.of(s, true, String.format("testing: First character '%s'", c)));
        }

        for (final char c : restChars.toCharArray()) {
            final String s = String.format("Some%sText", c);
            args.add(Arguments.of(s, true, String.format("testing: Middle character '%s'", c)));
        }

        // build failing test cases
        for (final char c : notFirstChars.toCharArray()) {
            final String s = String.format("%sMoreText", c);
            args.add(Arguments.of(s, false, String.format("testing: Bad first character '%s'", c)));
        }

        for (final char c : notRestChars.toCharArray()) {
            final String s = String.format("Some%sText", c);
            args.add(Arguments.of(s, false, String.format("testing: Bad middle character '%s'", c)));
        }

        return args.stream();
    }

    @BeforeAll
    public static void setup() {
        StringBuilder sb = new StringBuilder();
        final StringBuilder sb2 = new StringBuilder();
        int idx;

        for (final char c : PUNCTUATION.toCharArray()) {
            if (Character.isJavaIdentifierPart(c)) {
                sb.append(c);
            } else {
                sb2.append(c);
            }
        }
        acceptablePunctuation = sb.toString();
        notAcceptablePunctuation = sb2.toString();

        sb = new StringBuilder();
        for (final char c : OptionValidator.ADDITIONAL_LONG_CHARS) {
            sb.append(c);
        }
        additionalLongChars = sb.toString();

        sb = new StringBuilder();
        for (final char c : OptionValidator.ADDITIONAL_OPTION_CHARS) {
            sb.append(c);
        }
        additionalOptonChars = sb.toString();

        final String javaIdentifierPart = LETTERS + DIGITS + CURRENCY + acceptablePunctuation + COMBINING_MARK
                + NON_SPACING_MARK + IDENTIFIER_IGNORABLE;

        firstChars = additionalOptonChars + javaIdentifierPart;

        sb = new StringBuilder(notAcceptablePunctuation).append(additionalLongChars);
        for (final char c : OptionValidator.ADDITIONAL_OPTION_CHARS) {
            while ((idx = sb.indexOf(Character.toString(c))) > -1) {
                sb.deleteCharAt(idx);
            }
        }
        notFirstChars = sb.toString();

        restChars = additionalLongChars + javaIdentifierPart;
        sb = new StringBuilder(notAcceptablePunctuation).append(additionalOptonChars);
        for (final char c : OptionValidator.ADDITIONAL_LONG_CHARS) {
            while ((idx = sb.indexOf(Character.toString(c))) > -1) {
                sb.deleteCharAt(idx);
            }
        }
        notRestChars = sb.toString();

    }

    @Test
    public void testExclusivity() {
        /* since we modify acceptable chars by add and removing ADDITIONAL* chars we must verify that they do not exist in the
         * base javaIdentiferPart that is used in OptionValidator to validate basic characters  */
        for (final char c : OptionValidator.ADDITIONAL_LONG_CHARS) {
            assertFalse(Character.isJavaIdentifierPart(c), () -> String.format("'%s' should not be in 'ADDITIONAL_LONG_CHARS", c));
        }
        for (final char c : OptionValidator.ADDITIONAL_OPTION_CHARS) {
            assertFalse(Character.isJavaIdentifierPart(c), () -> String.format("'%s' should not be in 'ADDITIONAL_OPTION_CHARS", c));
        }
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("optionParameters")
    public void validateTest(final String str, final boolean expected, final String name) {
        if (expected) {
            assertEquals(str, OptionValidator.validate(str));
        } else {
            assertThrows(IllegalArgumentException.class, () -> OptionValidator.validate(str));
        }
    }
}
