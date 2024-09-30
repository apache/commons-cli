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

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UtilTest {

    @Test
    public void testStripLeadingAndTrailingQuotes() {
        assertNull(Util.stripLeadingAndTrailingQuotes(null));
        assertEquals("", Util.stripLeadingAndTrailingQuotes(""));
        assertEquals("foo", Util.stripLeadingAndTrailingQuotes("\"foo\""));
        assertEquals("foo \"bar\"", Util.stripLeadingAndTrailingQuotes("foo \"bar\""));
        assertEquals("\"foo\" bar", Util.stripLeadingAndTrailingQuotes("\"foo\" bar"));
        assertEquals("\"foo\" and \"bar\"", Util.stripLeadingAndTrailingQuotes("\"foo\" and \"bar\""));
        assertEquals("\"", Util.stripLeadingAndTrailingQuotes("\""));
    }

    @Test
    public void testStripLeadingHyphens() {
        assertEquals("f", Util.stripLeadingHyphens("-f"));
        assertEquals("foo", Util.stripLeadingHyphens("--foo"));
        assertEquals("-foo", Util.stripLeadingHyphens("---foo"));
        assertNull(Util.stripLeadingHyphens(null));
    }

    @Test
    public void testFindWrapPos() {
        String testString = "The quick brown fox jumps over\tthe lazy dog";

        assertEquals(9, Util.findWrapPos(testString, 10, 0), "did not find end of word");
        assertEquals(9, Util.findWrapPos(testString, 14, 0), "did not backup to end of word");
        assertEquals(15, Util.findWrapPos(testString, 15, 0), "did not find word at 15");
        assertEquals(15, Util.findWrapPos(testString, 16, 0));
        assertEquals(30, Util.findWrapPos(testString, 15, 20),  "did not find break character");
        assertEquals(30, Util.findWrapPos(testString, 150, 0), "did not handle text shorter than width");
    }



    @ParameterizedTest
    @MethodSource("charArgs")
    public void ltrimTest(final Character c, final boolean isWhitespace) {
        if (isWhitespace) {
            assertEquals("worx", Util.ltrim(format("%sworx", c)), () -> format("Did not process character 0x%x", (int) c));
        } else {
            assertNotEquals("worx", Util.ltrim(format("%sworx", c)), () -> format("Did not process character 0x%x", (int) c));
        }

    }

    @ParameterizedTest
    @MethodSource("charArgs")
    public void rtrimTest(final Character c, final boolean isWhitespace) {
        if (isWhitespace) {
            assertEquals("worx", Util.rtrim(format("worx%s", c)), () -> format("Did not process character 0x%x", (int) c));
        } else {
            assertNotEquals("worx", Util.rtrim(format("worx%s", c)), () -> format("Did not process character 0x%x", (int) c));
        }
    }

    public static Stream<Arguments> charArgs() {
        List<Arguments> lst = new ArrayList<>();
        final char[] whitespace = {' ', '\t', '\n', '\f', '\r',
                Character.SPACE_SEPARATOR,
                Character.LINE_SEPARATOR,
                Character.PARAGRAPH_SEPARATOR,
                '\u000B', // VERTICAL TABULATION.
                '\u001C', // FILE SEPARATOR.
                '\u001D', // GROUP SEPARATOR.
                '\u001E', // RECORD SEPARATOR.
                '\u001F', // UNIT SEPARATOR.
        };

        final char[] nonBreakingSpace = {  '\u00A0', '\u2007', '\u202F' };

        for (char c : whitespace) {
            lst.add(Arguments.of(Character.valueOf(c), Boolean.TRUE));
        }
        for (char c : nonBreakingSpace) {
            lst.add(Arguments.of(Character.valueOf(c), Boolean.FALSE));
        }
        return lst.stream();
    }
}
