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
package org.apache.commons.example.cli;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.cli.help.TextStyle;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

public class AptHelpWriterTest {

    private StringBuilder sb = new StringBuilder();
    private AptHelpWriter underTest = new AptHelpWriter(sb);

    @Test
    public void writeTitleTest() throws IOException {
        sb.setLength(0);
        underTest.writeTitle("Hello World");
        assertEquals(format("        -----%n        Hello World%n        -----%n%nHello World%n%n"), sb.toString());
    }

    @Test
    public void writeParaTest() throws IOException {
        sb.setLength(0);
        underTest.writePara("Hello World");
        assertEquals(format("  Hello World%n%n"), sb.toString());
    }

    @Test
    public void writeHeaderTest() throws IOException {
        sb.setLength(0);
        underTest.writeHeader(1, "Hello World");
        assertEquals(format("* Hello World%n%n"), sb.toString());
        sb.setLength(0);
        underTest.writeHeader(2, "Hello World");
        assertEquals(format("** Hello World%n%n"), sb.toString());
        sb.setLength(0);
        assertThrows(IllegalArgumentException.class, () -> underTest.writeHeader(0, "Hello World"));
    }

    @Test
    public void writeListTest() throws IOException {
        String[] entries = {"one", "two", "three"};
        sb.setLength(0);
        underTest.writeList(true, Arrays.asList(entries));
        assertEquals(format("    [[1]] one%n    [[2]] two%n    [[3]] three%n%n"), sb.toString());

        sb.setLength(0);
        underTest.writeList(false, Arrays.asList(entries));
        assertEquals(format("    * one%n    * two%n    * three%n%n"), sb.toString());
    }

    @Test
    public void writeTableTest() throws IOException {
        List<TextStyle> styles = Arrays.asList(TextStyle.DEFAULT, TextStyle.DEFAULT, TextStyle.DEFAULT);
        String[] headers = {"one", "two", "three"};
        List[] rows = {
                Arrays.asList(new String[]{"uno", "dos", "tres"}),
                Arrays.asList(new String[]{"aon", "dhá", "trí"}),
                Arrays.asList(new String[]{"واحد", "اثنين", "ثلاثة"})
        };

        List<String> expected = new ArrayList<>();
        expected.add("*-----+-----+-------+");
        expected.add("| one | two | three |");
        expected.add("*-----+-----+-------+");
        expected.add("| uno | dos | tres |");
        expected.add("*-----+-----+-------+");
        expected.add("| aon | dhá | trí |");
        expected.add("*-----+-----+-------+");
        expected.add("| واحد | اثنين | ثلاثة |");
        expected.add("*-----+-----+-------+");
        expected.add("The caption");
        expected.add("");

        TableDefinition table = TableDefinition.from("The caption", styles, Arrays.asList(headers), Arrays.asList(rows));
        sb.setLength(0);
        underTest.writeTable(table);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "full table failed");


        table = TableDefinition.from(null, styles, Arrays.asList(headers), Arrays.asList(rows));
        expected.remove(9);
        sb.setLength(0);
        underTest.writeTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        table = TableDefinition.from(null, styles, Arrays.asList(headers), Collections.emptyList());
        expected = new ArrayList<>();
        expected.add("*-----+-----+-------+");
        expected.add("| one | two | three |");
        expected.add("*-----+-----+-------+");
        expected.add("");

        sb.setLength(0);
        underTest.writeTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "no rows test failed");

    }
}
