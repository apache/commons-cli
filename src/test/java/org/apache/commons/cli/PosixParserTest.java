/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for the PosixParser.
 */
public class PosixParserTest extends ParserTestCase
{
    @SuppressWarnings("deprecation")
    @Before
    public void setUp()
    {
        super.setUp();
        parser = new PosixParser();
    }

    @Override @Test @Ignore
    public void testDoubleDash2() throws Exception
    {
        // not supported by the PosixParser
    }
    
    @Override @Test @Ignore
    public void testLongWithoutEqualSingleDash() throws Exception
    {
        // not supported by the PosixParser
    }

    @Override @Test @Ignore
    public void testAmbiguousLongWithoutEqualSingleDash() throws Exception
    {
        // not supported by the PosixParser
    }
    
    @Override @Test @Ignore
    public void testNegativeOption() throws Exception
    {
        // not supported by the PosixParser (CLI-184)
    }

    @Override @Test @Ignore
    public void testLongWithUnexpectedArgument1() throws Exception
    {
        // not supported by the PosixParser
    }

    @Override @Test @Ignore
    public void testLongWithEqualSingleDash() throws Exception
    {
        // not supported by the PosixParser
    }

    @Override @Test @Ignore
    public void testShortWithEqual() throws Exception
    {
        // not supported by the PosixParser
    }

    @Override @Test @Ignore
    public void testUnambiguousPartialLongOption4() throws Exception
    {
        // not supported by the PosixParser
    }

    @Override @Test @Ignore
    public void testAmbiguousPartialLongOption4() throws Exception
    {
        // not supported by the PosixParser
    }
}
