/**
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author John Keyes (john at integralsource.com)
 * @version $Revision$
 */
public class ParseRequiredTest extends TestCase
{

    private Options _options = null;
    private CommandLineParser parser = new PosixParser();

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
            .addOption("a",
                       "enable-a",
                       false,
                       "turn [a] on or off")
            .addOption( OptionBuilder.withLongOpt( "bfile" )
                                     .hasArg()
                                     .isRequired()
                                     .withDescription( "set the value of [b]" )
                                     .create( 'b' ) );
    }

    public void tearDown()
    {

    }

    public void testWithRequiredOption()
    {
        String[] args = new String[] {  "-b", "file" };

        try
        {
            CommandLine cl = parser.parse(_options,args);
            
            assertTrue( "Confirm -a is NOT set", !cl.hasOption("a") );
            assertTrue( "Confirm -b is set", cl.hasOption("b") );
            assertTrue( "Confirm arg of -b", cl.getOptionValue("b").equals("file") );
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
            CommandLine cl = parser.parse(_options,args);

            assertTrue( "Confirm -a is set", cl.hasOption("a") );
            assertTrue( "Confirm -b is set", cl.hasOption("b") );
            assertTrue( "Confirm arg of -b", cl.getOptionValue("b").equals("file") );
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
            CommandLine cl = parser.parse(_options,args);
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
