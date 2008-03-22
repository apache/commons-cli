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
package org.apache.commons.cli2.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.ArgumentImpl;
import org.apache.commons.cli2.option.SourceDestArgument;
import org.apache.commons.cli2.util.HelpFormatter;

/**
 * <p>Test the <code>cp</code> command. Duplicated Option types are not
 * tested e.g. -a and -d are the same Option type.</p>
 *
 * <p>The following is the man output for 'cp'. See
 * <a href="http://www.rt.com/man/cp.1.html">http://www.rt.com/man/cp.1.html</a>.</p>
 *
 * <pre>
 *  CP(1) FSF CP(1)
 *
 *  NAME cp - copy files and directories
 *
 *  SYNOPSIS cp [OPTION]... SOURCE DEST cp [OPTION]... SOURCE... DIRECTORY
 *
 *  DESCRIPTION Copy SOURCE to DEST, or multiple SOURCE(s) to DIRECTORY.
 *
 *  -a, --archive same as -dpR
 *
 *  -b, --backup make backup before removal
 *
 *  -d, --no-dereference preserve links
 *
 *  -f, --force remove existing destinations, never prompt
 *
 *  -i, --interactive prompt before overwrite
 *
 *  -l, --link link files instead of copying
 *
 *  -p, --preserve preserve file attributes if possible
 *
 *  -P, --parents append source path to DIRECTORY
 * -r copy recursively, non-directories as files
 *
 *  --sparse=WHEN control creation of sparse files
 *
 *  -R, --recursive copy directories recursively
 *
 *  -s, --symbolic-link make symbolic links instead of copying
 *
 *  -S, --suffix=SUFFIX override the usual backup suffix
 *
 *  -u, --update copy only when the SOURCE file is newer than the destination file or when the destination file is missing
 *
 *  -v, --verbose explain what is being done
 *
 *  -V, --version-control=WORD override the usual version control
 *
 *  -x, --one-file-system stay on this file system
 *
 *  --help display this help and exit
 *
 *  --version output version information and exit
 *
 *  By default, sparse SOURCE files are detected by a crude heuristic and the corresponding DEST file is made sparse as well. That is the behavior selected by --sparse=auto. Specify --sparse=always to create a sparse DEST file when- ever the SOURCE file contains a long enough sequence of zero bytes. Use --sparse=never to inhibit creation of sparse files.
 *
 *  The backup suffix is ~, unless set with SIMPLE_BACKUP_SUF- FIX. The version control may be set with VERSION_CONTROL, values are:
 * t, numbered make numbered backups
 *
 *  nil, existing numbered if numbered backups exist, simple other- wise
 *
 *  never, simple always make simple backups
 *
 *  As a special case, cp makes a backup of SOURCE when the force and backup options are given and SOURCE and DEST are the same name for an existing, regular file. * </pre>
 * </pre>
 *
 * @author Rob Oxspring
 * @author John Keyes
 */
public class CpTest extends TestCase {

    /** Option Builder */
    private static final DefaultOptionBuilder oBuilder =
        new DefaultOptionBuilder();

    /** Argument Builder */
    private static final ArgumentBuilder aBuilder = new ArgumentBuilder();

    /** Group Builder */
    private static final GroupBuilder gBuilder = new GroupBuilder();

    private Group options;

    public static Test suite() {
        return new TestSuite(CpTest.class);
    }

    private ArgumentImpl source;
    private ArgumentImpl dest;
    private Argument targets;

    private Option archive;
    private Option backup;
    private Option noDereference;
    private Option force;
    private Option interactive;
    private Option link;
    private Option preserve;
    private Option parents;
    private Option recursive1;
    private Option sparse;
    private Option recursive2;
    private Option symbolicLink;
    private Option suffix;
    private Option update;
    private Option verbose;
    private Option versionControl;
    private Option oneFileSystem;
    private Option help;
    private Option version;

