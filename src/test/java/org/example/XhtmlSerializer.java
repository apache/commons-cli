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

import static java.lang.String.format;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.Util;
import org.apache.commons.cli.help.AbstractSerializer;
import org.apache.commons.cli.help.TableDef;

/** An example XML serializer -- DO NOT USE as this does not properly escape strings */
public class XhtmlSerializer extends AbstractSerializer {

    public XhtmlSerializer(final Appendable output) {
        super(output);
    }

    @Override
    public void writeTitle(final String title) throws IOException {
        output.append(format("<span class='commons_cli_title'>%s</span>%n", title));
    }

    @Override
    public void writePara(final String paragraph) throws IOException {
        if (!Util.isEmpty(paragraph)) {
            output.append(format("<p>%s</p>%n", paragraph));
        }
    }

    @Override
    public void writeHeader(final int level, final String text) throws IOException {
        if (!Util.isEmpty(text)) {
            if (level < 1) {
                throw new IllegalArgumentException("level must be at least 1");
            }
            output.append(format("<h%s>%s</h%1$s>%n", level, text));
        }
    }

    @Override
    public void writeList(final boolean ordered, final Collection<String> list) throws IOException {
        output.append(format("<%sl>%n", ordered ? "o" : "u"));
        for (String line : list) {
            output.append(format("  <li>%s</li>%n", Util.defaultValue(line, "")));
        }
        output.append(format("</%sl>%n", ordered ? "o" : "u"));
    }

    @Override
    public void writeTable(final TableDef table) throws IOException {
        output.append(format("<table class='commons_cli_table'>%n"));

        if (!Util.isEmpty(table.caption())) {
            output.append(format("  <caption>%s</caption>%n", table.caption()));
        }

        // write the headers
        if (!table.headers().isEmpty()) {
            output.append(format("  <tr>%n"));
            for (String header : table.headers()) {
                output.append(format("    <th>%s</th>%n", header));
            }
            output.append(format("  </tr>%n"));
        }

        // write the data
        for (List<String> row : table.rows()) {
            output.append(format("  <tr>%n"));
            for (String column : row) {
                output.append(format("    <td>%s</td>%n", column));
            }
            output.append(format("  </tr>%n"));
        }
        output.append(format("</table>%n"));
    }
}
