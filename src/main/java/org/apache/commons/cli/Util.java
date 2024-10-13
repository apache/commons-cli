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
import java.util.HashSet;
import java.util.Set;

/**
 * Contains useful helper methods for classes within this package.
 * @since 1.10.0
 */
public final class Util {

    /** An array of chars that are breaks in text */
    private static final char[] BREAK_CHARS = {'\t', '\n', '\f', '\r',
            Character.LINE_SEPARATOR,
            Character.PARAGRAPH_SEPARATOR,
            '\u000B', // VERTICAL TABULATION.
            '\u001C', // FILE SEPARATOR.
            '\u001D', // GROUP SEPARATOR.
            '\u001E', // RECORD SEPARATOR.
            '\u001F', // UNIT SEPARATOR.
    };

    /** The list of characters that are breaks in text. */
    private static final Set<Character> BREAK_CHAR_SET = new HashSet<>();

    static {
        for (char c : BREAK_CHARS) {
            BREAK_CHAR_SET.add(c);
        }
    }

    /**
     * An empty immutable {@code String} array.
     */
    static final String[] EMPTY_STRING_ARRAY = {};


    private Util() {
        // no instances
    }

    /**
     * Tests whether the given array is null or empty.
     *
     * @param array the array to test.
     * @return the given array is null or empty.
     * @since 1.10.0
     */
    static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Tests whether the given string is null or empty.
     *
     * @param str The string to test.
     * @return Whether the given string is null or empty.
     * @since 1.10.0
     */
    public static boolean isEmpty(final CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Removes the leading and trailing quotes from {@code str}. E.g. if str is '"one two"', then 'one two' is returned.
     *
     * @param str The string from which the leading and trailing quotes should be removed.
     * @return The string without the leading and trailing quotes.
     */
    static String stripLeadingAndTrailingQuotes(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        final int length = str.length();
        if (length > 1 && str.startsWith("\"") && str.endsWith("\"") && str.substring(1, length - 1).indexOf('"') == -1) {
            return str.substring(1, length - 1);
        }
        return str;
    }

    /**
     * Removes the hyphens from the beginning of {@code str} and return the new String.
     *
     * @param str The string from which the hyphens should be removed.
     * @return the new String.
     */
    static String stripLeadingHyphens(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.startsWith("--")) {
            return str.substring(2);
        }
        if (str.startsWith("-")) {
            return str.substring(1);
        }
        return str;
    }

    /**
     * Finds the next text wrap position after {@code startPos} for the text in {@code text} with the column width
     * {@code width}. The wrap point is the last position before startPos+width having a whitespace character (space,
     * \n, \r). If there is no whitespace character before startPos+width, it will return startPos+width.
     *
     * @param text The text being searched for the wrap position
     * @param width width of the wrapped text
     * @param startPos position from which to start the lookup whitespace character
     * @return position on which the text must be wrapped or @{code text.length()} if the wrap position is at the end of the text.
     * @since 1.10.0
     */
    public static int findWrapPos(final CharSequence text, final int width, final int startPos) {
        if (width < 1) {
            throw new IllegalArgumentException("Width must be greater than 0");
        }
        // handle case of width > text.
        // the line ends before the max wrap pos or a new line char found
        int limit = Math.min(startPos + width, text.length() - 1);

        for (int idx = startPos; idx < limit; idx++) {
            if (BREAK_CHAR_SET.contains(text.charAt(idx))) {
                return idx;
            }
        }

        if ((startPos + width) >= text.length()) {
            return text.length();
        }

        int pos;
        // look for the last whitespace character before limit
        for (pos = limit; pos >= startPos; --pos) {
            if (isWhitespace(text.charAt(pos))) {
                break;
            }
        }
        // if we found it return it, otherwise just chop at limit
        return pos > startPos ? pos : limit - 1;
    }

    /**
     * Finds the position of the first non whitespace character.
     * @param text the text to search in.
     * @param startPos the starting position to search from.
     * @return the index of the first non whitespace character or -1 if non found.
     * @since 1.10.0
     */
    public static int findNonWhitespacePos(final CharSequence text, final int startPos) {
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
     * Removes the trailing whitespace from the specified String.
     *
     * @param s The String to remove the trailing padding from.
     * @return The String of without the trailing padding
     * @since 1.10.0
     */
    public static String rtrim(final String s) {
        if (isEmpty(s)) {
            return s;
        }
        int pos = s.length();
        while (pos > 0 && isWhitespace(s.charAt(pos - 1))) {
            --pos;
        }
        return s.substring(0, pos);
    }

    // work around for https://bugs.java.com/bugdatabase/view_bug?bug_id=8341522
    private static boolean isWhitespace(final char c) {
        return Character.isWhitespace(c) || Character.PARAGRAPH_SEPARATOR == c;
    }

    /**
     * Returns the {@code defaultValue} if {@code str} is empty.
     * @param str The string to check
     * @param defaultValue the default value if the string is empty.
     * @param <T> The type of arguments.
     * @return the {@code defaultValue} if {@code str} is empty,
     * @since 1.9.0
     */
    public static <T extends CharSequence> T defaultValue(final T str, final T defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * Constructs a string of specified length filled with the specified char.
     * @param len the length of the final string.
     * @param fillChar the character to file it will.
     * @return A string of specified length filled with the specified char.
     * @since 1.10.0
     */
    public static String filledString(final int len, final char fillChar) {
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
     * @since 1.10.0
     */
    public static String createPadding(final int len) {
        return filledString(len, ' ');
    }

    /**
     * Removes the leading whitespace from the specified String.
     *
     * @param s The String to remove the leading padding from.
     * @return The String of without the leading padding
     * @since 1.10.0
     */
    public static String ltrim(final String s) {
        int pos = findNonWhitespacePos(s, 0);
        return pos == -1 ? "" : s.substring(pos);
    }
}
