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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for standard Verifiers.
 */
public class VerifierTests {

    /**
     * Verifies number formats
     */
    @Test
    public void numberTests() {
        // test good numbers
        for (String s : new String[] {"123", "12.3", "-123", "-12.3", ".3", "-.3"}) {
            assertTrue(Verifier.NUMBER.test(s), s);
        }

        // test bad numbers
        for (String s : new String[] {"0x5F", "2,3", "1.2.3"}) {
            assertFalse(Verifier.NUMBER.test(s), s);
        }
    }

    /**
     * Verifies integer formats
     */
    @Test
    public void integerTests() {
        // test good numbers
        for (String s : new String[] {"123", "-123"}) {
            assertTrue(Verifier.INTEGER.test(s), s);
        }

        // test bad numbers
        for (String s : new String[] {"12.3", "-12.3", ".3", "-.3", "0x5F", "2,3", "1.2.3"}) {
            assertFalse(Verifier.INTEGER.test(s), s);
        }
    }

    /**
     * Verifies class names
     */
    @Test
    public void classTests() {
        String testName = this.getClass().getName();
        assertTrue(Verifier.CLASS.test(testName), testName);
        assertTrue(Verifier.CLASS.test(this.getClass().getCanonicalName()), this.getClass().getCanonicalName());
        assertTrue(Verifier.CLASS.test(this.getClass().getSimpleName()), this.getClass().getSimpleName());
        assertTrue(Verifier.CLASS.test(this.getClass().getTypeName()), this.getClass().getTypeName());

        // test bad prefixes
        for (String s : new String[] {"1", "-", "."}) {
            assertFalse(Verifier.CLASS.test(s + testName), s + testName);
        }

        // test good prefixes
        for (String s : new String[] {"$"}) {
            assertTrue(Verifier.CLASS.test(s + testName), s + testName);
        }

        // test bad suffixes
        for (String s : new String[] {"-", "."}) {
            assertFalse(Verifier.CLASS.test(testName + s), testName + s);
        }

        // test good suffixes
        for (String s : new String[] {"1", "$"}) {
            assertTrue(Verifier.CLASS.test(testName + s), testName + s);
        }

        // test bad infixes
        for (String s : new String[] {"..", "-"}) {
            assertFalse(Verifier.CLASS.test("foo" + s + "bar"), "foo" + s + "bar");
        }

        // test good infixes
        for (String s : new String[] {"$", "_"}) {
            assertTrue(Verifier.CLASS.test("foo" + s + "bar"), "foo" + s + "bar");
        }
    }
    
    enum MyEnum {
        ONE, Two, three, someothernumber
    }

    private Predicate<String> underTest = Verifier.enumVerifier(MyEnum.Two);

    @ParameterizedTest(name = "{0}")
    @MethodSource("testData")
    public void test(final String str, final boolean expected) {
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
