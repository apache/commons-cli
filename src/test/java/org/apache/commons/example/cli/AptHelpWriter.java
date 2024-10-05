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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.Util;
import org.apache.commons.cli.help.AbstractHelpWriter;
import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.cli.help.TextStyle;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;

/**
 * A class to write APT formatted text.
 */
public class AptHelpWriter extends AbstractHelpWriter {

    /**
     * Translator object for escaping APT codes
     */
    public static final CharSequenceTranslator ESCAPE_APT;

    static {
        final Map<CharSequence, CharSequence> escapeAptMap = new HashMap<>();
        escapeAptMap.put("\\", "\\\\");
        escapeAptMap.put("\"", "\\\"");
        escapeAptMap.put("*", "\\*");
        escapeAptMap.put("+", "\\+");
        escapeAptMap.put("|", "\\|");
        ESCAPE_APT = new LookupTranslator(escapeAptMap);
    }

    public AptHelpWriter(final Appendable output) {
        super(output);
    }

    @Override
    public void writeTitle(final CharSequence title) throws IOException {
        if (!Util.isEmpty(title)) {
            output.append(format("        -----%n        %1$s%n        -----%n%n%1$s%n%n", title));
        }
    }

    @Override
    public void writePara(final CharSequence paragraph) throws IOException {
        if (!Util.isEmpty(paragraph)) {
            output.append(format("  %s%n%n", ESCAPE_APT.translate(paragraph)));
        }
    }

    @Override
    public void writeHeader(final int level, final CharSequence text) throws IOException {
        if (!Util.isEmpty(text)) {
            if (level < 1) {
                throw new IllegalArgumentException("level must be at least 1");
            }
            for (int i = 0; i < level; i++) {
                output.append("*");
            }
            output.append(format(" %s%n%n", ESCAPE_APT.translate(text)));
        }
    }

    @Override
    public void writeList(final boolean ordered, final Collection<CharSequence> list) throws IOException {
        if (null != list) {
            if (ordered) {
                int idx = 1;
                for (CharSequence s : list) {
                    output.append(format("    [[%s]] %s%n", idx++, ESCAPE_APT.translate(s)));
                }
            } else {
                for (CharSequence s : list) {
                    output.append(format("    * %s%n", ESCAPE_APT.translate(s)));
                }
            }
            output.append(System.lineSeparator());
        }
    }

    @Override
    public void writeTable(final TableDefinition table) throws IOException {
        if (table != null) {
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
            }
            ;
            String rowSeparator = System.lineSeparator() + sb.append(System.lineSeparator()).toString();

            // output the header line.
            output.append(sb.toString());
            output.append("|");
            for (String header : table.headers()) {
                output.append(format(" %s |", ESCAPE_APT.translate(header)));
            }
            output.append(rowSeparator);

            // write the table entries
            for (Collection<String> row : table.rows()) {
                output.append("|");
                for (String cell : row) {
                    output.append(format(" %s |", ESCAPE_APT.translate(cell)));
                }
                output.append(rowSeparator);
            }

            // write the caption
            if (!Util.isEmpty(table.caption())) {
                output.append(format("%s%n", ESCAPE_APT.translate(table.caption())));
            }

            output.append(System.lineSeparator());
        }
    }
}
