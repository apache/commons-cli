package org.apache.commons.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>
 * This is a collection of tests that test real world
 * applications command lines.
 * </p>
 * 
 * <p>
 * The following are the applications that are tested:
 * <ul>
 * <li>Ant</li>
 * </ul>
 * </p>
 *
 * @author John Keyes (jbjk at mac.com)
 */
public class ApplicationTest extends TestCase {

    public static Test suite() { 
        return new TestSuite(ApplicationTest.class); 
    }

    public ApplicationTest(String name)
    {
        super(name);
    }

    /**
     * Ant test
     */
    public void testAnt() {
        // use the GNU parser
        CommandLineParser parser = CommandLineParserFactory.newParser( "org.apache.commons.cli.GnuParser" );
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
        options.addOption( OptionBuilder.withDescription( "use value for given property" )
                                        .hasArg()
                                        .hasMultipleArgs()
                                        .create( 'D' ) );
                           //, null, true, , false, true );
        options.addOption( "find", true, "search for buildfile towards the root of the filesystem and use it" );

        String[] args = new String[]{ "-buildfile", "mybuild.xml",
            "-Dproperty=value", "-Dproperty1=value1",
            "-projecthelp" };

        try {
            CommandLine line = parser.parse( options, args );

            // check multiple values
            String[] opts = line.getOptionValues( "D" );
            assertEquals( opts[0], "property=value" );
            assertEquals( opts[1], "property1=value1" );

            // check single value
            assertEquals( line.getOptionValue( "buildfile"), "mybuild.xml" );

            // check option
            assertTrue( line.hasOption( "projecthelp") );
        }
        catch( ParseException exp ) {
            fail( "Unexpected exception:" + exp.getMessage() );
        }

    }

}