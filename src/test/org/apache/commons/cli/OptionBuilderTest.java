package org.apache.commons.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

public class OptionBuilderTest extends TestCase {

    public OptionBuilderTest( String name ) {
        super( name );
    }

    public static Test suite() { 
        return new TestSuite( OptionBuilderTest.class ); 
    }

    public static void main( String args[] ) { 
        TestRunner.run( suite() );
    }

    public void testCompleteOption( ) {
        Option simple = OptionBuilder.withLongOpt( "simple option")
                                     .hasArg( )
                                     .isRequired( )
                                     .hasArgs( )
                                     .withType( new Float( 10 ) )
                                     .withDescription( "this is a simple option" )
                                     .create( 's' );

        assertEquals( "s", simple.getOpt() );
        assertEquals( "simple option", simple.getLongOpt() );
        assertEquals( "this is a simple option", simple.getDescription() );
        assertEquals( simple.getType().getClass(), Float.class );
        assertTrue( simple.hasArg() );
        assertTrue( simple.isRequired() );
        assertTrue( simple.hasArgs() );
    }

    public void testTwoCompleteOptions( ) {
        Option simple = OptionBuilder.withLongOpt( "simple option")
                                     .hasArg( )
                                     .isRequired( )
                                     .hasArgs( )
                                     .withType( new Float( 10 ) )
                                     .withDescription( "this is a simple option" )
                                     .create( 's' );

        assertEquals( "s", simple.getOpt() );
        assertEquals( "simple option", simple.getLongOpt() );
        assertEquals( "this is a simple option", simple.getDescription() );
        assertEquals( simple.getType().getClass(), Float.class );
        assertTrue( simple.hasArg() );
        assertTrue( simple.isRequired() );
        assertTrue( simple.hasArgs() );

        simple = OptionBuilder.withLongOpt( "dimple option")
                              .hasArg( )
                              .withDescription( "this is a dimple option" )
                              .create( 'd' );

        assertEquals( "d", simple.getOpt() );
        assertEquals( "dimple option", simple.getLongOpt() );
        assertEquals( "this is a dimple option", simple.getDescription() );
        assertNull( simple.getType() );
        assertTrue( simple.hasArg() );
        assertTrue( !simple.isRequired() );
        assertTrue( !simple.hasArgs() );
    }

    public void testBaseOptionCharOpt() {
        Option base = OptionBuilder.withDescription( "option description")
                                   .create( 'o' );

        assertEquals( "o", base.getOpt() );
        assertEquals( "option description", base.getDescription() );
        assertTrue( !base.hasArg() );
    }

    public void testBaseOptionStringOpt() {
        Option base = OptionBuilder.withDescription( "option description")
                                   .create( "o" );

        assertEquals( "o", base.getOpt() );
        assertEquals( "option description", base.getDescription() );
        assertTrue( !base.hasArg() );
    }

    public void testSpecialOptChars() {

        // '?'
        try {
            Option opt = OptionBuilder.withDescription( "help options" )
                                      .create( '?' );
            assertEquals( "?", opt.getOpt() );
        }
        catch( IllegalArgumentException arg ) {
            fail( "IllegalArgumentException caught" );
        }

        // '@'
        try {
            Option opt = OptionBuilder.withDescription( "read from stdin" )
                                      .create( '@' );
            assertEquals( "@", opt.getOpt() );
        }
        catch( IllegalArgumentException arg ) {
            fail( "IllegalArgumentException caught" );
        }
    }

    public void testOptionArgNumbers() {
        Option opt = OptionBuilder.withDescription( "option description" )
                                  .hasArgs( 2 )
                                  .create( 'o' );
        assertEquals( 2, opt.getArgs() );
    }

    public void testIllegalOptions() {
        // bad single character option
        try {
            Option opt = OptionBuilder.withDescription( "option description" )
                                      .create( '"' );
            fail( "IllegalArgumentException not caught" );
        }
        catch( IllegalArgumentException exp ) {
            // success
        }

        // bad character in option string
        try {
            Option opt = OptionBuilder.create( "opt`" );
            fail( "IllegalArgumentException not caught" );
        }
        catch( IllegalArgumentException exp ) {
            // success
        }

        // valid option 
        try {
            Option opt = OptionBuilder.create( "opt" );
            // success
        }
        catch( IllegalArgumentException exp ) {
            fail( "IllegalArgumentException caught" );
        }
    }
}