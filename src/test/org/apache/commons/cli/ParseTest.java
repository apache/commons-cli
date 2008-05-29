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

import junit.framework.TestCase;

public class ParseTest extends TestCase
{
    private Options _options = null;
    private Parser _parser = null;

    public void setUp()
    {
        _options = new Options()
            .addOption("a",
                       "enable-a",
                       false,
                       "turn [a] on or off")
            .addOption("b",
                       "bfile",
                       true,
                       "set the value of [b]")
            .addOption("c",
                       "copt",
                       false,
                       "turn [c] on or off");

        _parser = new PosixParser();
    }

    public void testSimpleShort() throws Exception
    {
        String[] args = new String[] { "-a",
                                       "-b", "toast",
                                       "foo", "bar" };

        CommandLine cl = _parser.parse(_options, args);

        assertTrue( "Confirm -a is set", cl.hasOption("a") );
        assertTrue( "Confirm -b is set", cl.hasOption("b") );
        assertTrue( "Confirm arg of -b", cl.getOptionValue("b").equals("toast") );
        assertTrue( "Confirm size of extra args", cl.getArgList().size() == 2);
    }

    public void testSimpleLong() throws Exception
    {
        String[] args = new String[] { "--enable-a",
                                       "--bfile", "toast",
                                       "foo", "bar" };

        CommandLine cl = _parser.parse(_options, args);

        assertTrue( "Confirm -a is set", cl.hasOption("a") );
        assertTrue( "Confirm -b is set", cl.hasOption("b") );
        assertTrue( "Confirm arg of -b", cl.getOptionValue("b").equals("toast") );
        assertTrue( "Confirm arg of --bfile", cl.getOptionValue( "bfile" ).equals( "toast" ) );
        assertTrue( "Confirm size of extra args", cl.getArgList().size() == 2);
    }

    public void testComplexShort() throws Exception
    {
        String[] args = new String[] { "-acbtoast",
                                       "foo", "bar" };

        CommandLine cl = _parser.parse(_options, args);

        assertTrue( "Confirm -a is set", cl.hasOption("a") );
        assertTrue( "Confirm -b is set", cl.hasOption("b") );
        assertTrue( "Confirm -c is set", cl.hasOption("c") );
        assertTrue( "Confirm arg of -b", cl.getOptionValue("b").equals("toast") );
        assertTrue( "Confirm size of extra args", cl.getArgList().size() == 2);
    }

    public void testExtraOption() throws Exception
    {
        String[] args = new String[] { "-adbtoast",
                                       "foo", "bar" };

        boolean caught = false;

        try
        {
            CommandLine cl = _parser.parse(_options, args);
            
            assertTrue( "Confirm -a is set", cl.hasOption("a") );
            assertTrue( "Confirm -b is set", cl.hasOption("b") );
            assertTrue( "confirm arg of -b", cl.getOptionValue("b").equals("toast") );
            assertTrue( "Confirm size of extra args", cl.getArgList().size() == 3);
        }
        catch (UnrecognizedOptionException e)
        {
            caught = true;
        }

        assertTrue( "Confirm UnrecognizedOptionException caught", caught );
    }

    public void testMissingArg() throws Exception
    {

        String[] args = new String[] { "-acb" };

        boolean caught = false;

        try
        {
            _parser.parse(_options, args);
        }
        catch (MissingArgumentException e)
        {
            caught = true;
        }

        assertTrue( "Confirm MissingArgumentException caught", caught );
    }

    public void testStop() throws Exception
    {
        String[] args = new String[] { "-c",
                                       "foober",
                                       "-btoast" };

        CommandLine cl = _parser.parse(_options, args, true);
        assertTrue( "Confirm -c is set", cl.hasOption("c") );
        assertTrue( "Confirm  2 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 2);
    }

    public void testMultiple() throws Exception
    {
        String[] args = new String[] { "-c",
                                       "foobar",
                                       "-btoast" };

        CommandLine cl = _parser.parse(_options, args, true);
        assertTrue( "Confirm -c is set", cl.hasOption("c") );
        assertTrue( "Confirm  2 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 2);

        cl = _parser.parse(_options, cl.getArgs() );

        assertTrue( "Confirm -c is not set", ! cl.hasOption("c") );
        assertTrue( "Confirm -b is set", cl.hasOption("b") );
        assertTrue( "Confirm arg of -b", cl.getOptionValue("b").equals("toast") );
        assertTrue( "Confirm  1 extra arg: " + cl.getArgList().size(), cl.getArgList().size() == 1);
        assertTrue( "Confirm  value of extra arg: " + cl.getArgList().get(0), cl.getArgList().get(0).equals("foobar") );
    }

    public void testMultipleWithLong() throws Exception
    {
        String[] args = new String[] { "--copt",
                                       "foobar",
                                       "--bfile", "toast" };

        CommandLine cl = _parser.parse(_options,args,
                                        true);
        assertTrue( "Confirm -c is set", cl.hasOption("c") );
        assertTrue( "Confirm  3 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 3);

        cl = _parser.parse(_options, cl.getArgs() );

        assertTrue( "Confirm -c is not set", ! cl.hasOption("c") );
        assertTrue( "Confirm -b is set", cl.hasOption("b") );
        assertTrue( "Confirm arg of -b", cl.getOptionValue("b").equals("toast") );
        assertTrue( "Confirm  1 extra arg: " + cl.getArgList().size(), cl.getArgList().size() == 1);
        assertTrue( "Confirm  value of extra arg: " + cl.getArgList().get(0), cl.getArgList().get(0).equals("foobar") );
    }

    public void testDoubleDash() throws Exception
    {
        String[] args = new String[] { "--copt",
                                       "--",
                                       "-b", "toast" };

        CommandLine cl = _parser.parse(_options, args);

        assertTrue( "Confirm -c is set", cl.hasOption("c") );
        assertTrue( "Confirm -b is not set", ! cl.hasOption("b") );
        assertTrue( "Confirm 2 extra args: " + cl.getArgList().size(), cl.getArgList().size() == 2);
    }

    public void testSingleDash() throws Exception
    {
        String[] args = new String[] { "--copt",
                                       "-b", "-",
                                       "-a",
                                       "-" };

        CommandLine cl = _parser.parse(_options, args);

        assertTrue( "Confirm -a is set", cl.hasOption("a") );
        assertTrue( "Confirm -b is set", cl.hasOption("b") );
        assertTrue( "Confirm arg of -b", cl.getOptionValue("b").equals("-") );
        assertTrue( "Confirm 1 extra arg: " + cl.getArgList().size(), cl.getArgList().size() == 1);
        assertTrue( "Confirm value of extra arg: " + cl.getArgList().get(0), cl.getArgList().get(0).equals("-") );
    }
}
