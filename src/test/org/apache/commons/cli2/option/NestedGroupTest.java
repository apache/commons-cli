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
package org.apache.commons.cli2.option;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli2.CLITestCase;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;


/**
 * Test to exercise nested groups developed to demonstrate bug 32533
 */
public class NestedGroupTest extends CLITestCase {
    private static final String[] EXPECTED_USAGE = new String[] {
            "Usage:                                                                          ",
            " [-h -k -e|-d -b|-3 -f <file>|-s <string>]                                      ",
            "encryptionService                                                               ",
            "  -h (--help)               Print this message                                  ",
            "  -k (--key)                Encryption key                                      ",
            "  Action                    Action                                              ",
            "    -e (--encrypt)          Encrypt input                                       ",
            "    -d (--decrypt)          Decrypt input                                       ",
            "  Algorithm                 Encryption Algorithm                                ",
            "    -b (--blowfish)         Blowfish                                            ",
            "    -3 (--3DES)             Triple DES                                          ",
            "  Input                     Input                                               ",
            "    -f (--file) file        Input file                                          ",
            "    -s (--string) string    Input string                                        "
    };

    final static DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
    final static ArgumentBuilder abuilder = new ArgumentBuilder();
    final static GroupBuilder gbuilder = new GroupBuilder();

    static Group buildActionGroup() {
        return gbuilder.withName("Action").withDescription("Action")
                       .withMinimum(1).withMaximum(1)
                       .withOption(obuilder.withId(5).withShortName("e")
                                           .withLongName("encrypt")
                                           .withDescription("Encrypt input")
                                           .create())
                       .withOption(obuilder.withId(6).withShortName("d")
                                           .withLongName("decrypt")
                                           .withDescription("Decrypt input")
                                           .create()).create();
    }

    static Group buildAlgorithmGroup() {
        return gbuilder.withName("Algorithm")
                       .withDescription("Encryption Algorithm").withMaximum(1)
                       .withOption(obuilder.withId(0).withShortName("b")
                                           .withLongName("blowfish")
                                           .withDescription("Blowfish").create())
                       .withOption(obuilder.withId(1).withShortName("3")
                                           .withLongName("3DES")
                                           .withDescription("Triple DES")
                                           .create()).create();
    }

    static Group buildInputGroup() {
        return gbuilder.withName("Input").withDescription("Input").withMinimum(1)
                       .withMaximum(1)
                       .withOption(obuilder.withId(2).withShortName("f")
                                           .withLongName("file")
                                           .withDescription("Input file")
                                           .withArgument(abuilder.withName(
                    "file").withMinimum(1).withMaximum(1).create()).create())
                       .withOption(obuilder.withId(3).withShortName("s")
                                           .withLongName("string")
                                           .withDescription("Input string")
                                           .withArgument(abuilder.withName(
                    "string").withMinimum(1).withMaximum(1).create()).create())
                       .create();
    }

    static Group buildEncryptionServiceGroup(Group[] nestedGroups) {
        gbuilder.withName("encryptionService")
                .withOption(obuilder.withId(4).withShortName("h")
                                    .withLongName("help")
                                    .withDescription("Print this message")
                                    .create()).withOption(obuilder.withShortName(
                "k").withLongName("key").withDescription("Encryption key")
                                                                  .create());

        for (int i = 0; i < nestedGroups.length; i++) {
            gbuilder.withOption(nestedGroups[i]);
        }

        return gbuilder.create();
    }

    public void testNestedGroup()
        throws OptionException {
        final String[] args = {
                "-eb",
                "--file",
                "/tmp/filename.txt"
            };

        Group[] nestedGroups = {
                buildActionGroup(),
                buildAlgorithmGroup(),
                buildInputGroup()
            };

        Parser parser = new Parser();
        parser.setGroup(buildEncryptionServiceGroup(nestedGroups));

        CommandLine commandLine = parser.parse(args);

        assertTrue("/tmp/filename.txt".equals(commandLine.getValue("-f")));
        assertTrue(commandLine.hasOption("-e"));
        assertTrue(commandLine.hasOption("-b"));
        assertFalse(commandLine.hasOption("-d"));
    }

    public void testNestedGroupHelp() {
        checkNestedGroupHelp(new HelpFormatter(), EXPECTED_USAGE);
    }

    public void testNestedGroupHelpOptional()
    {
        HelpFormatter helpFormatter = new HelpFormatter();
        Set dispOptions = new HashSet(helpFormatter.getFullUsageSettings());
        dispOptions.add(DisplaySetting.DISPLAY_OPTIONAL_CHILD_GROUP);
        List expLines = new ArrayList(Arrays.asList(EXPECTED_USAGE));
        expLines.set(1," [-h -k -e|-d [-b|-3] -f <file>|-s <string>]                                    ");
        helpFormatter.setFullUsageSettings(dispOptions);
        checkNestedGroupHelp(helpFormatter, (String[]) expLines
                .toArray(new String[expLines.size()]));
    }

    private void checkNestedGroupHelp(HelpFormatter helpFormatter, String[] expected) {
        Group[] nestedGroups = {
                buildActionGroup(),
                buildAlgorithmGroup(),
                buildInputGroup()
            };
        helpFormatter.setGroup(buildEncryptionServiceGroup(nestedGroups));

        final StringWriter out = new StringWriter();
        helpFormatter.setPrintWriter(new PrintWriter(out));

        try {
            helpFormatter.print();

            final BufferedReader bufferedReader = new BufferedReader(new StringReader(
                        out.toString()));

            List actual = new ArrayList(expected.length);
            String input;

            while ((input = bufferedReader.readLine()) != null) {
                actual.add(input);
            }

            // Show they are the same number of lines
            assertEquals("Help text lines should be " + expected.length,
                actual.size(), expected.length);

            for (int i = 0; i < expected.length; i++) {
                if (!expected[i].equals(actual.get(i))) {
                    for (int x = 0; x < expected.length; i++) {
                        System.out.println("   " + expected[i]);
                        System.out.println((expected[i].equals(actual.get(i))
                            ? "== "
                            : "!= ") + actual.get(i));
                    }
                }

                assertEquals(expected[i], actual.get(i));
            }
        }
        catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
    }
}
