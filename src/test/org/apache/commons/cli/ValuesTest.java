/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: ValueTest.java,v 1.1 2001/12/19 18:16:25 jstrachan Exp $
 */

package org.apache.commons.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ValuesTest extends TestCase
{

    public static Test suite() { 
        return new TestSuite(ValuesTest.class); 
        /*
        TestSuite suite = new TestSuite();

        suite.addTest( new ValueTest("testLongNoArg") );

        return suite;
        */
    }

    private CommandLine _cl = null;


    public ValuesTest(String name)
    {
        super(name);
    }

    public void setUp()
    {
        Options opts = new Options();
        opts.addOption("a",
                       false,
                       "toggle -a");

        opts.addOption("b",
                       true,
                       "set -b");

        opts.addOption("c",
                       "c",
                       false,
                       "toggle -c");

        opts.addOption("d",
                       "d",
                       true,
                       "set -d");
        
        opts.addOption("e",
                       "e",
                       true,
                       "set -e",
                       false,
                       true);

        opts.addOption("f",
                       "f",
                       false,
                       "jk");

        String[] args = new String[] { "-a",
                                       "-b", "foo",
                                       "--c",
                                       "--d", "bar",
                                       "-e", "one", "two",
                                       "-f",
                                       "arg1", "arg2" };

        try
        {
            _cl = opts.parse(args);
        }
        catch (ParseException e)
        {
            fail("Cannot setUp() CommandLine: " + e.toString());
        }
    }

    public void tearDown()
    {

    }

    public void testShortArgs()
    {
        assertTrue( _cl.hasOption("a") );
        assertTrue( _cl.hasOption("c") );

        assertNull( _cl.getOptionValues("a") );
        assertNull( _cl.getOptionValues("c") );
    }

    public void testShortArgsWithValue()
    {
        assertTrue( _cl.hasOption("b") );
        assertTrue( _cl.getOptionValue("b").equals("foo"));
        assertTrue( _cl.getOptionValues("b").length == 1);

        assertTrue( _cl.hasOption("d") );
        assertTrue( _cl.getOptionValue("d").equals("bar"));
        assertTrue( _cl.getOptionValues("d").length == 1);
    }

    public void testMultipleArgValues()
    {
        String[] result = _cl.getOptionValues("e");
        String[] values = new String[] { "one", "two" };
        assertTrue( _cl.hasOption("e") );
        assertTrue( _cl.getOptionValues("e").length == 2);
        assertTrue( java.util.Arrays.equals( values, _cl.getOptionValues("e") ) );
    }

    public void testExtraArgs()
    {
        String[] args = new String[] { "arg1", "arg2" };
        assertTrue( _cl.getArgs().length == 2);
        assertTrue( java.util.Arrays.equals( args, _cl.getArgs() ) );
    }
}
