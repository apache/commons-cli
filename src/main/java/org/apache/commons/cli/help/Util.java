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

package org.apache.commons.cli.help;

import java.util.Arrays;

/**
 * Contains useful helper methods for classes within this package.
 */
final class Util {

    /**
     * Returns the {@code defaultValue} if {@code str} is empty.
     *
     * @param str          The string to check
     * @param defaultValue the default value if the string is empty.
     * @param <T>          The type of arguments.
     * @return the {@code defaultValue} if {@code str} is empty,
     */
    static <T extends CharSequence> T defaultValue(final T str, final T defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * Finds the index of the first non whitespace character.
     *
     * @param text     the text to search in.
     * @param startPos the starting position to search from.
     * @return the index of the first non whitespace character or -1 if non found.
     */
    static int indexOfNonWhitespace(final CharSequence text, final int startPos) {
        if (isEmpty(text)) {
            return -1;
        }
        // the line ends before the max wrap pos or a new line char found
        int idx = startPos;
        while (idx < text.length() && isWhitespace(text.charAt(idx))) {
            idx++;
        }
        return idx < text.length() ? idx : -1;
    }

    /**
     * Tests whether the given string is null or empty.
     *
     * @param str The string to test.
     * @return Whether the given string is null or empty.
     */
    static boolean isEmpty(final CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Works around https://bugs.java.com/bugdatabase/view_bug?bug_id=8341522
     *
     * Affected Version: 8,11,17,21,24
     */
    static boolean isWhitespace(final char c) {
        return Character.isWhitespace(c) || Character.PARAGRAPH_SEPARATOR == c;
    }

    /**
     * Removes the leading whitespace from the specified String.
     *
     * @param s The String to remove the leading padding from.
     * @return The String of without the leading padding
     */
    static String ltrim(final String s) {
        final int pos = indexOfNonWhitespace(s, 0);
        return pos == -1 ? "" : s.substring(pos);
    }

    /**
     * Constructs a string of specified length filled with the specified char.
     *
     * @param len      the length of the final string.
     * @param fillChar the character to file it will.
     * @return A string of specified length filled with the specified char.
     */
    static String repeat(final int len, final char fillChar) {
        final char[] padding = new char[len];
        Arrays.fill(padding, fillChar);
        return new String(padding);
    }

    /**
     * Creates a String of padding of length {@code len}.
     *
     * @param len The length of the String of padding to create.
     *
     * @return The String of padding
     */
    static String repeatSpace(final int len) {
        return repeat(len, ' ');
    }

    /**
     * Removes the trailing whitespace from the specified String.
     *
     * @param s The String to remove the trailing padding from.
     * @return The String of without the trailing padding
     */
    static String rtrim(final String s) {
        if (isEmpty(s)) {
            return s;
        }
        int pos = s.length();
        while (pos > 0 && isWhitespace(s.charAt(pos - 1))) {
            --pos;
        }
        return s.substring(0, pos);
    }

    private Util() {
        // no instances
    }
}
