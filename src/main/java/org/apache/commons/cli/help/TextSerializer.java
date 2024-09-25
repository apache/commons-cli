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

import org.apache.commons.cli.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.String.format;

public class TextSerializer extends AbstractSerializer {

    /** Default number of characters per line */
    public static final int DEFAULT_WIDTH = 74;

    /** Default padding to the left of each line */
    public static final int DEFAULT_LEFT_PAD = 1;

    /** Number of space characters to be prefixed to each description line */
    public static final int DEFAULT_INDENT = 3;

    private TextStyle.Builder styleBuilder;

    public TextSerializer(Appendable output) {
        super(output);
        styleBuilder = new TextStyle.Builder().setMaxWidth(DEFAULT_WIDTH)
                .setLeftPad(DEFAULT_LEFT_PAD).setIndent(DEFAULT_INDENT);
    }

    public void setMaxWidth(int maxWidth) {
        styleBuilder.setMaxWidth(maxWidth);
    }

    public void setLeftPad(int leftPad) {
        styleBuilder.setLeftPad(leftPad);
    }

    public void setIndent(int indent) {
        styleBuilder.setIndent(indent);
    }

    public TextStyle.Builder getStyleBuilder() {
        return styleBuilder;
    }

    private void printQueue(Queue<String> queue) throws IOException {
        for (String s : queue) {
            output.append(format("%s%n",Util.rtrim(s)));
        }
    }

    @Override
    public void writeTitle(final String title) throws IOException {
        if (!Util.isEmpty(title)) {
            TextStyle style = styleBuilder.get();
            Queue<String> queue = makeColumnQueue(title, style);
            queue.add(Util.createPadding(style.leftPad)+Util.filledString(Math.min(title.length(), style.getMaxWidth()),'#'));
            queue.add("");
            printQueue(queue);
        }
    }

    @Override
    public void writePara(final String paragraph) throws IOException {
        if (!Util.isEmpty(paragraph)) {
            Queue<String> queue = makeColumnQueue(paragraph, styleBuilder.get());
            queue.add("");
            printQueue(queue);
        }
    }

    @Override
    public void writeHeader(final int level, final String text) throws IOException {
        if (!Util.isEmpty(text)) {
            if (level < 1) {
                throw new IllegalArgumentException("level must be at least 1");
            }
            char[] fillChars = {'=', '%', '+', '_'};
            int idx = (Math.min(level, fillChars.length)) - 1;
            TextStyle style = styleBuilder.get();
            Queue<String> queue = makeColumnQueue(text, style);
            queue.add(Util.createPadding(style.leftPad)+Util.filledString(Math.min(text.length(), style.getMaxWidth()), fillChars[idx]));
            queue.add("");
            printQueue(queue);
        }
    }

    @Override
    public void writeList(boolean ordered, Collection<String> list) throws IOException {
        if (list != null && !list.isEmpty()) {
            int maxWidth = 0;
            TextStyle.Builder builder = new TextStyle.Builder().setLeftPad(styleBuilder.getLeftPad()).setIndent(7);
            int i = 1;
            for (String line : list) {
                String entry = ordered ? format(" %s. %s", i++, Util.defaultValue(line, "")) :
                        format(" * %s", Util.defaultValue(line, ""));
                builder.setMaxWidth(Math.min(styleBuilder.getMaxWidth(), entry.length()));
                printQueue(makeColumnQueue(entry, builder.get()));
            }
            output.append(System.lineSeparator());
        }
    }

    private int resize(int orig, double fraction) {
        return (int) (orig * fraction);
    }

    public TextStyle.Builder resize(TextStyle.Builder builder, double fraction) {
        double indentFrac = builder.getIndent() * 1.0 / builder.getMaxWidth();
        builder.setMaxWidth(Math.max(resize(builder.getMaxWidth(), fraction), builder.getMinWidth()));
        int maxAdjust = builder.getMaxWidth() / 3;
        int newIndent = builder.getMaxWidth() == 1 ? 0 : builder.getIndent();
        if (newIndent > maxAdjust) {
            newIndent = Math.min(resize(builder.getIndent(), indentFrac), Math.min(maxAdjust, builder.getMinWidth()));
        }
        builder.setIndent(newIndent);
        return builder;
    }


