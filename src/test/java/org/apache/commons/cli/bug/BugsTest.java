/*
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

package org.apache.commons.cli.bug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.junit.Test;

@SuppressWarnings("deprecation") // tests some deprecated classes
public class BugsTest {
    @Test
    public void test11456() throws Exception {
        // Posix
        Options options = new Options();
        options.addOption(OptionBuilder.hasOptionalArg().create('a'));
        options.addOption(OptionBuilder.hasArg().create('b'));
        String[] args = {"-a", "-bvalue"};

        CommandLineParser parser = new PosixParser();

        CommandLine cmd = parser.parse(options, args);
        assertEquals(cmd.getOptionValue('b'), "value");

        // GNU
        options = new Options();
        options.addOption(OptionBuilder.hasOptionalArg().create('a'));
        options.addOption(OptionBuilder.hasArg().create('b'));
        args = new String[] {"-a", "-b", "value"};

        parser = new GnuParser();

        cmd = parser.parse(options, args);
        assertEquals(cmd.getOptionValue('b'), "value");
    }

    @Test
    public void test11457() throws Exception {
        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("verbose").create());
        final String[] args = {"--verbose"};

        final CommandLineParser parser = new PosixParser();

        final CommandLine cmd = parser.parse(options, args);
        assertTrue(cmd.hasOption("verbose"));
    }

    @Test
    public void test11458() throws Exception {
        final Options options = new Options();
        options.addOption(OptionBuilder.withValueSeparator('=').hasArgs().create('D'));
        options.addOption(OptionBuilder.withValueSeparator(':').hasArgs().create('p'));
        final String[] args = {"-DJAVA_HOME=/opt/java", "-pfile1:file2:file3"};

        final CommandLineParser parser = new PosixParser();

        final CommandLine cmd = parser.parse(options, args);

        String[] values = cmd.getOptionValues('D');

        assertEquals(values[0], "JAVA_HOME");
        assertEquals(values[1], "/opt/java");

        values = cmd.getOptionValues('p');

        assertEquals(values[0], "file1");
        assertEquals(values[1], "file2");
        assertEquals(values[2], "file3");

        final Iterator<Option> iter = cmd.iterator();
        while (iter.hasNext()) {
            final Option opt = iter.next();
            switch (opt.getId()) {
            case 'D':
                assertEquals(opt.getValue(0), "JAVA_HOME");
                assertEquals(opt.getValue(1), "/opt/java");
                break;
            case 'p':
                assertEquals(opt.getValue(0), "file1");
                assertEquals(opt.getValue(1), "file2");
                assertEquals(opt.getValue(2), "file3");
                break;
            default:
                fail("-D option not found");
            }
        }
    }

    @Test
    public void test11680() throws Exception {
        final Options options = new Options();
        options.addOption("f", true, "foobar");
        options.addOption("m", true, "missing");
        final String[] args = {"-f", "foo"};

        final CommandLineParser parser = new PosixParser();

        final CommandLine cmd = parser.parse(options, args);

        cmd.getOptionValue("f", "default f");
        cmd.getOptionValue("m", "default m");
    }

    @Test
    public void test12210() throws Exception {
        // create the main options object which will handle the first parameter
        final Options mainOptions = new Options();
        // There can be 2 main exclusive options: -exec|-rep

        // Therefore, place them in an option group

        String[] argv = {"-exec", "-exec_opt1", "-exec_opt2"};
        final OptionGroup grp = new OptionGroup();

        grp.addOption(new Option("exec", false, "description for this option"));

        grp.addOption(new Option("rep", false, "description for this option"));

        mainOptions.addOptionGroup(grp);

        // for the exec option, there are 2 options...
        final Options execOptions = new Options();
        execOptions.addOption("exec_opt1", false, " desc");
        execOptions.addOption("exec_opt2", false, " desc");

        // similarly, for rep there are 2 options...
        final Options repOptions = new Options();
        repOptions.addOption("repopto", false, "desc");
        repOptions.addOption("repoptt", false, "desc");

        // create the parser
        final GnuParser parser = new GnuParser();

        // finally, parse the arguments:

        // first parse the main options to see what the user has specified
        // We set stopAtNonOption to true so it does not touch the remaining
        // options
        CommandLine cmd = parser.parse(mainOptions, argv, true);
        // get the remaining options...
        argv = cmd.getArgs();

        if (cmd.hasOption("exec")) {
            cmd = parser.parse(execOptions, argv, false);
            // process the exec_op1 and exec_opt2...
            assertTrue(cmd.hasOption("exec_opt1"));
            assertTrue(cmd.hasOption("exec_opt2"));
        } else if (cmd.hasOption("rep")) {
            cmd = parser.parse(repOptions, argv, false);
            // process the rep_op1 and rep_opt2...
        } else {
            fail("exec option not found");
        }
    }

    @Test
    public void test13425() throws Exception {
        final Options options = new Options();
        //@formatter:off
        final Option oldpass = OptionBuilder.withLongOpt("old-password")
            .withDescription("Use this option to specify the old password")
            .hasArg()
            .create('o');
        final Option newpass = OptionBuilder.withLongOpt("new-password")
            .withDescription("Use this option to specify the new password")
            .hasArg()
            .create('n');
        //@formatter:on

        final String[] args = {"-o", "-n", "newpassword"};

        options.addOption(oldpass);
        options.addOption(newpass);

        final Parser parser = new PosixParser();

        try {
            parser.parse(options, args);
            fail("MissingArgumentException not caught.");
        } catch (final MissingArgumentException expected) {
        }
    }

    @Test
    public void test13666() throws Exception {
        final Options options = new Options();
        final Option dir = OptionBuilder.withDescription("dir").hasArg().create('d');
        options.addOption(dir);

        final PrintStream oldSystemOut = System.out;
        try {
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            final PrintStream print = new PrintStream(bytes);

            // capture this platform's eol symbol
            print.println();
            final String eol = bytes.toString();
            bytes.reset();

            System.setOut(new PrintStream(bytes));

            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("dir", options);

            assertEquals("usage: dir" + eol + " -d <arg>   dir" + eol, bytes.toString());
        } finally {
            System.setOut(oldSystemOut);
        }
    }

    @Test
    public void test13935() throws Exception {
        final OptionGroup directions = new OptionGroup();

        final Option left = new Option("l", "left", false, "go left");
        final Option right = new Option("r", "right", false, "go right");
        final Option straight = new Option("s", "straight", false, "go straight");
        final Option forward = new Option("f", "forward", false, "go forward");
        forward.setRequired(true);

        directions.addOption(left);
        directions.addOption(right);
        directions.setRequired(true);

        final Options opts = new Options();
        opts.addOptionGroup(directions);
        opts.addOption(straight);

        final CommandLineParser parser = new PosixParser();

        String[] args = {};
        try {
            parser.parse(opts, args);
            fail("Expected ParseException");
        } catch (final ParseException expected) {
        }

        args = new String[] {"-s"};
        try {
            parser.parse(opts, args);
            fail("Expected ParseException");
        } catch (final ParseException expected) {
        }

        args = new String[] {"-s", "-l"};
        CommandLine line = parser.parse(opts, args);
        assertNotNull(line);

        opts.addOption(forward);
        args = new String[] {"-s", "-l", "-f"};
        line = parser.parse(opts, args);
        assertNotNull(line);
    }

    @Test
    public void test14786() throws Exception {
        final Option o = OptionBuilder.isRequired().withDescription("test").create("test");
        final Options opts = new Options();
        opts.addOption(o);
        opts.addOption(o);

        final CommandLineParser parser = new GnuParser();

        final String[] args = {"-test"};

        final CommandLine line = parser.parse(opts, args);
        assertTrue(line.hasOption("test"));
    }

    @Test
    public void test15046() throws Exception {
        final CommandLineParser parser = new PosixParser();
        final String[] cliArgs = {"-z", "c"};

        final Options options = new Options();
        options.addOption(new Option("z", "timezone", true, "affected option"));

        parser.parse(options, cliArgs);

        // now add conflicting option
        options.addOption("c", "conflict", true, "conflict option");
        final CommandLine line = parser.parse(options, cliArgs);
        assertEquals(line.getOptionValue('z'), "c");
        assertFalse(line.hasOption("c"));
    }

    @Test
    public void test15648() throws Exception {
        final CommandLineParser parser = new PosixParser();
        final String[] args = {"-m", "\"Two Words\""};
        final Option m = OptionBuilder.hasArgs().create("m");
        final Options options = new Options();
        options.addOption(m);
        final CommandLine line = parser.parse(options, args);
        assertEquals("Two Words", line.getOptionValue("m"));
    }

    @Test
    public void test31148() throws ParseException {
        final Option multiArgOption = new Option("o", "option with multiple args");
        multiArgOption.setArgs(1);

        final Options options = new Options();
        options.addOption(multiArgOption);

        final Parser parser = new PosixParser();
        final String[] args = {};
        final Properties props = new Properties();
        props.setProperty("o", "ovalue");
        final CommandLine cl = parser.parse(options, args, props);

        assertTrue(cl.hasOption('o'));
        assertEquals("ovalue", cl.getOptionValue('o'));
    }

}
