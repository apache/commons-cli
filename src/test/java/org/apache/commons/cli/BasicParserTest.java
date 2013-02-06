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

@SuppressWarnings("deprecation") // tests some deprecated classes
public class BasicParserTest extends ParserTestCase
{
    @Before
    public void setUp()
    {
        super.setUp();
        parser = new BasicParser();
    }

    @Override
    public void testDoubleDash2() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testLongWithoutEqualSingleDash() throws Exception
    {
        // not supported by the BasicParser
    }
    
    @Override
    public void testAmbiguousLongWithoutEqualSingleDash() throws Exception
    {
        // not supported by the basicParser
    }
    
    @Override
    public void testNegativeOption() throws Exception
    {
        // not supported by the BasicParser (CLI-184)
    }

    @Override
    public void testPropertiesOption1() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testPropertiesOption2() throws Exception
    {
        // not supported by the BasicParser
    }    

    @Override
    public void testShortWithEqual() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testShortWithoutEqual() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testLongWithEqualDoubleDash() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testLongWithEqualSingleDash() throws Exception
    {
        // not supported by the BasicParser
    }
    
    @Override
    public void testUnambiguousPartialLongOption1() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testUnambiguousPartialLongOption2() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testUnambiguousPartialLongOption3() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testUnambiguousPartialLongOption4() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testAmbiguousPartialLongOption1() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testAmbiguousPartialLongOption2() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testAmbiguousPartialLongOption3() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testAmbiguousPartialLongOption4() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testPartialLongOptionSingleDash() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testBursting() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testUnrecognizedOptionWithBursting() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testMissingArgWithBursting() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testStopBursting() throws Exception
    {
        // not supported by the BasicParser
    }

    @Override
    public void testStopBursting2() throws Exception
    {
        // not supported by the BasicParser
    }
}
