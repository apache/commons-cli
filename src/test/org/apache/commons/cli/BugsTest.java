/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: BugsTest.java,v 1.3 2002/08/18 15:52:23 jkeyes Exp $
 */

package org.apache.commons.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BugsTest extends TestCase
{
    /** CommandLine instance */
    private CommandLine _cmdline = null;
    private Option _option = null;

    public static Test suite() { 
        return new TestSuite( BugsTest.class );
    }

    public BugsTest( String name )
    {
        super( name );
    }

    public void setUp()
    {
    }

    public void tearDown()
    {
    }

    public void test11458()
    {
        Options options = new Options();
        options.addOption( OptionBuilder.withValueSeparator( '=' )
                           .hasArgs()
                           .create( 'D' ) );
        options.addOption( OptionBuilder.withValueSeparator( ':' )
                           .hasArgs()
                           .create( 'p' ) );
        String[] args = new String[] { "-DJAVA_HOME=/opt/java" ,
        "-pfile1:file2:file3" };

        CommandLineParser parser = CommandLineParserFactory.newParser();

        try {
            CommandLine cmd = parser.parse( options, args );

            String[] values = cmd.getOptionValues( 'D' );

            assertEquals( values[0], "JAVA_HOME" );
            assertEquals( values[1], "/opt/java" );

            values = cmd.getOptionValues( 'p' );

            assertEquals( values[0], "file1" );
            assertEquals( values[1], "file2" );
            assertEquals( values[2], "file3" );

            java.util.Iterator iter = cmd.iterator();
            while( iter.hasNext() ) {
                Option opt = (Option)iter.next();
                switch( opt.getId() ) {
                    case 'D':
                        assertEquals( opt.getValue( 0 ), "JAVA_HOME" );
                        assertEquals( opt.getValue( 1 ), "/opt/java" );
                        break;
                    case 'p':
                        assertEquals( opt.getValue( 0 ), "file1" );
                        assertEquals( opt.getValue( 1 ), "file2" );
                        assertEquals( opt.getValue( 2 ), "file3" );
                        break;
                    default:
                        fail( "-D option not found" );
                }
            }
        }
        catch( ParseException exp ) {
            fail( "Unexpected Exception:\nMessage:" + exp.getMessage() 
                  + "Type: " + exp.getClass().getName() );
        }
    }

    public void test11680()
    {
        Options options = new Options();
        options.addOption("f", true, "foobar");
	options.addOption("m", true, "missing");
        String[] args = new String[] { "-f" , "foo" };

        CommandLineParser parser = CommandLineParserFactory.newParser();

        try {
            CommandLine cmd = parser.parse( options, args );

            try {
                cmd.getOptionValue( "f", "default f");
                cmd.getOptionValue( "m", "default m");
            }
            catch( NullPointerException exp ) {
                fail( "NullPointer caught: " + exp.getMessage() );
            }
        }
        catch( ParseException exp ) {
            fail( "Unexpected Exception: " + exp.getMessage() );
        }
    }

    public void test11456()
    {
        // Posix 
        Options options = new Options();
        options.addOption( OptionBuilder.hasOptionalArg()
                           .create( 'a' ) );
        options.addOption( OptionBuilder.hasArg()
                           .create( 'b' ) );
        String[] args = new String[] { "-a", "-bvalue" };

        CommandLineParser parser = CommandLineParserFactory.newParser();

        try {
            CommandLine cmd = parser.parse( options, args );
            assertEquals( cmd.getOptionValue( 'b' ), "value" );
        }
        catch( ParseException exp ) {
            fail( "Unexpected Exception: " + exp.getMessage() );
        }

        // GNU
        options = new Options();
        options.addOption( OptionBuilder.hasOptionalArg()
                           .create( 'a' ) );
        options.addOption( OptionBuilder.hasArg()
                           .create( 'b' ) );
        args = new String[] { "-a", "-b", "value" };

        parser = CommandLineParserFactory.newParser( "org.apache.commons.cli.GnuParser" );

        try {
            CommandLine cmd = parser.parse( options, args );
            assertEquals( cmd.getOptionValue( 'b' ), "value" );
        }
        catch( ParseException exp ) {
            fail( "Unexpected Exception: " + exp.getMessage() );
        }

    }

}
