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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.help.FilterHelpAppendable;
import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

/** An example XML helpAppendable */
public class XhtmlHelpAppendable extends FilterHelpAppendable {

    public XhtmlHelpAppendable(final Appendable output) {
        super(output);
    }

    @Override
    public void appendHeader(final int level, final CharSequence text) throws IOException {
        if (StringUtils.isNotEmpty(text)) {
            if (level < 1) {
                throw new IllegalArgumentException("level must be at least 1");
            }
            output.append(format("<h%s>%s</h%1$s>%n", level, StringEscapeUtils.escapeHtml4(text.toString())));
        }
    }

    @Override
    public void appendList(final boolean ordered, final Collection<CharSequence> list) throws IOException {
        output.append(format("<%sl>%n", ordered ? "o" : "u"));
        for (final CharSequence line : list) {
            output.append(format("  <li>%s</li>%n", StringEscapeUtils.escapeHtml4(StringUtils.defaultIfEmpty(line, "").toString())));
        }
        output.append(format("</%sl>%n", ordered ? "o" : "u"));
    }

    @Override
    public void appendParagraph(final CharSequence paragraph) throws IOException {
        if (StringUtils.isNotEmpty(paragraph)) {
            output.append(format("<p>%s</p>%n", StringEscapeUtils.escapeHtml4(paragraph.toString())));
        }
    }

    @Override
    public void appendTable(final TableDefinition table) throws IOException {
        output.append(format("<table class='commons_cli_table'>%n"));

        if (StringUtils.isNotEmpty(table.caption())) {
            output.append(format("  <caption>%s</caption>%n", StringEscapeUtils.escapeHtml4(table.caption())));
        }

        // write the headers
        if (!table.headers().isEmpty()) {
            output.append(format("  <tr>%n"));
            for (final String header : table.headers()) {
                output.append(format("    <th>%s</th>%n", StringEscapeUtils.escapeHtml4(header)));
            }
            output.append(format("  </tr>%n"));
        }

        // write the data
        for (final List<String> row : table.rows()) {
            output.append(format("  <tr>%n"));
            for (final String column : row) {
                output.append(format("    <td>%s</td>%n", StringEscapeUtils.escapeHtml4(column)));
            }
            output.append(format("  </tr>%n"));
        }
        output.append(format("</table>%n"));
    }

    @Override
    public void appendTitle(final CharSequence title) throws IOException {
        output.append(format("<span class='commons_cli_title'>%s</span>%n", StringEscapeUtils.escapeHtml4(title.toString())));
    }
}