    private TableDef adjustTableFormat(TableDef table) {
        List<TextStyle.Builder> styleBuilders = new ArrayList<>();
        for (int i = 0; i < table.columnStyle().size(); i++) {
            TextStyle style = table.columnStyle().get(i);
            TextStyle.Builder builder = new TextStyle.Builder(style);
            styleBuilders.add(builder);
            String header = table.headers().get(i);

            if (style.getMaxWidth() < header.length() || style.getMaxWidth() == TextStyle.UNSET) {
                builder.setMaxWidth(header.length());
            }
            if (style.getMinWidth() < header.length()) {
                builder.setMinWidth(header.length());
            }
            for (List<String> row : table.rows()) {
                String cell = row.get(i);
                if (cell.length() > builder.getMaxWidth()) {
                    builder.setMaxWidth(cell.length());
                }
            }
        }

        // calculate the total width.
        int calcWidth = 0;
        int adjustedMaxWidth = styleBuilder.getMaxWidth();
        for (TextStyle.Builder builder : styleBuilders) {
            adjustedMaxWidth -= builder.getLeftPad();
            if (builder.getScaling() == TextStyle.Scaling.VARIABLE) {
                calcWidth += builder.getMaxWidth();
            } else {
                adjustedMaxWidth -= builder.getMaxWidth();
            }
        }

        // rescale if necessary
        if (calcWidth > adjustedMaxWidth) {
            double fraction = adjustedMaxWidth * 1.0 / calcWidth;
            for (int i = 0; i < styleBuilders.size(); i++) {
                TextStyle.Builder builder = styleBuilders.get(i);
                if (builder.getScaling() == TextStyle.Scaling.VARIABLE) {
                    // resize and remove the padding from the maxWidth calculation.
                    styleBuilders.set(i, resize(builder, fraction));
                }
            }
        }
        // regenerate the styles
        List<TextStyle> styles = new ArrayList<>();
        for (TextStyle.Builder builder : styleBuilders) {
            // adjust by removing the padding as it was not accounted for above.
            styles.add(builder.get());
        }

        return TableDef.from(table.caption(), styles, table.headers(), table.rows());
    }

    @Override
    public void writeTable(TableDef rawTable) throws IOException {
        TableDef table = adjustTableFormat(rawTable);
        // write the table
        writePara(table.caption());

        List<TextStyle> headerStyles = new ArrayList<>();
        for (TextStyle style : table.columnStyle()) {
            headerStyles.add(new TextStyle.Builder(style).setAlignment(TextStyle.Alignment.CENTER).get());
        }
        writeColumnQueues(makeColumnQueues(table.headers(), headerStyles), headerStyles);
        for (List<String> row : table.rows()) {
            writeColumnQueues(makeColumnQueues(row, table.columnStyle()), table.columnStyle());
        }

        output.append(System.lineSeparator());
    }

    protected void writeColumnQueues(List<Queue<String>> columnQueues, List<TextStyle> styles) throws IOException {
        boolean moreData = true;
        String lPad = Util.createPadding(styleBuilder.get().leftPad);
        while (moreData) {
            output.append(lPad);
            moreData = false;
            for (int i = 0; i < columnQueues.size(); i++) {
                TextStyle style = styles.get(i);
                Queue<String> columnQueue = columnQueues.get(i);
                String line = columnQueue.poll();
                if (Util.isEmpty(line)) {
                    output.append(Util.createPadding(style.getMaxWidth() + style.getLeftPad()));
                } else {
                    output.append(line);
                }
                moreData |= !columnQueue.isEmpty();
            }
            output.append(System.lineSeparator());
        }
    }

    protected List<Queue<String>> makeColumnQueues(List<String> columnData, List<TextStyle> styles) {
        List<Queue<String>> result = new ArrayList<>();
        for (int i = 0; i < columnData.size(); i++) {
            result.add(makeColumnQueue(columnData.get(i), styles.get(i)));
        }
        return result;
    }

    /**
     * Creates a queue comprising strings extracted from columnData where the alignment and length are determined
     * by ColumnDef.
     * @param columnData The string to wrap
     * @param style The column definition to guide the wrapping.
     * @return A queue of the string wrapped.
     */
    protected Queue<String> makeColumnQueue(String columnData, TextStyle style) {
        String lpad = Util.createPadding(style.getLeftPad());
        String indent = Util.createPadding(style.getIndent());
        Queue<String> result = new LinkedList<>();
        int wrapPos = 0;
        int nextPos = 0;
        int wrappedMaxWidth = style.getMaxWidth() - indent.length();
        while (wrapPos < columnData.length()) {
            int workingWidth = wrapPos == 0 ? style.getMaxWidth() : wrappedMaxWidth;
            nextPos = Util.findWrapPos(columnData, workingWidth, wrapPos);
            String working = columnData.substring(wrapPos, nextPos);
            String rest = Util.createPadding(workingWidth - working.length());
            StringBuilder sb = new StringBuilder(lpad);
            sb.append(style.pad(wrapPos > 0, working));
            result.add(sb.toString());
            wrapPos = Util.findNonWhitespacePos(columnData, nextPos);
            wrapPos = wrapPos == -1 ? nextPos : wrapPos;
        }
        return result;
    }

    public void printWrapped(String text, TextStyle style) throws IOException {
        printQueue(makeColumnQueue(text, style));
    }
}