    public void setUp() {
        source =
            (ArgumentImpl)aBuilder.withName("SOURCE").withMinimum(1).create();
        dest =
            (ArgumentImpl)aBuilder
                .withName("DEST")
                .withMinimum(1)
                .withMaximum(1)
                .create();
        targets = new SourceDestArgument(source, dest);

        archive =
            oBuilder
                .withShortName("a")
                .withLongName("archive")
                .withDescription("same as -dpR")
                .create();

        backup =
            oBuilder
                .withShortName("b")
                .withLongName("backup")
                .withDescription("make backup before removal")
                .create();

        noDereference =
            oBuilder
                .withShortName("d")
                .withLongName("no-dereference")
                .withDescription("preserve links")
                .create();

        force =
            oBuilder
                .withShortName("f")
                .withLongName("force")
                .withDescription("remove existing destinations, never prompt")
                .create();

        interactive =
            oBuilder
                .withShortName("i")
                .withLongName("interactive")
                .withDescription("prompt before overwrite")
                .create();

        link =
            oBuilder
                .withShortName("l")
                .withLongName("link")
                .withDescription("link files instead of copying")
                .create();

        preserve =
            oBuilder
                .withShortName("p")
                .withLongName("preserve")
                .withDescription("preserve file attributes if possible")
                .create();

        parents =
            oBuilder
                .withShortName("P")
                .withLongName("parents")
                .withDescription("append source path to DIRECTORY")
                .create();

        recursive1 =
            oBuilder
                .withShortName("r")
                .withDescription("copy recursively, non-directories as files")
                .create();

        sparse =
            oBuilder
                .withLongName("sparse")
                .withDescription("control creation of sparse files")
                .withArgument(
                    aBuilder
                        .withName("WHEN")
                        .withMinimum(1)
                        .withMaximum(1)
                        .withInitialSeparator('=')
                        .create())
                .create();

        recursive2 =
            oBuilder
                .withShortName("R")
                .withLongName("recursive")
                .withDescription("copy directories recursively")
                .create();

        symbolicLink =
            oBuilder
                .withShortName("s")
                .withLongName("symbolic-link")
                .withDescription("make symbolic links instead of copying")
                .create();

        suffix =
            oBuilder
                .withShortName("S")
                .withLongName("suffix")
                .withDescription("override the usual backup suffix")
                .withArgument(
                    aBuilder
                        .withName("SUFFIX")
                        .withMinimum(1)
                        .withMaximum(1)
                        .create())
                .create();

        update =
            oBuilder
                .withShortName("u")
                .withLongName("update")
                .withDescription("copy only when the SOURCE file is newer than the destination file or when the destination file is missing")
                .create();

        verbose =
            oBuilder
                .withShortName("v")
                .withLongName("verbose")
                .withDescription("explain what is being done")
                .create();

        versionControl =
            oBuilder
                .withShortName("V")
                .withLongName("version-contol")
                .withDescription("explain what is being done")
                .withArgument(
                    aBuilder
                        .withName("WORD")
                        .withInitialSeparator('=')
                        .withMinimum(1)
                        .withMaximum(1)
                        .create())
                .create();

        oneFileSystem =
            oBuilder
                .withShortName("x")
                .withLongName("one-file-system")
                .withDescription("stay on this file system")
                .create();

        help =
            oBuilder
                .withLongName("help")
                .withDescription("display this help and exit")
                .create();

        version =
            oBuilder
                .withLongName("version")
                .withDescription("output version information and exit")
                .create();

        options =
            gBuilder
                .withOption(archive)
                .withOption(backup)
                .withOption(noDereference)
                .withOption(force)
                .withOption(interactive)
                .withOption(link)
                .withOption(preserve)
                .withOption(parents)
                .withOption(recursive1)
                .withOption(sparse)
                .withOption(recursive2)
                .withOption(symbolicLink)
                .withOption(suffix)
                .withOption(update)
                .withOption(verbose)
                .withOption(versionControl)
                .withOption(oneFileSystem)
                .withOption(help)
                .withOption(version)
                .withOption(targets)
                .withName("OPTIONS")
                .create();
    }

    public void testNoSource() {
        Parser parser = new Parser();
        parser.setGroup(options);
        try {
            parser.parse(new String[0]);
        }
        catch (OptionException mve) {
            assertEquals(
                "Missing value(s) SOURCE [SOURCE ...]",
                mve.getMessage());
        }
    }

