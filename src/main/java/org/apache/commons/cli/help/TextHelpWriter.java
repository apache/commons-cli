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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Writes text format output.
 * @since 1.10.0
 */
public class TextHelpWriter extends AbstractHelpWriter {

    /** Default number of characters per line */
    public static final int DEFAULT_WIDTH = 74;

    /** Default padding to the left of each line */
    public static final int DEFAULT_LEFT_PAD = 1;

    /** Number of space characters to be prefixed to each description line */
    public static final int DEFAULT_INDENT = 3;

    /** Number of space characters before a list continuation line */
    public static final int DEFAULT_LIST_INDENT = 7;

    /** A blank line in the output */
    private static final String BLANK_LINE = "";

    /** An array of chars that are breaks in text */
    private static final char[] BREAK_CHARS = {'\t', '\n', '\f', '\r',
            Character.LINE_SEPARATOR,
            Character.PARAGRAPH_SEPARATOR,
            '\u000B', // VERTICAL TABULATION.
            '\u001C', // FILE SEPARATOR.
            '\u001D', // GROUP SEPARATOR.
            '\u001E', // RECORD SEPARATOR.
            '\u001F', // UNIT SEPARATOR.
    };

    /** The list of characters that are breaks in text. */
    private static final Set<Character> BREAK_CHAR_SET = new HashSet<>();

    static {
        for (char c : BREAK_CHARS) {
            BREAK_CHAR_SET.add(c);
        }
    }

    /** Defines the TextStyle for paragraph, and associated output formats. */
    private final TextStyle.Builder styleBuilder;

    /**
     * Construct from an output.
     * @param output the Appendable to write the output to.
     */
    public TextHelpWriter(final Appendable output) {
        super(output);
        styleBuilder = new TextStyle.Builder().setMaxWidth(DEFAULT_WIDTH)
                .setLeftPad(DEFAULT_LEFT_PAD).setIndent(DEFAULT_INDENT);
    }

    /**
     * Finds the next text wrap position after {@code startPos} for the text in {@code text} with the column width
     * {@code width}. The wrap point is the last position before startPos+width having a whitespace character (space,
     * \n, \r). If there is no whitespace character before startPos+width, it will return startPos+width.
     *
     * @param text The text being searched for the wrap position
     * @param width width of the wrapped text
     * @param startPos position from which to start the lookup whitespace character
     * @return position on which the text must be wrapped or @{code text.length()} if the wrap position is at the end of the text.
     * @since 1.10.0
     */
    public static int findWrapPos(final CharSequence text, final int width, final int startPos) {
        if (width < 1) {
            throw new IllegalArgumentException("Width must be greater than 0");
        }
        // handle case of width > text.
        // the line ends before the max wrap pos or a new line char found
        int limit = Math.min(startPos + width, text.length() - 1);

        for (int idx = startPos; idx < limit; idx++) {
            if (BREAK_CHAR_SET.contains(text.charAt(idx))) {
                return idx;
            }
        }

        if ((startPos + width) >= text.length()) {
            return text.length();
        }

        int pos;
        // look for the last whitespace character before limit
        for (pos = limit; pos >= startPos; --pos) {
            if (Util.isWhitespace(text.charAt(pos))) {
                break;
            }
        }
        // if we found it return it, otherwise just chop at limit
        return pos > startPos ? pos : limit - 1;
    }

    /**
     * Sets the maximum width for the output.
     * @param maxWidth the maximum width for the output.
     */
    public void setMaxWidth(final int maxWidth) {
        styleBuilder.setMaxWidth(maxWidth);
    }

    /**
     * Gets the maximum width for the output
     * @return the maximum width for the output.
     */
    public int getMaxWidth() {
        return styleBuilder.getMaxWidth();
    }

    /**
     * Sets the left padding: the number of characters from the left edge to start output.
     * @param leftPad the left padding.
     */
    public void setLeftPad(final int leftPad) {
        styleBuilder.setLeftPad(leftPad);
    }

