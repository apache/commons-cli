/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: ParseRequiredTest.java,v 1.1 2002/04/23 16:08:02 jstrachan Exp $
 */

package org.apache.commons.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author John Keyes (john at integralsource.com)
 * @version $Revision: 1.1 $
 */
public class ParseRequiredTest extends TestCase
{

    private Options _options = null;

    public static Test suite() { 
        return new TestSuite(ParseRequiredTest.class); 
    }

    public ParseRequiredTest(String name)
    {
        super(name);
    }

    public void setUp()
    {
        _options = new Options()
            .addOption('a',
                       "enable-a",
                       false,
                       "turn [a] on or off")
            .addOption('b',
                       "bfile",
                       true,
                       "set the value of [b]",
                       true);
    }

    public void tearDown()
    {

    }

    public void testWithRequiredOption()
    {
        String[] args = new String[] {  "-b", "file" };

        try
        {
            CommandLine cl = _options.parse(args);
            
            assertTrue( "Confirm -a is NOT set", !cl.hasOption('a') );
            assertTrue( "Confirm -b is set", cl.hasOption('b') );
            assertTrue( "Confirm arg of -b", cl.getOptionValue('b').equals("file") );
            assertTrue( "Confirm NO of extra args", cl.getArgList().size() == 0);
        }
        catch (ParseException e)
        {
            fail( e.toString() );
        }
    }

    public void testOptionAndRequiredOption()
    {
        String[] args = new String[] {  "-a", "-b", "file" };

        try
        {
            CommandLine cl = _options.parse(args);

            assertTrue( "Confirm -a is set", cl.hasOption('a') );
            assertTrue( "Confirm -b is set", cl.hasOption('b') );
            assertTrue( "Confirm arg of -b", cl.getOptionValue('b').equals("file") );
            assertTrue( "Confirm NO of extra args", cl.getArgList().size() == 0);
        }
        catch (ParseException e)
        {
            fail( e.toString() );
        }
    }

    public void testMissingRequiredOption()
    {
        String[] args = new String[] { "-a" };

        try
        {
            CommandLine cl = _options.parse(args);
            fail( "exception should have been thrown" );
        }
        catch (ParseException e)
        {
            if( !( e instanceof MissingOptionException ) )
            {
                fail( "expected to catch MissingOptionException" );
            }
        }
    }

}
