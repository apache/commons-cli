/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
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
 * <li>ls</li>
 * <li>Ant</li>
 * <li>Groovy</li>
 * <li>man</li>
 * </ul>
 * </p>
 */
@SuppressWarnings("deprecation") // tests some deprecated classes
public class ApplicationTest {
    /**
     * Ant test
     */
    @Test
    public void testAnt() throws Exception {
        // use the GNU parser
        final CommandLineParser parser = new GnuParser();
        final Options options = new Options();
        options.addOption("help", false, "print this message");
        options.addOption("projecthelp", false, "print project help information");
        options.addOption("version", false, "print the version information and exit");
        options.addOption("quiet", false, "be extra quiet");
        options.addOption("verbose", false, "be extra verbose");
        options.addOption("debug", false, "print debug information");
        options.addOption("logfile", true, "use given file for log");
        options.addOption("logger", true, "the class which is to perform the logging");
        options.addOption("listener", true, "add an instance of a class as a project listener");
        options.addOption("buildfile", true, "use given buildfile");
        //@formatter:off
        options.addOption(OptionBuilder.withDescription("use value for given property")
                                        .hasArgs()
                                        .withValueSeparator()
                                        .create('D'));
                           //, null, true, , false, true);
        //@formatter:on
        options.addOption("find", true, "search for buildfile towards the root of the filesystem and use it");

        final String[] args = {"-buildfile", "mybuild.xml", "-Dproperty=value", "-Dproperty1=value1", "-projecthelp"};

        final CommandLine line = parser.parse(options, args);

        // check multiple values
        final String[] opts = line.getOptionValues("D");
        assertEquals("property", opts[0]);
        assertEquals("value", opts[1]);
        assertEquals("property1", opts[2]);
        assertEquals("value1", opts[3]);

        // check single value
        assertEquals(line.getOptionValue("buildfile"), "mybuild.xml");

        // check option
        assertTrue(line.hasOption("projecthelp"));
    }

    @Test
    public void testGroovy() throws Exception {
        final Options options = new Options();

        //@formatter:off
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
        //@formatter:on

        final Parser parser = new PosixParser();
        final CommandLine line = parser.parse(options, new String[] {"-e", "println 'hello'"}, true);

        assertTrue(line.hasOption('e'));
        assertEquals("println 'hello'", line.getOptionValue('e'));
    }

    @Test
    public void testLs() throws Exception {
        // create the command line parser
        final CommandLineParser parser = new PosixParser();
        final Options options = new Options();
        options.addOption("a", "all", false, "do not hide entries starting with .");
        options.addOption("A", "almost-all", false, "do not list implied . and ..");
        options.addOption("b", "escape", false, "print octal escapes for nongraphic characters");
        //@formatter:off
        options.addOption(OptionBuilder.withLongOpt("block-size")
                                        .withDescription("use SIZE-byte blocks")
                                        .hasArg()
                                        .withArgName("SIZE")
                                        .create());
        //@formatter:on
        options.addOption("B", "ignore-backups", false, "do not list implied entried ending with ~");
        options.addOption("c", false, "with -lt: sort by, and show, ctime (time of last modification of file status information) with "
            + "-l:show ctime and sort by name otherwise: sort by ctime");
        options.addOption("C", false, "list entries by columns");

        final String[] args = {"--block-size=10"};

        final CommandLine line = parser.parse(options, args);
        assertTrue(line.hasOption("block-size"));
        assertEquals(line.getOptionValue("block-size"), "10");
    }

