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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("deprecation") // tests some deprecated classes
public class GnuParserTest extends ParserTestCase {
    @Override
    @Before
    public void setUp() {
        super.setUp();
        parser = new GnuParser();
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testAmbiguousLongWithoutEqualSingleDash() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testAmbiguousPartialLongOption1() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testAmbiguousPartialLongOption2() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testAmbiguousPartialLongOption3() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testAmbiguousPartialLongOption4() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testBursting() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testDoubleDash2() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testLongWithoutEqualSingleDash() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testLongWithUnexpectedArgument1() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testLongWithUnexpectedArgument2() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testMissingArgWithBursting() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser (CLI-184)")
    public void testNegativeOption() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testPartialLongOptionSingleDash() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testShortWithUnexpectedArgument() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testStopBursting() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testStopBursting2() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testUnambiguousPartialLongOption1() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testUnambiguousPartialLongOption2() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testUnambiguousPartialLongOption3() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testUnambiguousPartialLongOption4() throws Exception {
    }

    @Override
    @Test
    @Ignore("not supported by the GnuParser")
    public void testUnrecognizedOptionWithBursting() throws Exception {
    }
}