    /**
     * Returns the left padding for the output.
     * @return The left padding for the output.
     */
    public int getLeftPad() {
        return styleBuilder.getLeftPad();
    }

    /**
     * Sets the indent for the output.
     * @param indent the indent used for paragraphs.
     */
    public void setIndent(final int indent) {
        styleBuilder.setIndent(indent);
    }

    /**
     * Gets the indent for the output.
     * @return the indent ofr the page.
     */
    public int getIndent() {
        return styleBuilder.getIndent();
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
    public void appendTitle(final CharSequence title) throws IOException {
        if (!Util.isEmpty(title)) {
            TextStyle style = styleBuilder.get();
            Queue<String> queue = makeColumnQueue(title, style);
            queue.add(Util.createPadding(style.getLeftPad()) + Util.filledString(Math.min(title.length(), style.getMaxWidth()), '#'));
            queue.add(BLANK_LINE);
            printQueue(queue);
        }
    }

    @Override
    public void appendParagraph(final CharSequence paragraph) throws IOException {
        if (!Util.isEmpty(paragraph)) {
            Queue<String> queue = makeColumnQueue(paragraph, styleBuilder.get());
            queue.add(BLANK_LINE);
            printQueue(queue);
        }
    }

    @Override
    public void appendHeader(final int level, final CharSequence text) throws IOException {
        if (!Util.isEmpty(text)) {
            if (level < 1) {
                throw new IllegalArgumentException("level must be at least 1");
            }
            char[] fillChars = {'=', '%', '+', '_'};
            int idx = Math.min(level, fillChars.length) - 1;
            TextStyle style = styleBuilder.get();
            Queue<String> queue = makeColumnQueue(text, style);
            queue.add(Util.createPadding(style.getLeftPad()) + Util.filledString(Math.min(text.length(), style.getMaxWidth()), fillChars[idx]));
            queue.add(BLANK_LINE);
            printQueue(queue);
        }
    }

    @Override
    public void appendList(final boolean ordered, final Collection<CharSequence> list) throws IOException {
        if (list != null && !list.isEmpty()) {
            TextStyle.Builder builder = new TextStyle.Builder().setLeftPad(styleBuilder.getLeftPad()).setIndent(DEFAULT_LIST_INDENT);
            int i = 1;
            for (CharSequence line : list) {
                String entry = ordered ? format(" %s. %s", i++, Util.defaultValue(line, BLANK_LINE)) :
                        format(" * %s", Util.defaultValue(line, BLANK_LINE));
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
            newIndent = Math.min(resize(builder.getIndent(), indentFrac), maxAdjust);
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
     * @return a new TableDefinition with adjusted values.
     */
    protected TableDefinition adjustTableFormat(final TableDefinition table) {
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
            if (builder.isScalable()) {
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
                if (builder.isScalable()) {
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

        return TableDefinition.from(table.caption(), styles, table.headers(), table.rows());
    }

    @Override
    public void appendTable(final TableDefinition rawTable) throws IOException {
        TableDefinition table = adjustTableFormat(rawTable);
        // write the table
        appendParagraph(table.caption());

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
    protected Queue<String> makeColumnQueue(final CharSequence columnData, final TextStyle style) {
        String lpad = Util.createPadding(style.getLeftPad());
        String indent = Util.createPadding(style.getIndent());
        Queue<String> result = new LinkedList<>();
        int wrapPos = 0;
        int nextPos;
        int wrappedMaxWidth = style.getMaxWidth() - indent.length();
        while (wrapPos < columnData.length()) {
            int workingWidth = wrapPos == 0 ? style.getMaxWidth() : wrappedMaxWidth;
            nextPos = findWrapPos(columnData, workingWidth, wrapPos);
            CharSequence working = columnData.subSequence(wrapPos, nextPos);
            result.add(lpad + style.pad(wrapPos > 0, working));
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

    /**
     * Print wrapped text using the TextHelpWriter output style.
     * @param text the text to wrap
     * @throws IOException on output error.
     */
    public void printWrapped(final String text) throws IOException {
        printQueue(makeColumnQueue(text, this.styleBuilder.get()));
    }
}
