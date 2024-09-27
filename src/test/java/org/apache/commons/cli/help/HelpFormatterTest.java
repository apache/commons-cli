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
package org.apache.commons.cli.help;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.example.XhtmlSerializer;
import org.junit.jupiter.api.Test;

public class HelpFormatterTest {

    @Test
    public void testPrintOptions() throws IOException {
        StringBuilder sb = new StringBuilder();
        TextSerializer serializer = new TextSerializer(sb);
        HelpFormatter formatter = new HelpFormatter.Builder().setSerializer(serializer).setShowSince(false).build();

        // help format default column styles
        // col  options     description     serializer
        // styl   FIXED     VARIABLE         VARIABLE
        // LPad     0           5               1
        // indent   1           1               3
        //
        // default serializer

        Options options;
        List<String> expected = new ArrayList<>();
        expected.add(" Options           Description       ");
        expected.add(" -a          aaaa aaaa aaaa aaaa aaaa");
        expected.add("");

        options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa");

        formatter.printOptions(options);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        sb.setLength(0);
        serializer.setMaxWidth(30);
        expected = new ArrayList<>();
        expected.add(" Options        Description    ");
        expected.add(" -a          aaaa aaaa aaaa    ");
        expected.add("              aaaa aaaa        ");
        expected.add("");
        formatter.printOptions(options);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(31, actual.get(0).length());
        assertEquals(expected, actual);

        sb.setLength(0);
        serializer.setLeftPad(5);
        expected = new ArrayList<>();
        expected.add("     Options        Description    ");
        expected.add("     -a          aaaa aaaa aaaa    ");
        expected.add("                  aaaa aaaa        ");
        expected.add("");
        formatter.printOptions(options);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);
    }

    @Test
    public void testPrintHelp() throws IOException {
        StringBuilder sb = new StringBuilder();
        TextSerializer serializer = new TextSerializer(sb);
        Options options = new Options().addOption(Option.builder("a").since("1853").hasArg()
                .desc("aaaa aaaa aaaa aaaa aaaa").build());
        HelpFormatter formatter = new HelpFormatter(serializer);

        List<String> expected = new ArrayList<>();
        expected.add(" usage:  commandSyntax [-a <arg>]");
        expected.add("");
        expected.add(" header");
        expected.add("");
        expected.add(" Options      Since           Description       ");
        expected.add(" -a <arg>     1853      aaaa aaaa aaaa aaaa aaaa");
        expected.add("");
        expected.add(" footer");
        expected.add("");

        formatter.printHelp("commandSyntax", "header", options, "footer", true);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        formatter = new HelpFormatter.Builder().setShowSince(false).setSerializer(serializer).build();
        expected = new ArrayList<>();
        expected.add(" usage:  commandSyntax [-a <arg>]");
        expected.add("");
        expected.add(" header");
        expected.add("");
        expected.add(" Options            Description       ");
        expected.add(" -a <arg>     aaaa aaaa aaaa aaaa aaaa");
        expected.add("");
        expected.add(" footer");
        expected.add("");

        sb.setLength(0);
        formatter.printHelp("commandSyntax", "header", options, "footer", true);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);
    }

    @Test
    public void testPrintHelpXML() throws IOException {
        StringBuilder sb = new StringBuilder();
        XhtmlSerializer serializer = new XhtmlSerializer(sb);
        HelpFormatter formatter = new HelpFormatter(serializer);
        formatter.setShowSince(false);
        Options options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa");

        List<String> expected = new ArrayList<>();
        expected.add("<p>usage:  commandSyntax [-a]</p>");
        expected.add("<p>header</p>");
        expected.add("<table class='commons_cli_table'>");
        expected.add("  <tr>");
        expected.add("    <th>Options</th>");
        expected.add("    <th>Description</th>");
        expected.add("  </tr>");
        expected.add("  <tr>");
        expected.add("    <td>-a</td>");
        expected.add("    <td>aaaa aaaa aaaa aaaa aaaa</td>");
        expected.add("  </tr>");
        expected.add("</table>");
        expected.add("<p>footer</p>");

        formatter.printHelp("commandSyntax", "header", options, "footer", true);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));

        assertEquals(expected, actual);
    }
}
