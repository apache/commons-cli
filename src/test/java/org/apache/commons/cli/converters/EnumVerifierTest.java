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
package org.apache.commons.cli.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for the EnumVerifier class
 *
 */
public class EnumVerifierTest {

    enum MyEnum {
        ONE, Two, three, someothernumber
    }

    private EnumVerifier underTest = new EnumVerifier(MyEnum.Two);

    @ParameterizedTest(name = "{0}")
    @MethodSource("testData")
    public void test(String str, boolean expected) {
        assertEquals(expected, underTest.test(str));
    }

    private static Stream<Arguments> testData() {
        List<Arguments> lst = new ArrayList<>();

        lst.add(Arguments.of("ONE", true));
        lst.add(Arguments.of("one", false));
        lst.add(Arguments.of("Two", true));
        lst.add(Arguments.of("three", true));
        lst.add(Arguments.of("someothernumber", true));
        lst.add(Arguments.of("NonValue", false));

        return lst.stream();
    }
}
