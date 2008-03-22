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
package org.apache.commons.cli2.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.util.HelpFormatter;

import junit.framework.TestCase;

public class ParserTest extends TestCase {
    
    private Parser parser;
    private DefaultOption verboseOption;
    private DefaultOption helpOption;
    private Group options;
    private HelpFormatter helpFormatter;
    private StringWriter out;
    private BufferedReader in;

    public void setUp() {
        parser = new Parser();
        
        final GroupBuilder gBuilder = new GroupBuilder();
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        
        helpOption = oBuilder.withLongName("help").withShortName("h").create();
        verboseOption = oBuilder.withLongName("verbose").withShortName("v").create();
        options = gBuilder.withOption(helpOption).withOption(verboseOption).create();
        parser.setGroup(options);
        
        helpFormatter = new HelpFormatter();
        out = new StringWriter();
        helpFormatter.setPrintWriter(new PrintWriter(out));
        parser.setHelpFormatter(helpFormatter);
    }

    public void testParse_Successful() throws OptionException {
        final CommandLine cl = parser.parse(new String[]{"-hv"});
        
        assertTrue(cl.hasOption(helpOption));
        assertTrue(cl.hasOption(verboseOption));
        
        assertEquals("--help --verbose",cl.toString());
        
        final WriteableCommandLineImpl wcli = (WriteableCommandLineImpl)cl;
        assertEquals("[--help, --verbose]",wcli.getNormalised().toString());
    }

    public void testParse_WithUnexpectedOption() {
        try {
            parser.parse(new String[]{"--unexpected"});
            fail("OptionException");
        }
        catch(OptionException e) {
            assertEquals(options,e.getOption());
            assertEquals("Unexpected --unexpected while processing --help|--verbose",e.getMessage());
        }
    }

    public void testParseAndHelp_Successful() throws IOException {
        final CommandLine cl = parser.parseAndHelp(new String[]{"-v"});
        
        assertTrue(cl.hasOption(verboseOption));
        assertEquals("",out.getBuffer().toString());
    }

    public void testParseAndHelp_ByHelpOption() throws IOException {
        parser.setHelpOption(helpOption);
        
        assertNull(parser.parseAndHelp(new String[]{"-hv"}));
        
        inReader();
        assertInReaderUsage();
        assertInReaderEOF();
    }

    public void testParseAndHelp_ByHelpTrigger() throws IOException {
        parser.setHelpTrigger("--help");
        
        assertNull(parser.parseAndHelp(new String[]{"-hv"}));
        
        inReader();
        assertInReaderUsage();
        assertInReaderEOF();
    }

    public void testParseAndHelp_WithUnexpectedOption() throws IOException {
        assertNull(parser.parseAndHelp(new String[]{"--unexpected"}));
        
        inReader();
        assertInReaderLine("Unexpected --unexpected while processing --help|--verbose");
        assertInReaderUsage();
        assertInReaderEOF();
    }

    private void assertInReaderUsage() throws IOException {
        assertInReaderLine("Usage:");
        assertInReaderLine("[--help --verbose]");
        assertInReaderLine("--help|--verbose");
        assertInReaderLine("--help (-h)");
        assertInReaderLine("--verbose (-v)");
    }

    private void assertInReaderLine(final String string) throws IOException {
        assertEquals(string,in.readLine().trim());
    }

    private void assertInReaderEOF() throws IOException {
        assertNull(in.readLine());
    }

    private void inReader() {
        in = new BufferedReader(new StringReader(out.getBuffer().toString()));
    }
}