    /**
     * author Slawek Zachcial
     */
    @Test
    public void testMan() {
        final String cmdLine = "man [-c|-f|-k|-w|-tZT device] [-adlhu7V] [-Mpath] [-Ppager] [-Slist] [-msystem] [-pstring] [-Llocale] [-eextension] [section]"
            + " page ...";
        //@formatter:off
        final Options options = new Options().
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
                addOption("m", "systems", true, "search for man pages from other Unix system(s).").
                //FIXME - should generate -L,--locale locale
                addOption("L", "locale", true, "define the locale for this particular man search.").
                //FIXME - should generate -p,--preprocessor string
                addOption("p", "preprocessor", true, "string indicates which preprocessor to run.\n" +
                         " e - [n]eqn  p - pic     t - tbl\n" +
                         " g - grap    r - refer   v - vgrind").
                addOption("V", "version", false, "show version.").
                addOption("h", "help", false, "show this usage message.");
        //@formatter:on

        final HelpFormatter hf = new HelpFormatter();
        final String eol = System.getProperty("line.separator");
        final StringWriter out = new StringWriter();
        hf.printHelp(new PrintWriter(out), 60, cmdLine, null, options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null, false);
        //@formatter:off
        assertEquals("usage: man [-c|-f|-k|-w|-tZT device] [-adlhu7V] [-Mpath]" + eol +
                        "           [-Ppager] [-Slist] [-msystem] [-pstring]" + eol +
                        "           [-Llocale] [-eextension] [section] page ..." + eol +
                        " -7,--ascii                display ASCII translation or" + eol +
                        "                           certain latin1 chars." + eol +
                        " -a,--all                  find all matching manual pages." + eol +
                        " -c,--catman               used by catman to reformat out of" + eol +
                        "                           date cat pages." + eol +
                        " -d,--debug                emit debugging messages." + eol +
                        " -D,--default              reset all options to their" + eol +
                        "                           default values." + eol +
                        " -e,--extension            limit search to extension type" + eol +
                        "                           'extension'." + eol +
                        " -f,--whatis               equivalent to whatis." + eol +
                        " -h,--help                 show this usage message." + eol +
                        " -k,--apropos              equivalent to apropos." + eol +
                        " -l,--local-file           interpret 'page' argument(s) as" + eol +
                        "                           local filename(s)" + eol +
                        " -L,--locale <arg>         define the locale for this" + eol +
                        "                           particular man search." + eol +
                        " -M,--manpath <arg>        set search path for manual pages" + eol +
                        "                           to 'path'." + eol +
                        " -m,--systems <arg>        search for man pages from other" + eol +
                        "                           Unix system(s)." + eol +
                        " -P,--pager <arg>          use program 'pager' to display" + eol +
                        "                           output." + eol +
                        " -p,--preprocessor <arg>   string indicates which" + eol +
                        "                           preprocessor to run." + eol +
                        "                           e - [n]eqn  p - pic     t - tbl" + eol +
                        "                           g - grap    r - refer   v -" + eol +
                        "                           vgrind" + eol +
                        " -r,--prompt <arg>         provide 'less' pager with prompt." + eol +
                        " -S,--sections <arg>       use colon separated section list." + eol +
                        " -t,--troff                use troff format pages." + eol +
                        " -T,--troff-device <arg>   use groff with selected device." + eol +
                        " -u,--update               force a cache consistency check." + eol +
                        " -V,--version              show version." + eol +
                        " -w,--location             print physical location of man" + eol +
                        "                           page(s)." + eol +
                        " -Z,--ditroff              use groff with selected device." + eol,
                out.toString());
        //@formatter:on
    }

    /**
     * Real world test with long and short options.
     */
    @Test
    public void testNLT() throws Exception {
        final Option help = new Option("h", "help", false, "print this message");
        final Option version = new Option("v", "version", false, "print version information");
        final Option newRun = new Option("n", "new", false, "Create NLT cache entries only for new items");
        final Option trackerRun = new Option("t", "tracker", false, "Create NLT cache entries only for tracker items");

        //@formatter:off
        final Option timeLimit = OptionBuilder.withLongOpt("limit").hasArg()
                                        .withValueSeparator()
                                        .withDescription("Set time limit for execution, in minutes")
                                        .create("l");

        final Option age = OptionBuilder.withLongOpt("age").hasArg()
                                  .withValueSeparator()
                                  .withDescription("Age (in days) of cache item before being recomputed")
                                  .create("a");

        final Option server = OptionBuilder.withLongOpt("server").hasArg()
                                     .withValueSeparator()
                                     .withDescription("The NLT server address")
                                     .create("s");

        final Option numResults = OptionBuilder.withLongOpt("results").hasArg()
                                         .withValueSeparator()
                                         .withDescription("Number of results per item")
                                         .create("r");

        final Option configFile = OptionBuilder.withLongOpt("file").hasArg()
                                         .withValueSeparator()
                                         .withDescription("Use the specified configuration file")
                                         .create();
        //@formatter:on

        final Options options = new Options();
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
        final CommandLineParser parser = new PosixParser();

        final String[] args = {"-v", "-l", "10", "-age", "5", "-file", "filename"};

        final CommandLine line = parser.parse(options, args);
        assertTrue(line.hasOption("v"));
        assertEquals(line.getOptionValue("l"), "10");
        assertEquals(line.getOptionValue("limit"), "10");
        assertEquals(line.getOptionValue("a"), "5");
        assertEquals(line.getOptionValue("age"), "5");
        assertEquals(line.getOptionValue("file"), "filename");
    }
}