    public void testOneSource() throws OptionException {
        final String[] args = new String[] { "source1", "dest1" };
        final Parser parser = new Parser();
        parser.setGroup(options);
        final CommandLine commandLine = parser.parse(args);

        assertTrue(commandLine.getValues(source).contains("source1"));
        assertEquals(1, commandLine.getValues(source).size());
        assertTrue(commandLine.getValues(dest).contains("dest1"));
        assertEquals(1, commandLine.getValues(dest).size());
    }

    public void testMultiSource() throws OptionException {
        final String[] args =
            new String[] { "source1", "source2", "source3", "dest1" };
        final Parser parser = new Parser();
        parser.setGroup(options);
        final CommandLine commandLine = parser.parse(args);

        assertTrue(commandLine.getValues(source).contains("source1"));
        assertTrue(commandLine.getValues(source).contains("source2"));
        assertTrue(commandLine.getValues(source).contains("source3"));
        assertEquals(3, commandLine.getValues(source).size());

        assertTrue(commandLine.getValues(dest).contains("dest1"));
        assertEquals(1, commandLine.getValues(dest).size());
    }

    public void testHelp() throws IOException {
        final StringWriter out = new StringWriter();
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setGroup(options);
        helpFormatter.setPrintWriter(new PrintWriter(out));
        helpFormatter.print();

        final BufferedReader in =
            new BufferedReader(new StringReader(out.toString()));
        assertEquals(
            "Usage:                                                                          ",
            in.readLine());
        assertEquals(
            " [-a -b -d -f -i -l -p -P -r --sparse <WHEN> -R -s -S <SUFFIX> -u -v -V <WORD>  ",
            in.readLine());
        assertEquals(
            "-x --help --version] <SOURCE1> [<SOURCE2> ...] <DEST>                           ",
            in.readLine());
        assertEquals(
            "OPTIONS                                                                         ",
            in.readLine());
        assertEquals(
            "  -a (--archive)                same as -dpR                                    ",
            in.readLine());
        assertEquals(
            "  -b (--backup)                 make backup before removal                      ",
            in.readLine());
        assertEquals(
            "  -d (--no-dereference)         preserve links                                  ",
            in.readLine());
        assertEquals(
            "  -f (--force)                  remove existing destinations, never prompt      ",
            in.readLine());
        assertEquals(
            "  -i (--interactive)            prompt before overwrite                         ",
            in.readLine());
        assertEquals(
            "  -l (--link)                   link files instead of copying                   ",
            in.readLine());
        assertEquals(
            "  -p (--preserve)               preserve file attributes if possible            ",
            in.readLine());
        assertEquals(
            "  -P (--parents)                append source path to DIRECTORY                 ",
            in.readLine());
        assertEquals(
            "  -r                            copy recursively, non-directories as files      ",
            in.readLine());
        assertEquals(
            "  --sparse WHEN                 control creation of sparse files                ",
            in.readLine());
        assertEquals(
            "  -R (--recursive)              copy directories recursively                    ",
            in.readLine());
        assertEquals(
            "  -s (--symbolic-link)          make symbolic links instead of copying          ",
            in.readLine());
        assertEquals(
            "  -S (--suffix) SUFFIX          override the usual backup suffix                ",
            in.readLine());
        assertEquals(
            "  -u (--update)                 copy only when the SOURCE file is newer than    ",
            in.readLine());
        assertEquals(
            "                                the destination file or when the destination    ",
            in.readLine());
        assertEquals(
            "                                file is missing                                 ",
            in.readLine());
        assertEquals(
            "  -v (--verbose)                explain what is being done                      ",
            in.readLine());
        assertEquals(
            "  -V (--version-contol) WORD    explain what is being done                      ",
            in.readLine());
        assertEquals(
            "  -x (--one-file-system)        stay on this file system                        ",
            in.readLine());
        assertEquals(
            "  --help                        display this help and exit                      ",
            in.readLine());
        assertEquals(
            "  --version                     output version information and exit             ",
            in.readLine());
        assertEquals(
            "  SOURCE [SOURCE ...]                                                           ",
            in.readLine());
        assertEquals(
            "  DEST                                                                          ",
            in.readLine());
        assertNull(in.readLine());
    }
}
