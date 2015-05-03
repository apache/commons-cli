/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

/**
 * This is a collection of tests that test real world applications command lines.
 *
 * <p>
 * The following applications are tested:
 * <ul>
 *   <li>ls</li>
 *   <li>Ant</li>
 *   <li>Groovy</li>
 *   <li>man</li>
 * </ul>
 * </p>
 */
@SuppressWarnings("deprecation") // tests some deprecated classes
public class ApplicationTest
{
    @Test
    public void testLs() throws Exception {
        // create the command line parser
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption( "a", "all", false, "do not hide entries starting with ." );
        options.addOption( "A", "almost-all", false, "do not list implied . and .." );
        options.addOption( "b", "escape", false, "print octal escapes for nongraphic characters" );
        options.addOption( OptionBuilder.withLongOpt( "block-size" )
                                        .withDescription( "use SIZE-byte blocks" )
                                        .hasArg()
                                        .withArgName("SIZE")
                                        .create() );
        options.addOption( "B", "ignore-backups", false, "do not list implied entried ending with ~");
        options.addOption( "c", false, "with -lt: sort by, and show, ctime (time of last modification of file status information) with -l:show ctime and sort by name otherwise: sort by ctime" );
        options.addOption( "C", false, "list entries by columns" );

        String[] args = new String[]{ "--block-size=10" };

        CommandLine line = parser.parse( options, args );
        assertTrue( line.hasOption( "block-size" ) );
        assertEquals( line.getOptionValue( "block-size" ), "10" );
    }

    /**
     * Ant test
     */
    @Test
    public void testAnt() throws Exception {
        // use the GNU parser
        CommandLineParser parser = new GnuParser( );
        Options options = new Options();
        options.addOption( "help", false, "print this message" );
        options.addOption( "projecthelp", false, "print project help information" );
        options.addOption( "version", false, "print the version information and exit" );
        options.addOption( "quiet", false, "be extra quiet" );
        options.addOption( "verbose", false, "be extra verbose" );
        options.addOption( "debug", false, "print debug information" );
        options.addOption( "logfile", true, "use given file for log" );
        options.addOption( "logger", true, "the class which is to perform the logging" );
        options.addOption( "listener", true, "add an instance of a class as a project listener" );
        options.addOption( "buildfile", true, "use given buildfile" );
        options.addOption( OptionBuilder.withDescription( "use value for given property" )
                                        .hasArgs()
                                        .withValueSeparator()
                                        .create( 'D' ) );
                           //, null, true, , false, true );
        options.addOption( "find", true, "search for buildfile towards the root of the filesystem and use it" );

        String[] args = new String[]{ "-buildfile", "mybuild.xml",
            "-Dproperty=value", "-Dproperty1=value1",
            "-projecthelp" };

        CommandLine line = parser.parse( options, args );

        // check multiple values
        String[] opts = line.getOptionValues( "D" );
        assertEquals( "property", opts[0] );
        assertEquals( "value", opts[1] );
        assertEquals( "property1", opts[2] );
        assertEquals( "value1", opts[3] );

        // check single value
        assertEquals( line.getOptionValue( "buildfile"), "mybuild.xml" );

        // check option
        assertTrue( line.hasOption( "projecthelp") );
    }

