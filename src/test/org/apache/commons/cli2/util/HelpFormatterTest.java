/**
 * Copyright 2003-2004 The Apache Software Foundation
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
package org.apache.commons.cli2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.option.ArgumentTest;
import org.apache.commons.cli2.option.DefaultOptionTest;

public class HelpFormatterTest extends TestCase {
    private HelpFormatter helpFormatter;
    private Option verbose;
    private Group options;

    public void setUp() {
        helpFormatter = new HelpFormatter("|*", "*-*", "*|", 80);
        helpFormatter.setDivider(
            "+------------------------------------------------------------------------------+");
        helpFormatter.setHeader("Jakarta Commons CLI");
        helpFormatter.setFooter("Copyright 2003\nApache Software Foundation");
        helpFormatter.setShellCommand("ant");

        verbose =
            new DefaultOptionBuilder()
                .withLongName("verbose")
                .withDescription("print the version information and exit")
                .create();

        options = new GroupBuilder()
            .withName("options")
            .withOption(DefaultOptionTest.buildHelpOption())
            .withOption(ArgumentTest.buildTargetsArgument())
            .withOption(
                new DefaultOptionBuilder()
                    .withLongName("diagnostics")
                    .withDescription("print information that might be helpful to diagnose or report problems.")
                    .create())
            .withOption(
                new DefaultOptionBuilder()
                    .withLongName("projecthelp")
                    .withDescription("print project help information")
                    .create())
            .withOption(verbose)
            .create();        
        
        helpFormatter.setGroup(options);
    }

    public void testPrint() throws IOException {
        final StringWriter writer = new StringWriter();
        helpFormatter.setPrintWriter(new PrintWriter(writer));
        helpFormatter.print();

        //System.out.println(writer.toString());

        final BufferedReader reader =
            new BufferedReader(new StringReader(writer.toString()));
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertEquals(
            "|*Jakarta Commons CLI                                                         *|",
            reader.readLine());
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertEquals(
            "|*Usage:                                                                      *|",
            reader.readLine());
        assertEquals(
            "|*ant [--help --diagnostics --projecthelp --verbose] [<target1> [<target2>    *|",
            reader.readLine());
        assertEquals(
            "|*...]]                                                                       *|",
            reader.readLine());
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertEquals(
            "|*options              *-*                                                    *|",
            reader.readLine());
        assertEquals(
            "|*  --help (-?,-h)     *-*Displays the help                                   *|",
            reader.readLine());
        assertEquals(
            "|*  --diagnostics      *-*print information that might be helpful to diagnose *|",
            reader.readLine());
        assertEquals(
            "|*                     *-*or report problems.                                 *|",
            reader.readLine());
        assertEquals(
            "|*  --projecthelp      *-*print project help information                      *|",
            reader.readLine());
        assertEquals(
            "|*  --verbose          *-*print the version information and exit              *|",
            reader.readLine());
        assertEquals(
            "|*  target [target ...]*-*The targets ant should build                        *|",
            reader.readLine());
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertEquals(
            "|*Copyright 2003                                                              *|",
            reader.readLine());
        assertEquals(
            "|*Apache Software Foundation                                                  *|",
            reader.readLine());
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertNull(reader.readLine());
    }

    public void testPrintHelp() throws IOException {
        final StringWriter writer = new StringWriter();
        helpFormatter.setPrintWriter(new PrintWriter(writer));
        helpFormatter.printHelp();
        final BufferedReader reader =
            new BufferedReader(new StringReader(writer.toString()));
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertEquals(
            "|*options              *-*                                                    *|",
            reader.readLine());
        assertEquals(
            "|*  --help (-?,-h)     *-*Displays the help                                   *|",
            reader.readLine());
        assertEquals(
            "|*  --diagnostics      *-*print information that might be helpful to diagnose *|",
            reader.readLine());
        assertEquals(
            "|*                     *-*or report problems.                                 *|",
            reader.readLine());
        assertEquals(
            "|*  --projecthelp      *-*print project help information                      *|",
            reader.readLine());
        assertEquals(
            "|*  --verbose          *-*print the version information and exit              *|",
            reader.readLine());
        assertEquals(
            "|*  target [target ...]*-*The targets ant should build                        *|",
            reader.readLine());
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertNull(reader.readLine());
    }

    public void testPrintHelp_WithException() throws IOException {
        final StringWriter writer = new StringWriter();
        helpFormatter.setPrintWriter(new PrintWriter(writer));
        helpFormatter.setException(new OptionException(verbose));
        helpFormatter.printHelp();
        //System.out.println(writer);
        final BufferedReader reader =
            new BufferedReader(new StringReader(writer.toString()));
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertEquals(
            "|*--verbose*-*print the version information and exit                          *|",
            reader.readLine());
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertNull(reader.readLine());
    }

    public void testPrintHelp_TooNarrow() throws IOException {
        final StringWriter writer = new StringWriter();
        helpFormatter = new HelpFormatter("<","=",">",4);
        helpFormatter.setGroup(options);
        helpFormatter.setPrintWriter(new PrintWriter(writer));
        helpFormatter.printHelp();
        final BufferedReader reader =
            new BufferedReader(new StringReader(writer.toString()));
        assertEquals(
            "<options              = >",
            reader.readLine());
        assertEquals(
            "<  --help (-?,-h)     =D>",
            reader.readLine());
        assertEquals(
            "<                     =i>",
            reader.readLine());
        // lots more lines unchecked
    }

    public void testPrintException() throws IOException {
        final StringWriter writer = new StringWriter();
        helpFormatter.setPrintWriter(new PrintWriter(writer));
        helpFormatter.setException(new OptionException(verbose, "cli.error.missing.option"));
        helpFormatter.printException();
        //System.out.println(writer);
        final BufferedReader reader =
            new BufferedReader(new StringReader(writer.toString()));
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertEquals(
            "|*Missing option --verbose                                                    *|",
            reader.readLine());
        assertNull(reader.readLine());
    }

    public void testPrintUsage() throws IOException {
        final StringWriter writer = new StringWriter();
        helpFormatter.setPrintWriter(new PrintWriter(writer));
        helpFormatter.printUsage();
        final BufferedReader reader =
            new BufferedReader(new StringReader(writer.toString()));
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertEquals(
            "|*Usage:                                                                      *|",
            reader.readLine());
        assertEquals(
            "|*ant [--help --diagnostics --projecthelp --verbose] [<target1> [<target2>    *|",
            reader.readLine());
        assertEquals(
            "|*...]]                                                                       *|",
            reader.readLine());
        assertNull(reader.readLine());
    }

    public void testPrintHeader() throws IOException {
        final StringWriter writer = new StringWriter();
        helpFormatter.setPrintWriter(new PrintWriter(writer));
        helpFormatter.printHeader();
        final BufferedReader reader =
            new BufferedReader(new StringReader(writer.toString()));
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertEquals(
            "|*Jakarta Commons CLI                                                         *|",
            reader.readLine());
        assertNull(reader.readLine());
    }

    public void testPrintFooter() throws IOException {
        final StringWriter writer = new StringWriter();
        helpFormatter.setPrintWriter(new PrintWriter(writer));
        helpFormatter.printFooter();
        final BufferedReader reader =
            new BufferedReader(new StringReader(writer.toString()));
        assertEquals(
            "|*Copyright 2003                                                              *|",
            reader.readLine());
        assertEquals(
            "|*Apache Software Foundation                                                  *|",
            reader.readLine());
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertNull(reader.readLine());
    }

    public void testPrintDivider() throws IOException {
        final StringWriter writer = new StringWriter();
        helpFormatter.setPrintWriter(new PrintWriter(writer));
        helpFormatter.printDivider();
        final BufferedReader reader =
            new BufferedReader(new StringReader(writer.toString()));
        assertEquals(
            "+------------------------------------------------------------------------------+",
            reader.readLine());
        assertNull(reader.readLine());
    }

    public void testWrap() {
        final Iterator i =
            HelpFormatter.wrap("Apache Software Foundation", 30).iterator();
        assertEquals("Apache Software Foundation", i.next());
        assertFalse(i.hasNext());
    }

    public void testWrap_WrapNeeded() {
        final Iterator i =
            HelpFormatter.wrap("Apache Software Foundation", 20).iterator();
        assertEquals("Apache Software", i.next());
        assertEquals("Foundation", i.next());
        assertFalse(i.hasNext());
    }

    public void testWrap_BeforeSpace() {
        final Iterator i =
            HelpFormatter.wrap("Apache Software Foundation", 16).iterator();
        assertEquals("Apache Software", i.next());
        assertEquals("Foundation", i.next());
        assertFalse(i.hasNext());
    }

    public void testWrap_AfterSpace() {
        final Iterator i =
            HelpFormatter.wrap("Apache Software Foundation", 17).iterator();
        assertEquals("Apache Software", i.next());
        assertEquals("Foundation", i.next());
        assertFalse(i.hasNext());
    }

    public void testWrap_InWord() {
        final Iterator i =
            HelpFormatter.wrap("Apache Software Foundation", 8).iterator();
        assertEquals("Apache", i.next());
        assertEquals("Software", i.next());
        assertEquals("Foundati", i.next());
        assertEquals("on", i.next());
        assertFalse(i.hasNext());
    }

    public void testWrap_NewLine() {
        final Iterator i =
            HelpFormatter.wrap("\nApache Software Foundation\n", 30).iterator();
        assertEquals("", i.next());
        assertEquals("Apache Software Foundation", i.next());
        assertEquals("", i.next());
        assertFalse(i.hasNext());
    }

    public void testWrap_NewLine2() {
        List wrapped = HelpFormatter.wrap("A really quite long general description of the option with specific alternatives documented:\n"+
                    "  Indented special case\n"+
                    "  Alternative scenario", 30);
        
        final Iterator i =
            wrapped.iterator();
        
        assertEquals("A really quite long general", i.next());
        assertEquals("description of the option", i.next());
        assertEquals("with specific alternatives", i.next());
        assertEquals("documented:", i.next());
        assertEquals("  Indented special case", i.next());
        assertEquals("  Alternative scenario", i.next());
        assertFalse(i.hasNext());
    }
    
    public void testWrap_Below1Length() {
        try{
            HelpFormatter.wrap("Apache Software Foundation",-1);
            fail("IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            assertEquals("width must be positive",e.getMessage());
        }
    }

    public void testPad() throws IOException {
        final StringWriter writer = new StringWriter();
        HelpFormatter.pad("hello", 10, writer);
        assertEquals("hello     ", writer.toString());
    }

    public void testPad_Null() throws IOException {
        final StringWriter writer = new StringWriter();
        HelpFormatter.pad(null, 10, writer);
        assertEquals("          ", writer.toString());
    }

    public void testPad_TooLong() throws IOException {
        final StringWriter writer = new StringWriter();
        HelpFormatter.pad("hello world", 10, writer);
        assertEquals("hello world", writer.toString());
    }
    
    public void testPad_TooShort() throws IOException {
        final StringWriter writer = new StringWriter();
        HelpFormatter.pad("hello world", -5, writer);
        assertEquals("hello world", writer.toString());
    }
}
