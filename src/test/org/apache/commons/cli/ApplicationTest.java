package org.apache.commons.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ApplicationTest extends TestCase {

    static {
        System.setProperty( "org.apache.commons.cli.parser",
                            "org.apache.commons.cli.GnuParser");
    }

    public static Test suite() { 
        return new TestSuite(ApplicationTest.class); 
    }

    public ApplicationTest(String name)
    {
        super(name);
    }

    public void testAnt() {
        Options options = new Options();
        options.addOption( "help", false, "print this message" );
        options.addOption( "projecthelp", false, "print project help information" );
        options.addOption( "version", false, "print the version information and exit" );
        options.addOption( "quiet", false, "be extra quiet" );
        options.addOption( "verbose", false, "be extra verbose" );
        options.addOption( "debug", false, "print debug information" );
        options.addOption( "version", false, "produce logging information without adornments" );
        options.addOption( "logfile", true, "use given file for log" );
        options.addOption( "logger", true, "the class which is to perform the logging" );
        options.addOption( "listener", true, "add an instance of a class as a project listener" );
        options.addOption( "buildfile", true, "use given buildfile" );
        options.addOption( "D", true, "use value for given property" );
        options.addOption( "find", true, "search for buildfile towards the root of the filesystem and use it" );

        String[] args = new String[]{ "-buildfile", "mybuild.xml" };

        try {
            CommandLine line = options.parse( args );
            assertTrue( "mybuild.xml" == line.getOptionValue( "buildfile" ) );
        }
        catch( ParseException exp ) {
            fail( "Unexpected exception:" + exp.getMessage() );
        }

        args = new String[]{ "-buildfile", "mybuild.xml",
        "-Dproperty=value" };
    
        try {
            CommandLine line = options.parse( args );
            assertEquals( line.getOptionValue( "D" ), "property=value" );
        }
        catch( ParseException exp ) {
            fail( "Unexpected exception:" + exp.getMessage() );
        }
    }

}