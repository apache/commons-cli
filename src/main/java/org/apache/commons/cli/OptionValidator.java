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

/**
 * Validates an Option string.
 */
final class OptionValidator {
    /* package private for testing access */
    /** The array of additional characters allowed as the first character in the option but not in the rest of the option */
    static final char[] ADDITIONAL_OPTION_CHARS = {'?', '@'};
    /** The array of additional characters allowed in the rest of the option but not in the first position */
    static final char[] ADDITIONAL_LONG_CHARS = {'-'};

    /**
     * Returns whether the specified character is a valid character.
     * A character is valid if any of the following conditions are true:
     * <ul>
     * <li>it is a letter</li>
     * <li>it is a currency symbol (such as '$')</li>
     * <li>it is a connecting punctuation character (such as '_')</li>
     * <li>it is a digit</li>
     * <li>it is a numeric letter (such as a Roman numeral character)</li>
     * <li>it is a combining mark</li>
     * <li>it is a non-spacing mark</li>
     * <li>isIdentifierIgnorable returns true for the character</li>
     * <li>it is a hyphen/dash ('-')</li>
     * </ul>
     * @param c the character to validate
     * @return true if {@code c} is a valid character letter.
     */
    private static boolean isValidChar(final char c) {
        return Character.isJavaIdentifierPart(c) || search(ADDITIONAL_LONG_CHARS, c);
    }

    /**
     * Returns whether the specified character is a valid Option.
     * A character is valid if any of the following conditions are true:
     * <ul>
     * <li>it is a letter</li>
     * <li>it is a currency symbol (such as '$')</li>
     * <li>it is a connecting punctuation character (such as '_')</li>
     * <li>it is a digit</li>
     * <li>it is a numeric letter (such as a Roman numeral character)</li>
     * <li>it is a combining mark</li>
     * <li>it is a non-spacing mark</li>
     * <li>isIdentifierIgnorable returns true for the character</li>
     * <li>it is a question mark or 'at' sign ('?' or '@')</li>
     * </ul>
     * @param c the option to validate
     * @return true if {@code c} is a letter, '?' or '@', otherwise false.
     */
    private static boolean isValidOpt(final char c) {
        return Character.isJavaIdentifierPart(c) || search(ADDITIONAL_OPTION_CHARS, c);
    }

    /**
     * Checks the char array for a matching char.
     * @param chars the char array to search
     * @param c the char to look for.
     * @return {@code true} if {@code c} was in {@code ary}, {@code false} otherwise.
     */
    private static boolean search(final char[] chars, final char c) {
        for (final char a : chars) {
            if (a == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates whether {@code opt} is a permissible Option shortOpt. The rules that specify if the {@code opt}
     * is valid are:
     *
     * <ul>
     * <li>a single character {@code opt} that is either Chars.SP(special case), '?', '@' or a letter</li>
     * <li>a multi character {@code opt} that only contains valid characters</li>
     * </ul>
     * </p><p>
     * A character is valid if any of the following conditions are true:
     * <ul>
     * <li>it is a letter</li>
     * <li>it is a currency symbol (such as '$')</li>
     * <li>it is a connecting punctuation character (such as '_')</li>
     * <li>it is a digit</li>
     * <li>it is a numeric letter (such as a Roman numeral character)</li>
     * <li>it is a combining mark</li>
     * <li>it is a non-spacing mark</li>
     * <li>isIdentifierIgnorable returns true for the character</li>
     * <li>it is a hyphen/dash ('-')</li>
     * </ul>
     * </p><p>
     * In case {@code opt} is {@code null} no further validation is performed.
     *
     * @param option The option string to validate, may be null
     * @throws IllegalArgumentException if the Option is not valid.
     */
    static String validate(final String option) throws IllegalArgumentException {
        // if opt is null do not check further.
        if (option == null) {
            return null;
        }
        if (option.isEmpty()) {
            throw new IllegalArgumentException("Empty option name.");
        }
        final char[] chars = option.toCharArray();
        final char ch0 = chars[0];
        if (!isValidOpt(ch0)) {
            throw new IllegalArgumentException(String.format("Illegal option name '%s'.", ch0));
        }
        // handle the multi-character opt
        if (option.length() > 1) {
            for (int i = 1; i < chars.length; i++) {
                final char ch = chars[i];
                if (!isValidChar(ch)) {
                    throw new IllegalArgumentException(String.format("The option '%s' contains an illegal " + "character : '%s'.", option, ch));
                }
            }
        }
        return option;
    }
}
