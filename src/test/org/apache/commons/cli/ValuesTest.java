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

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ValuesTest extends TestCase
{
    /** CommandLine instance */
    private CommandLine _cmdline = null;

    public static Test suite() { 
        return new TestSuite( ValuesTest.class );
    }

    public ValuesTest( String name )
    {
        super( name );
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
        
        opts.addOption( OptionBuilder.withLongOpt( "e" )
                                     .hasArgs()
                                     .withDescription( "set -e ")
                                     .create( 'e' ) );

        opts.addOption("f",
                       "f",
                       false,
                       "jk");
        
        opts.addOption( OptionBuilder.withLongOpt( "g" )
                        .hasArgs( 2 )
                        .withDescription( "set -g")
                        .create( 'g' ) );

        opts.addOption( OptionBuilder.withLongOpt( "h" )
                        .hasArgs( 2 )
                        .withDescription( "set -h")
                        .create( 'h' ) );

        opts.addOption( OptionBuilder.withLongOpt( "i" )
                        .withDescription( "set -i")
                        .create( 'i' ) );
        
        String[] args = new String[] { "-a",
                                       "-b", "foo",
                                       "--c",
                                       "--d", "bar",
                                       "-e", "one", "two",
                                       "-f",
                                       "arg1", "arg2",
                                       "-g", "val1", "val2" , "arg3",
                                       "-h", "val1", "-i",
                                       "-h", "val2" };

        CommandLineParser parser = CommandLineParserFactory.newParser();

        try
        {
            _cmdline = parser.parse(opts,args);
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
        assertTrue( _cmdline.hasOption("a") );
        assertTrue( _cmdline.hasOption("c") );

        assertNull( _cmdline.getOptionValues("a") );
        assertNull( _cmdline.getOptionValues("c") );
    }

    public void testShortArgsWithValue()
    {
        assertTrue( _cmdline.hasOption("b") );
        assertTrue( _cmdline.getOptionValue("b").equals("foo"));
        assertTrue( _cmdline.getOptionValues("b").length == 1);

        assertTrue( _cmdline.hasOption("d") );
        assertTrue( _cmdline.getOptionValue("d").equals("bar"));
        assertTrue( _cmdline.getOptionValues("d").length == 1);
    }

    public void testMultipleArgValues()
    {
        String[] result = _cmdline.getOptionValues("e");
        String[] values = new String[] { "one", "two" };
        assertTrue( _cmdline.hasOption("e") );
        assertTrue( _cmdline.getOptionValues("e").length == 2);
        assertTrue( Arrays.equals( values, _cmdline.getOptionValues("e") ) );
    }

    public void testTwoArgValues()
    {
        String[] result = _cmdline.getOptionValues("g");
        String[] values = new String[] { "val1", "val2" };
        assertTrue( _cmdline.hasOption("g") );
        assertTrue( _cmdline.getOptionValues("g").length == 2);
        assertTrue( Arrays.equals( values, _cmdline.getOptionValues("g") ) );
    }

    public void testComplexValues()
    {
        String[] result = _cmdline.getOptionValues("h");
        String[] values = new String[] { "val1", "val2" };
        assertTrue( _cmdline.hasOption("i") );
        assertTrue( _cmdline.hasOption("h") );
        assertTrue( _cmdline.getOptionValues("h").length == 2);
        assertTrue( Arrays.equals( values, _cmdline.getOptionValues("h") ) );
    }

    public void testExtraArgs()
    {
        String[] args = new String[] { "arg1", "arg2", "arg3" };
        assertTrue( _cmdline.getArgs().length == 3 );
        assertTrue( Arrays.equals( args, _cmdline.getArgs() ) );
    }
}
