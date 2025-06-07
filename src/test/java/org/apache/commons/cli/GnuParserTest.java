/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * TODO Needs a rework using JUnit parameterized tests.
 */
@SuppressWarnings("deprecation") // tests some deprecated classes
public class GnuParserTest extends AbstractParserTestCase {
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        parser = new GnuParser();
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testAmbiguousLongWithoutEqualSingleDash() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testAmbiguousLongWithoutEqualSingleDash2() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testAmbiguousPartialLongOption1() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testAmbiguousPartialLongOption2() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testAmbiguousPartialLongOption3() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testAmbiguousPartialLongOption4() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testBursting() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testDoubleDash2() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testLongWithoutEqualSingleDash() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testLongWithUnexpectedArgument1() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testLongWithUnexpectedArgument2() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testMissingArgWithBursting() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser (CLI-184)")
    void testNegativeOption() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testPartialLongOptionSingleDash() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testShortWithUnexpectedArgument() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testStopBursting() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testStopBursting2() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testUnambiguousPartialLongOption1() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testUnambiguousPartialLongOption2() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testUnambiguousPartialLongOption3() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testUnambiguousPartialLongOption4() throws Exception {
    }

    @Override
    @Test
    @Disabled("not supported by the GnuParser")
    void testUnrecognizedOptionWithBursting() throws Exception {
    }
}
