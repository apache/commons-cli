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

public class ValueTest extends TestCase
{

    public static Test suite() { 
        return new TestSuite(ValueTest.class); 
        /*
        TestSuite suite = new TestSuite();

        suite.addTest( new ValueTest("testLongNoArg") );

        return suite;
        */
    }

    private CommandLine _cl = null;


    public ValueTest(String name)
    {
        super(name);
    }

    public void setUp()
    {
        Options opts = new Options();
        opts.addOption('a',
                       false,
                       "toggle -a");

        opts.addOption('b',
                       true,
                       "set -b");

        opts.addOption('c',
                       "c",
                       false,
                       "toggle -c");

        opts.addOption('d',
                       "d",
                       true,
                       "set -d");

/*            
        try
        {
            opts.addOption('a',
                           false,
                           "toggle -a");
            
            opts.addOption('b',
                           true,
                           "set -b");
            
            opts.addOption('c',
                           "c",
                           false,
                           "toggle -c");
            
            opts.addOption('d',
                           "d",
                           true,
                           "set -d");
        }
        catch (DuplicateOptionException e)
        {
            fail("Cannot setUp() Options: " + e.toString());
        }
*/

        String[] args = new String[] { "-a",
                                       "-b", "foo",
                                       "--c",
                                       "--d", "bar" };

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

    public void testShortNoArg()
    {
        assertTrue( _cl.hasOption('a') );
        assertNull( _cl.getOptionValue('a') );
    }

    public void testShortWithArg()
    {
        assertTrue( _cl.hasOption('b') );
        assertNotNull( _cl.getOptionValue('b') );
        assertEquals( _cl.getOptionValue('b'), "foo");
    }

    public void testLongNoArg()
    {
        assertTrue( _cl.hasOption('c') );
        assertNull( _cl.getOptionValue('c') );
    }

    public void testLongWithArg()
    {
        assertTrue( _cl.hasOption('d') );
        assertNotNull( _cl.getOptionValue('d') );
        assertEquals( _cl.getOptionValue('d'), "bar");
    }
}
