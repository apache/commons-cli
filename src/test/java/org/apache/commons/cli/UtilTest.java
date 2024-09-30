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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class UtilTest {

    @Test
    public void stripLeadingAndTrailingQuotesTest() {
        assertNull(Util.stripLeadingAndTrailingQuotes(null));
        assertEquals("", Util.stripLeadingAndTrailingQuotes(""));
        assertEquals("foo", Util.stripLeadingAndTrailingQuotes("\"foo\""));
        assertEquals("foo \"bar\"", Util.stripLeadingAndTrailingQuotes("foo \"bar\""));
        assertEquals("\"foo\" bar", Util.stripLeadingAndTrailingQuotes("\"foo\" bar"));
        assertEquals("\"foo\" and \"bar\"", Util.stripLeadingAndTrailingQuotes("\"foo\" and \"bar\""));
        assertEquals("\"", Util.stripLeadingAndTrailingQuotes("\""));
    }

    @Test
    public void stripLeadingHyphensTest() {
        assertEquals("f", Util.stripLeadingHyphens("-f"));
        assertEquals("foo", Util.stripLeadingHyphens("--foo"));
        assertEquals("-foo", Util.stripLeadingHyphens("---foo"));
        assertNull(Util.stripLeadingHyphens(null));
    }

    @Test
    public void findWrapPosTest() {
        String testString = "The quick brown fox jumps over\tthe lazy dog";

        assertEquals(9, Util.findWrapPos(testString, 10, 0), "did not find end of word");
        assertEquals(9, Util.findWrapPos(testString, 14, 0), "did not backup to end of word");
        assertEquals(15, Util.findWrapPos(testString, 15, 0), "did not find word at 15");
        assertEquals(15, Util.findWrapPos(testString, 16, 0));
        assertEquals(30, Util.findWrapPos(testString, 15, 20), "did not find break character");
        assertEquals(30, Util.findWrapPos(testString, 150, 0), "did not handle text shorter than width");

        assertThrows(IllegalArgumentException.class, () -> Util.findWrapPos("", 0, 0));
        assertEquals(3, Util.findWrapPos("Hello", 4, 0));
    }

    @ParameterizedTest
    @MethodSource("charArgs")
    public void ltrimTest(final Character c, final boolean isWhitespace) {
        if (isWhitespace) {
            assertEquals("worx", Util.ltrim(format("%sworx", c)), () -> format("Did not process character 0x%x", (int) c));
        } else {
            assertNotEquals("worx", Util.ltrim(format("%sworx", c)), () -> format("Did not process character 0x%x", (int) c));
        }
        String text = format("%c%c%c", c, c, c);
        assertEquals(isWhitespace ? "" : text, Util.ltrim(text));
    }

    @ParameterizedTest
    @MethodSource("charArgs")
    public void rtrimTest(final Character c, final boolean isWhitespace) {
        if (isWhitespace) {
            assertEquals("worx", Util.rtrim(format("worx%s", c)), () -> format("Did not process character 0x%x", (int) c));
        } else {
            assertNotEquals("worx", Util.rtrim(format("worx%s", c)), () -> format("Did not process character 0x%x", (int) c));
        }
        String text = format("%c%c%c", c, c, c);
        assertEquals(isWhitespace ? "" : text, Util.ltrim(text));
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

    @Test
    public void isEmptyTest() {
        Object[] objAry = null;
        assertTrue(Util.isEmpty(objAry), "null array should be empty");
        objAry = new Object[]{};
        assertTrue(Util.isEmpty(objAry), "empty array should be empty");
        objAry = new Object[1];
        assertFalse(Util.isEmpty(objAry), "array with only null element should not be empty");
        objAry = new Object[] { "hello" };
        assertFalse(Util.isEmpty(objAry), "array with element should not be empty");

        String str = null;
        assertTrue(Util.isEmpty(str), "null string should be empty");
        str = "";
        assertTrue(Util.isEmpty(str), "empty string should be empty");
        str = " ";
        assertFalse(Util.isEmpty(str), "string with whitespace should not be empty");
    }

    @ParameterizedTest
    @MethodSource("charArgs")
    public void findWrapPosWithWhitespace(final Character c, final boolean isWhitespace) {
        String text = format("Hello%cWorld", c);
        assertEquals(isWhitespace ? 5 : 6, Util.findWrapPos(text, 7, 0));
    }

    @ParameterizedTest
    @MethodSource("charArgs")
    public void findNonWhitespacePosTest(final Character c, final boolean isWhitespace) {
        String text = format("%cWorld", c);
        assertEquals(isWhitespace ? 1 : 0, Util.findNonWhitespacePos(text, 0));
        text = format("%c%c%c", c, c, c);
        assertEquals(isWhitespace ? -1 : 0, Util.findNonWhitespacePos(text, 0));
    }

    @Test
    public void findNonWhitespacePosTest() {
        assertEquals(-1, Util.findNonWhitespacePos(null, 0));
        assertEquals(-1, Util.findNonWhitespacePos("", 0));
    }
}
