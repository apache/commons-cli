/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: BugsTest.java,v 1.1 2002/08/15 20:31:23 jkeyes Exp $
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
}
