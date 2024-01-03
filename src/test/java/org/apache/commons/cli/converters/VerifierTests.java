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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
}
