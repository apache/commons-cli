/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: BuildTest.java,v 1.1 2001/12/19 18:16:25 jstrachan Exp $
 */

package org.apache.commons.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BuildTest extends TestCase
{

    public static Test suite() { 
        return new TestSuite(BuildTest.class); 
    }

    public BuildTest(String name)
    {
        super(name);
    }

    public void setUp()
    {

    }

    public void tearDown()
    {

    }

    public void testSimple()
    {
        Options opts = new Options();
        
        opts.addOption("a",
                       false,
                       "toggle -a");

        opts.addOption("b",
                       true,
                       "toggle -b");
    }

    public void testDuplicateSimple()
    {
        Options opts = new Options();
        opts.addOption("a",
                       false,
                       "toggle -a");

        opts.addOption("a",
                       true,
                       "toggle -a*");
        
        assertEquals( "last one in wins", "toggle -a*", opts.getOption("a").getDescription() );
    }

    public void testLong()
    {
        Options opts = new Options();
        
        opts.addOption("a",
                       "--a",
                       false,
                       "toggle -a");

        opts.addOption("b",
                       "--b",
                       true,
                       "set -b");

    }

    public void testDuplicateLong()
    {
        Options opts = new Options();
        opts.addOption("a",
                       "--a",
                       false,
                       "toggle -a");

        opts.addOption("a",
                       "--a",
                       false,
                       "toggle -a*");
        assertEquals( "last one in wins", "toggle -a*", opts.getOption("a").getDescription() );
    }
}
