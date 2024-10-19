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
package org.apache.commons.cli.example;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.cli.help.FilterHelpAppendable;
import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Appends XHTML formatted text to an {@link Appendable}.
 */
public class XhtmlHelpAppendable extends FilterHelpAppendable {

    /**
     * Constructs an appendable filter built on top of the specified underlying appendable.
     *
     * @param output the underlying appendable to be assigned to the field {@code this.output} for later use, or {@code null} if this instance is to be created
     *               without an underlying stream.
     */
    public XhtmlHelpAppendable(final Appendable output) {
        super(output);
    }

    @Override
    public void appendHeader(final int level, final CharSequence text) throws IOException {
        if (StringUtils.isNotEmpty(text)) {
            if (level < 1) {
                throw new IllegalArgumentException("level must be at least 1");
            }
            appendFormat("<h%s>%s</h%1$s>%n", level, StringEscapeUtils.escapeHtml4(Objects.toString(text)));
        }
    }

    @Override
    public void appendList(final boolean ordered, final Collection<CharSequence> list) throws IOException {
        if (list != null) {
            appendFormat("<%sl>%n", ordered ? "o" : "u");
            for (final CharSequence line : list) {
                appendFormat("  <li>%s</li>%n", StringEscapeUtils.escapeHtml4(StringUtils.defaultIfEmpty(line, "").toString()));
            }
            appendFormat("</%sl>%n", ordered ? "o" : "u");
        }
    }

    @Override
    public void appendParagraph(final CharSequence paragraph) throws IOException {
        if (StringUtils.isNotEmpty(paragraph)) {
            appendFormat("<p>%s</p>%n", StringEscapeUtils.escapeHtml4(Objects.toString(paragraph)));
        }
    }

    @Override
    public void appendTable(final TableDefinition table) throws IOException {
        if (table != null) {
            appendFormat("<table class='commons_cli_table'>%n");
            if (StringUtils.isNotEmpty(table.caption())) {
                appendFormat("  <caption>%s</caption>%n", StringEscapeUtils.escapeHtml4(table.caption()));
            }
            // write the headers
            if (!table.headers().isEmpty()) {
                appendFormat("  <tr>%n");
                for (final String header : table.headers()) {
                    appendFormat("    <th>%s</th>%n", StringEscapeUtils.escapeHtml4(header));
                }
                appendFormat("  </tr>%n");
            }
            // write the data
            for (final List<String> row : table.rows()) {
                appendFormat("  <tr>%n");
                for (final String column : row) {
                    appendFormat("    <td>%s</td>%n", StringEscapeUtils.escapeHtml4(column));
                }
                appendFormat("  </tr>%n");
            }
            appendFormat("</table>%n");
        }
    }

    @Override
    public void appendTitle(final CharSequence title) throws IOException {
        if (StringUtils.isNotEmpty(title)) {
            appendFormat("<span class='commons_cli_title'>%s</span>%n", StringEscapeUtils.escapeHtml4(Objects.toString(title)));
        }
    }
}
