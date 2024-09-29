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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TextStyleTests {

    @Test
    public void verifyDefaultStyle() {
        TextStyle underTest = TextStyle.DEFAULT;
        assertEquals(TextStyle.Alignment.LEFT, underTest.getAlignment());
        assertEquals(TextStyle.Scaling.VARIABLE, underTest.getScaling());
        assertEquals(0, underTest.getLeftPad());
        assertEquals(0, underTest.getMinWidth());
        assertEquals(TextStyle.UNSET_MAX_WIDTH, underTest.getMaxWidth());
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("padTestData")
    public void padTest(TextStyle underTest, String unindentedString, String indentedString) {
        assertEquals(unindentedString, underTest.pad(false, "Hello world"), "Unindented string test failed");
        assertEquals(indentedString, underTest.pad(true, "Hello world"), "Indented string test failed");
    }

    public static Stream<Arguments> padTestData() {
        List<Arguments> lst = new ArrayList<>();
        TextStyle.Builder builder = new TextStyle.Builder();
        builder.setIndent(5);
        builder.setLeftPad(5);
        builder.setMinWidth(4);
        builder.setScaling(TextStyle.Scaling.VARIABLE);

        // undefined creates result of original text + indent
        builder.setMaxWidth(TextStyle.UNSET_MAX_WIDTH);
        builder.setAlignment(TextStyle.Alignment.LEFT);
        lst.add(Arguments.of(builder.get(), "Hello world", "     Hello world"));

        builder.setAlignment(TextStyle.Alignment.RIGHT);
        lst.add(Arguments.of(builder.get(), "Hello world", "     Hello world"));

        builder.setAlignment(TextStyle.Alignment.CENTER);
        lst.add(Arguments.of(builder.get(), "Hello world", "  Hello world   "));


        // width less than text length creates result of original text
        builder.setMaxWidth(5);
        builder.setAlignment(TextStyle.Alignment.LEFT);
        lst.add(Arguments.of(builder.get(), "Hello world", "Hello world"));

        builder.setAlignment(TextStyle.Alignment.RIGHT);
        lst.add(Arguments.of(builder.get(), "Hello world", "Hello world"));

        builder.setAlignment(TextStyle.Alignment.CENTER);
        lst.add(Arguments.of(builder.get(), "Hello world", "Hello world"));

        // width greater than text length + indent creates result of text length with indent
        builder.setMaxWidth(20);
        builder.setAlignment(TextStyle.Alignment.LEFT);
        lst.add(Arguments.of(builder.get(), "Hello world         ", "     Hello world    "));

        builder.setAlignment(TextStyle.Alignment.RIGHT);
        lst.add(Arguments.of(builder.get(), "         Hello world", "         Hello world"));

        builder.setAlignment(TextStyle.Alignment.CENTER);
        lst.add(Arguments.of(builder.get(), "    Hello world     ", "    Hello world     "));

        // width greater than text length and less than text length + indent creates result of text length + pad
        builder.setMaxWidth(14);
        builder.setAlignment(TextStyle.Alignment.LEFT);
        lst.add(Arguments.of(builder.get(), "Hello world   ", "Hello world   "));

        builder.setAlignment(TextStyle.Alignment.RIGHT);
        lst.add(Arguments.of(builder.get(), "   Hello world", "   Hello world"));

        builder.setAlignment(TextStyle.Alignment.CENTER);
        lst.add(Arguments.of(builder.get(), " Hello world  ", " Hello world  "));

        return lst.stream();
    }
}
