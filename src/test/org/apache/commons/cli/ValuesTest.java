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

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ValuesTest extends TestCase
{
    /** CommandLine instance */
    private CommandLine _cmdline = null;
    private Option _option = null;

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

        opts.addOption( OptionBuilder.withLongOpt( "j" )
                        .hasArgs( )
                        .withDescription( "set -j")
                        .withValueSeparator( '=' )
                        .create( 'j' ) );

        opts.addOption( OptionBuilder.withLongOpt( "k" )
                        .hasArgs( )
                        .withDescription( "set -k")
                        .withValueSeparator( '=' )
                        .create( 'k' ) );

        _option = OptionBuilder.withLongOpt( "m" )
                        .hasArgs( )
                        .withDescription( "set -m")
                        .withValueSeparator( )
                        .create( 'm' );

        opts.addOption( _option );
        
        String[] args = new String[] { "-a",
                                       "-b", "foo",
                                       "--c",
                                       "--d", "bar",
                                       "-e", "one", "two",
                                       "-f",
                                       "arg1", "arg2",
                                       "-g", "val1", "val2" , "arg3",
                                       "-h", "val1", "-i",
                                       "-h", "val2",
                                       "-jkey=value",
                                       "-j", "key=value",
                                       "-kkey1=value1", 
                                       "-kkey2=value2",
                                       "-mkey=value"};

        CommandLineParser parser = new PosixParser();

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

    public void testCharSeparator()
    {
        // tests the char methods of CommandLine that delegate to
        // the String methods
        String[] values = new String[] { "key", "value", "key", "value" };
        assertTrue( _cmdline.hasOption( "j" ) );
        assertTrue( _cmdline.hasOption( 'j' ) );
        assertEquals( 4, _cmdline.getOptionValues( "j" ).length );
        assertEquals( 4, _cmdline.getOptionValues( 'j' ).length );
        assertTrue( Arrays.equals( values, _cmdline.getOptionValues( "j" ) ) );
        assertTrue( Arrays.equals( values, _cmdline.getOptionValues( 'j' ) ) );

        values = new String[] { "key1", "value1", "key2", "value2" };
        assertTrue( _cmdline.hasOption( "k" ) );
        assertTrue( _cmdline.hasOption( 'k' ) );
        assertTrue( _cmdline.getOptionValues( "k" ).length == 4 );
        assertTrue( _cmdline.getOptionValues( 'k' ).length == 4 );
        assertTrue( Arrays.equals( values, _cmdline.getOptionValues( "k" ) ) );
        assertTrue( Arrays.equals( values, _cmdline.getOptionValues( 'k' ) ) );

        values = new String[] { "key", "value" };
        assertTrue( _cmdline.hasOption( "m" ) );
        assertTrue( _cmdline.hasOption( 'm' ) );
        assertTrue( _cmdline.getOptionValues( "m" ).length == 2);
        assertTrue( _cmdline.getOptionValues( 'm' ).length == 2);
        assertTrue( Arrays.equals( values, _cmdline.getOptionValues( "m" ) ) );
        assertTrue( Arrays.equals( values, _cmdline.getOptionValues( 'm' ) ) );
    }

    /**
     * jkeyes - commented out this test as the new architecture
     * breaks this type of functionality.  I have left the test 
     * here in case I get a brainwave on how to resolve this.
     */
    /*
    public void testGetValue()
    {
        // the 'm' option
        assertTrue( _option.getValues().length == 2 );
        assertEquals( _option.getValue(), "key" );
        assertEquals( _option.getValue( 0 ), "key" );
        assertEquals( _option.getValue( 1 ), "value" );

        try {
            assertEquals( _option.getValue( 2 ), "key" );
            fail( "IndexOutOfBounds not caught" );
        }
        catch( IndexOutOfBoundsException exp ) {
            
        }

        try {
            assertEquals( _option.getValue( -1 ), "key" );
            fail( "IndexOutOfBounds not caught" );
        }
        catch( IndexOutOfBoundsException exp ) {

        }
    }
    */
}
