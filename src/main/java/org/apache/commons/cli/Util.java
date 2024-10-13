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
 * Contains useful helper methods for classes within this package.
 */
final class Util {

    /**
     * An empty immutable {@code String} array.
     */
    static final String[] EMPTY_STRING_ARRAY = {};

    /**
     * Tests whether the given array is null or empty.
     *
     * @param array the array to test.
     * @return the given array is null or empty.
     */
    static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Tests whether the given string is null or empty.
     *
     * @param str The string to test.
     * @return Whether the given string is null or empty.
     */
    static boolean isEmpty(final String str) {
        return str == null || str.isEmpty();
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

    private Util() {
        // no instances
    }
}
