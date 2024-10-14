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

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class UtilTest {

    public static Stream<Arguments> charArgs() {
        final List<Arguments> lst = new ArrayList<>();
        // @formatter:off
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
        // @formatter:on
        final char[] nonBreakingSpace = { '\u00A0', '\u2007', '\u202F' };
        for (final char c : whitespace) {
            lst.add(Arguments.of(Character.valueOf(c), Boolean.TRUE));
        }
        for (final char c : nonBreakingSpace) {
            lst.add(Arguments.of(Character.valueOf(c), Boolean.FALSE));
        }
        return lst.stream();
    }

    @Test
    public void testFindNonWhitespacePos() {
        assertEquals(-1, Util.findNonWhitespacePos(null, 0));
        assertEquals(-1, Util.findNonWhitespacePos("", 0));
    }

    @ParameterizedTest
    @MethodSource("charArgs")
    public void testFindNonWhitespacePos(final Character c, final boolean isWhitespace) {
        String text = format("%cWorld", c);
        assertEquals(isWhitespace ? 1 : 0, Util.findNonWhitespacePos(text, 0));
        text = format("%c%c%c", c, c, c);
        assertEquals(isWhitespace ? -1 : 0, Util.findNonWhitespacePos(text, 0));
    }

    @Test
    public void testIsEmpty() {
        String str = null;
        assertTrue(Util.isEmpty(str), "null string should be empty");
        str = "";
        assertTrue(Util.isEmpty(str), "empty string should be empty");
        str = " ";
        assertFalse(Util.isEmpty(str), "string with whitespace should not be empty");
    }

    @ParameterizedTest
    @MethodSource("charArgs")
    public void testRtrim(final Character c, final boolean isWhitespace) {
        if (isWhitespace) {
            assertEquals("worx", Util.rtrim(format("worx%s", c)), () -> format("Did not process character 0x%x", (int) c));
        } else {
            assertNotEquals("worx", Util.rtrim(format("worx%s", c)), () -> format("Did not process character 0x%x", (int) c));
        }
        final String text = format("%c%c%c", c, c, c);
        assertEquals(isWhitespace ? "" : text, Util.ltrim(text));
    }
}
