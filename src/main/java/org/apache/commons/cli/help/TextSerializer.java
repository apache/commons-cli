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

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.cli.Util;

/**
 * Writes text format output.
 */
public class TextSerializer extends AbstractSerializer {

    /** Default number of characters per line */
    public static final int DEFAULT_WIDTH = 74;

    /** Default padding to the left of each line */
    public static final int DEFAULT_LEFT_PAD = 1;

    /** Number of space characters to be prefixed to each description line */
    public static final int DEFAULT_INDENT = 3;

    /** Number of space characters before a list continuation line */
    public static final int DEFAULT_LIST_INDENT = 7;

    /** Defines the TextStyle for paragraph, and associated output formats. */
    private final TextStyle.Builder styleBuilder;

    /**
     * Construct from an output.
     * @param output the Appendable to write the output to.
     */
    public TextSerializer(final Appendable output) {
        super(output);
        styleBuilder = new TextStyle.Builder().setMaxWidth(DEFAULT_WIDTH)
                .setLeftPad(DEFAULT_LEFT_PAD).setIndent(DEFAULT_INDENT);
    }

    /**
     * Sets the maximum width for the output.
     * @param maxWidth the maximum width for the output.
     */
    public void setMaxWidth(final int maxWidth) {
        styleBuilder.setMaxWidth(maxWidth);
    }

    /**
     * Sets the left padding: the number of characters from the left edge to start output.
     * @param leftPad the left padding.
     */
    public void setLeftPad(final int leftPad) {
        styleBuilder.setLeftPad(leftPad);
    }

    /**
     * Sets the indent for paragraphs.
     * @param indent the indent used for paragraphs.
     */
    public void setIndent(final int indent) {
        styleBuilder.setIndent(indent);
    }

    /**
     * Gets the style builder used to format text that is not otherwise formatted.
     * @return The style builder used to format text that is not otherwise formatted.
     */
    public TextStyle.Builder getStyleBuilder() {
        return styleBuilder;
    }

    /**
     * Print a queue of text.
     * @param queue the queue of text to print.
     * @throws IOException on output error.
     */
    private void printQueue(final Queue<String> queue) throws IOException {
        for (String s : queue) {
            output.append(format("%s%n", Util.rtrim(s)));
        }
    }

    @Override
    public void writeTitle(final String title) throws IOException {
        if (!Util.isEmpty(title)) {
            TextStyle style = styleBuilder.get();
            Queue<String> queue = makeColumnQueue(title, style);
            queue.add(Util.createPadding(style.getLeftPad()) + Util.filledString(Math.min(title.length(), style.getMaxWidth()), '#'));
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
            queue.add(Util.createPadding(style.getLeftPad()) + Util.filledString(Math.min(text.length(), style.getMaxWidth()), fillChars[idx]));
            queue.add("");
            printQueue(queue);
        }
    }

    @Override
    public void writeList(final boolean ordered, final Collection<String> list) throws IOException {
        if (list != null && !list.isEmpty()) {
            int maxWidth = 0;
            TextStyle.Builder builder = new TextStyle.Builder().setLeftPad(styleBuilder.getLeftPad()).setIndent(DEFAULT_LIST_INDENT);
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

    /**
     * Resizes an original width based on the fractional size it should be.
     * @param orig the original size.
     * @param fraction the fractional adjustment.
     * @return the resized value.
     */
    private int resize(final int orig, final double fraction) {
        return (int) (orig * fraction);
    }

    /**
     * Resize a TextBuilder based on the fractional size.
     * @param builder the builder to adjust.
     * @param fraction the fractional size (e.g. percentage of the current size) that the builder should be.
     * @return the builder with the maximum width and indent values resized.
     */
    protected TextStyle.Builder resize(final TextStyle.Builder builder, final double fraction) {
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

    /**
     * Adjust the table format.
     * <p>
     *     Given the width of the page and the size of the table attempt to resize the columns to fit the page width
     *     if necessary.  Adjustments are made as follows:
     * </p>
     * <ul>
     *     <li>The minimum size for a column may not be smaller than the length of the column header</li>
     *     <li>The maximum size is set to the maximum of the length of the header or the longest line length.</li>
     *     <li>If the total size of the columns is greater than the page wight, adjust the size of VARIABLE columns
     *     to attempt reduce the width to the the maximum size.
     * </ul>
     * <p>Note: it is possible for the size of the columns to exceed the declared page width.  In this case the table
     * will extend beyond the desired page width.</p>
     * @param table the table to adjust.
     * @return a new TableDef with adjusted values.
     */
    protected TableDef adjustTableFormat(final TableDef table) {
        List<TextStyle.Builder> styleBuilders = new ArrayList<>();
        for (int i = 0; i < table.columnStyle().size(); i++) {
            TextStyle style = table.columnStyle().get(i);
            TextStyle.Builder builder = new TextStyle.Builder(style);
            styleBuilders.add(builder);
            String header = table.headers().get(i);

            if (style.getMaxWidth() < header.length() || style.getMaxWidth() == TextStyle.UNSET_MAX_WIDTH) {
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
    public void writeTable(final TableDef rawTable) throws IOException {
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

    /**
     * Write one line from each of the {@code columnQueues} until all the queues are exhausted.
     * If an exhausted queue is encountered while other queues continue to have content the exhausted queue will
     * produce empty text for the output width of the column (maximum width + left pad).
     * @param columnQueues the List of queues that represent the columns of data.
     * @param styles the TextStyle for each column.
     * @throws IOException on output error.
     */
    protected void writeColumnQueues(final List<Queue<String>> columnQueues, final List<TextStyle> styles) throws IOException {
        boolean moreData = true;
        String lPad = Util.createPadding(styleBuilder.get().getLeftPad());
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

    /**
     * For each column in the {@code columnData} apply the associated {@link TextStyle} and generated a queue of strings
     * that are the maximum size of the column + the left pad.
     * @param columnData The column data to output.
     * @param styles the styles to apply.
     * @return A list of queues of strings that represent each column in the table.
     */
    protected List<Queue<String>> makeColumnQueues(final List<String> columnData, final List<TextStyle> styles) {
        List<Queue<String>> result = new ArrayList<>();
        for (int i = 0; i < columnData.size(); i++) {
            result.add(makeColumnQueue(columnData.get(i), styles.get(i)));
        }
        return result;
    }

    /**
     * Creates a queue comprising strings extracted from columnData where the alignment and length are determined
     * by the style.
     * @param columnData The string to wrap
     * @param style The TextStyle to guide the wrapping.
     * @return A queue of the string wrapped.
     */
    protected Queue<String> makeColumnQueue(final String columnData, final TextStyle style) {
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

    /**
     * Print wrapped text.
     * @param text the text to wrap
     * @param style the style for the wrapped text.
     * @throws IOException on output error.
     */
    public void printWrapped(final String text, final TextStyle style) throws IOException {
        printQueue(makeColumnQueue(text, style));
    }
}
