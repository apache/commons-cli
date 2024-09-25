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
package org.example;

import org.apache.commons.cli.Util;
import org.apache.commons.cli.help.AbstractSerializer;
import org.apache.commons.cli.help.TextStyle;
import org.apache.commons.cli.help.TableDef;

import java.io.IOException;
import java.util.Collection;

import static java.lang.String.format;


/**
 * A class to write APT formatted text.
 */
public class AptSerializer extends AbstractSerializer {

    public AptSerializer(Appendable output) {
        super(output);
    }

    @Override
    public void writeTitle(final String title) throws IOException {
        output.append(format("        -----%n        %1$s%n        -----%n%n%1$s%n%n", title));
    }

    @Override
    public void writePara(final String paragraph) throws IOException {
        output.append(format("  %s%n%n", paragraph));
    }

    @Override
    public void writeHeader(final int level, final String text) throws IOException {
        output.append(System.lineSeparator());
        for (int i = 0; i < level; i++) {
            output.append("*");
        }
        output.append(format(" %s%n%n", text));
    }

    @Override
    public void writeList(final boolean ordered, final Collection<String> list) throws IOException {
        for (String s : list) {
            output.append(format("    * %s%n", s));
        }
        output.append(System.lineSeparator());
    }

    @Override
    public void writeTable(TableDef table) throws IOException {
        // create the row separator string
        StringBuilder sb = new StringBuilder("*");
        for (int i = 0; i < table.headers().size(); i++) {
            String header = table.headers().get(i);
            TextStyle style = table.columnStyle().get(i);
            sb.append(Util.filledString(header.length() + 2, '-'));
            switch (style.getAlignment()) {
                case LEFT:
                    sb.append("+");
                    break;
                case CENTER:
                    sb.append("*");
                    break;
                case RIGHT:
                    sb.append(":");
                    break;
            }
        };
        String rowSeparator = System.lineSeparator() + sb.append(System.lineSeparator()).toString();

        // output the header line.
        output.append(sb.toString());
        output.append("| ");
        for (String header : table.headers()) {
            output.append(format("%s |", header));
            output.append(rowSeparator);
        }

        // write the table entries
        output.append("| ");
        for (Collection<String> row : table.rows()) {
            for (String cell : row) {
                output.append(format("%s |", cell));
            }
            output.append(rowSeparator);
        }

        // write the caption
        if (!Util.isEmpty(table.caption())) {
            output.append(format("%s%n", table.caption()));
        }
    }
}