    @Test
    public void testGroovy() throws Exception {
        Options options = new Options();

        options.addOption(
            OptionBuilder.withLongOpt("define").
                withDescription("define a system property").
                hasArg(true).
                withArgName("name=value").
                create('D'));
        options.addOption(
            OptionBuilder.hasArg(false)
            .withDescription("usage information")
            .withLongOpt("help")
            .create('h'));
        options.addOption(
            OptionBuilder.hasArg(false)
            .withDescription("debug mode will print out full stack traces")
            .withLongOpt("debug")
            .create('d'));
        options.addOption(
            OptionBuilder.hasArg(false)
            .withDescription("display the Groovy and JVM versions")
            .withLongOpt("version")
            .create('v'));
        options.addOption(
            OptionBuilder.withArgName("charset")
            .hasArg()
            .withDescription("specify the encoding of the files")
            .withLongOpt("encoding")
            .create('c'));
        options.addOption(
            OptionBuilder.withArgName("script")
            .hasArg()
            .withDescription("specify a command line script")
            .create('e'));
        options.addOption(
            OptionBuilder.withArgName("extension")
            .hasOptionalArg()
            .withDescription("modify files in place; create backup if extension is given (e.g. \'.bak\')")
            .create('i'));
        options.addOption(
            OptionBuilder.hasArg(false)
            .withDescription("process files line by line using implicit 'line' variable")
            .create('n'));
        options.addOption(
            OptionBuilder.hasArg(false)
            .withDescription("process files line by line and print result (see also -n)")
            .create('p'));
        options.addOption(
            OptionBuilder.withArgName("port")
            .hasOptionalArg()
            .withDescription("listen on a port and process inbound lines")
            .create('l'));
        options.addOption(
            OptionBuilder.withArgName("splitPattern")
            .hasOptionalArg()
            .withDescription("split lines using splitPattern (default '\\s') using implicit 'split' variable")
            .withLongOpt("autosplit")
            .create('a'));

        Parser parser = new PosixParser();
        CommandLine line = parser.parse(options, new String[] { "-e", "println 'hello'" }, true);

        assertTrue(line.hasOption('e'));
        assertEquals("println 'hello'", line.getOptionValue('e'));
    }

    /**
     * author Slawek Zachcial
     */
    @Test
    public void testMan()
    {
        String cmdLine =
                "man [-c|-f|-k|-w|-tZT device] [-adlhu7V] [-Mpath] [-Ppager] [-Slist] " +
                        "[-msystem] [-pstring] [-Llocale] [-eextension] [section] page ...";
        Options options = new Options().
                addOption("a", "all", false, "find all matching manual pages.").
                addOption("d", "debug", false, "emit debugging messages.").
                addOption("e", "extension", false, "limit search to extension type 'extension'.").
                addOption("f", "whatis", false, "equivalent to whatis.").
                addOption("k", "apropos", false, "equivalent to apropos.").
                addOption("w", "location", false, "print physical location of man page(s).").
                addOption("l", "local-file", false, "interpret 'page' argument(s) as local filename(s)").
                addOption("u", "update", false, "force a cache consistency check.").
                //FIXME - should generate -r,--prompt string
                addOption("r", "prompt", true, "provide 'less' pager with prompt.").
                addOption("c", "catman", false, "used by catman to reformat out of date cat pages.").
                addOption("7", "ascii", false, "display ASCII translation or certain latin1 chars.").
                addOption("t", "troff", false, "use troff format pages.").
                //FIXME - should generate -T,--troff-device device
                addOption("T", "troff-device", true, "use groff with selected device.").
                addOption("Z", "ditroff", false, "use groff with selected device.").
                addOption("D", "default", false, "reset all options to their default values.").
                //FIXME - should generate -M,--manpath path
                addOption("M", "manpath", true, "set search path for manual pages to 'path'.").
                //FIXME - should generate -P,--pager pager
                addOption("P", "pager", true, "use program 'pager' to display output.").
                //FIXME - should generate -S,--sections list
                addOption("S", "sections", true, "use colon separated section list.").
                //FIXME - should generate -m,--systems system
                addOption("m", "systems", true, "search for man pages from other unix system(s).").
                //FIXME - should generate -L,--locale locale
                addOption("L", "locale", true, "define the locale for this particular man search.").
                //FIXME - should generate -p,--preprocessor string
                addOption("p", "preprocessor", true, "string indicates which preprocessor to run.\n" +
                         " e - [n]eqn  p - pic     t - tbl\n" +
                         " g - grap    r - refer   v - vgrind").
                addOption("V", "version", false, "show version.").
                addOption("h", "help", false, "show this usage message.");

        HelpFormatter hf = new HelpFormatter();
        final String EOL = System.getProperty("line.separator");
        StringWriter out = new StringWriter();
        hf.printHelp(new PrintWriter(out), 60, cmdLine, null, options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null, false);
        assertEquals("usage: man [-c|-f|-k|-w|-tZT device] [-adlhu7V] [-Mpath]" + EOL +
                        "           [-Ppager] [-Slist] [-msystem] [-pstring]" + EOL +
                        "           [-Llocale] [-eextension] [section] page ..." + EOL +
                        " -7,--ascii                display ASCII translation or" + EOL +
                        "                           certain latin1 chars." + EOL +
                        " -a,--all                  find all matching manual pages." + EOL +
                        " -c,--catman               used by catman to reformat out of" + EOL +
                        "                           date cat pages." + EOL +
                        " -d,--debug                emit debugging messages." + EOL +
                        " -D,--default              reset all options to their" + EOL +
                        "                           default values." + EOL +
                        " -e,--extension            limit search to extension type" + EOL +
                        "                           'extension'." + EOL +
                        " -f,--whatis               equivalent to whatis." + EOL +
                        " -h,--help                 show this usage message." + EOL +
                        " -k,--apropos              equivalent to apropos." + EOL +
                        " -l,--local-file           interpret 'page' argument(s) as" + EOL +
                        "                           local filename(s)" + EOL +
                        " -L,--locale <arg>         define the locale for this" + EOL +
                        "                           particular man search." + EOL +
                        " -M,--manpath <arg>        set search path for manual pages" + EOL +
                        "                           to 'path'." + EOL +
                        " -m,--systems <arg>        search for man pages from other" + EOL +
                        "                           unix system(s)." + EOL +
                        " -P,--pager <arg>          use program 'pager' to display" + EOL +
                        "                           output." + EOL +
                        " -p,--preprocessor <arg>   string indicates which" + EOL +
                        "                           preprocessor to run." + EOL +
                        "                           e - [n]eqn  p - pic     t - tbl" + EOL +
                        "                           g - grap    r - refer   v -" + EOL +
                        "                           vgrind" + EOL +
                        " -r,--prompt <arg>         provide 'less' pager with prompt." + EOL +
                        " -S,--sections <arg>       use colon separated section list." + EOL +
                        " -t,--troff                use troff format pages." + EOL +
                        " -T,--troff-device <arg>   use groff with selected device." + EOL +
                        " -u,--update               force a cache consistency check." + EOL +
                        " -V,--version              show version." + EOL +
                        " -w,--location             print physical location of man" + EOL +
                        "                           page(s)." + EOL +
                        " -Z,--ditroff              use groff with selected device." + EOL,
                out.toString());
    }


    /**
     * Real world test with long and short options.
     */
    @Test
    public void testNLT() throws Exception {
        Option help = new Option("h", "help", false, "print this message");
        Option version = new Option("v", "version", false, "print version information");
        Option newRun = new Option("n", "new", false, "Create NLT cache entries only for new items");
        Option trackerRun = new Option("t", "tracker", false, "Create NLT cache entries only for tracker items");

        Option timeLimit = OptionBuilder.withLongOpt("limit").hasArg()
                                        .withValueSeparator()
                                        .withDescription("Set time limit for execution, in minutes")
                                        .create("l");

        Option age = OptionBuilder.withLongOpt("age").hasArg()
                                  .withValueSeparator()
                                  .withDescription("Age (in days) of cache item before being recomputed")
                                  .create("a");

        Option server = OptionBuilder.withLongOpt("server").hasArg()
                                     .withValueSeparator()
                                     .withDescription("The NLT server address")
                                     .create("s");

        Option numResults = OptionBuilder.withLongOpt("results").hasArg()
                                         .withValueSeparator()
                                         .withDescription("Number of results per item")
                                         .create("r");

        Option configFile = OptionBuilder.withLongOpt("file").hasArg()
                                         .withValueSeparator()
                                         .withDescription("Use the specified configuration file")
                                         .create();

        Options options = new Options();
        options.addOption(help);
        options.addOption(version);
        options.addOption(newRun);
        options.addOption(trackerRun);
        options.addOption(timeLimit);
        options.addOption(age);
        options.addOption(server);
        options.addOption(numResults);
        options.addOption(configFile);

        // create the command line parser
        CommandLineParser parser = new PosixParser();

        String[] args = new String[] {
                "-v",
                "-l",
                "10",
                "-age",
                "5",
                "-file",
                "filename"
            };

        CommandLine line = parser.parse(options, args);
        assertTrue(line.hasOption("v"));
        assertEquals(line.getOptionValue("l"), "10");
        assertEquals(line.getOptionValue("limit"), "10");
        assertEquals(line.getOptionValue("a"), "5");
        assertEquals(line.getOptionValue("age"), "5");
        assertEquals(line.getOptionValue("file"), "filename");
    }
}
