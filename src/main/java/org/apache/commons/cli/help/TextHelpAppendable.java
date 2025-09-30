/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package org.apache.commons.cli.help;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Writes text format output.
 *
 * @since 1.10.0
 */
public class TextHelpAppendable extends FilterHelpAppendable {

    /** The default number of characters per line: {@value}. */
    public static final int DEFAULT_WIDTH = 74;

    /** The default padding to the left of each line: {@value}. */
    public static final int DEFAULT_LEFT_PAD = 1;

    /** The number of space characters to be prefixed to each description line: {@value}. */
    public static final int DEFAULT_INDENT = 3;

    /** The number of space characters before a list continuation line: {@value}. */
    public static final int DEFAULT_LIST_INDENT = 7;

    /** A blank line in the output: {@value}. */
    private static final String BLANK_LINE = "";

    /** The set of characters that are breaks in text. */
    // @formatter:off
    private static final Set<Character> BREAK_CHAR_SET = Collections.unmodifiableSet(new HashSet<>(Arrays.asList('\t', '\n', '\f', '\r',
            (char) Character.LINE_SEPARATOR,
            (char) Character.PARAGRAPH_SEPARATOR,
            '\u000b', // VERTICAL TABULATION.
            '\u001c', // FILE SEPARATOR.
            '\u001d', // GROUP SEPARATOR.
            '\u001e', // RECORD SEPARATOR.
            '\u001f' // UNIT SEPARATOR.
    )));
    // @formatter:on

    /**
     * Finds the next text wrap position after {@code startPos} for the text in {@code text} with the column width {@code width}. The wrap point is the last
     * position before startPos+width having a whitespace character (space, \n, \r). If there is no whitespace character before startPos+width, it will return
     * startPos+width.
     *
     * @param text     The text being searched for the wrap position
     * @param width    width of the wrapped text
     * @param startPos position from which to start the lookup whitespace character
     * @return position on which the text must be wrapped or @{code text.length()} if the wrap position is at the end of the text.
     */
    public static int indexOfWrap(final CharSequence text, final int width, final int startPos) {
        if (width < 1) {
            throw new IllegalArgumentException("Width must be greater than 0");
        }
        // handle case of width > text.
        // the line ends before the max wrap pos or a new line char found
        int limit = Math.min(startPos + width, text.length());
        for (int idx = startPos; idx < limit; idx++) {
            if (BREAK_CHAR_SET.contains(text.charAt(idx))) {
                return idx;
            }
        }
        if (startPos + width >= text.length()) {
            return text.length();
        }

        limit = Math.min(startPos + width, text.length() - 1);
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
     * Creates a new TextHelpAppendable on {@link System#out}.
     *
     * @return a new TextHelpAppendable on {@link System#out}.
     */
    protected static TextHelpAppendable systemOut() {
        return new TextHelpAppendable(System.out);
    }

    /** Defines the TextStyle for paragraph, and associated output formats. */
    private final TextStyle.Builder textStyleBuilder;

    /**
     * Constructs an appendable filter built on top of the specified underlying appendable.
     *
     * @param output the underlying appendable to be assigned to the field {@code this.output} for later use, or {@code null} if this instance is to be created
     *               without an underlying stream.
     */
    public TextHelpAppendable(final Appendable output) {
        super(output);
        // @formatter:off
        textStyleBuilder = TextStyle.builder()
            .setMaxWidth(DEFAULT_WIDTH)
            .setLeftPad(DEFAULT_LEFT_PAD)
            .setIndent(DEFAULT_INDENT);
        // @formatter:on
    }

    /**
     * Adjusts the table format.
     * <p>
     * Given the width of the page and the size of the table attempt to resize the columns to fit the page width if necessary. Adjustments are made as follows:
     * </p>
     * <ul>
     * <li>The minimum size for a column may not be smaller than the length of the column header</li>
     * <li>The maximum size is set to the maximum of the length of the header or the longest line length.</li>
     * <li>If the total size of the columns is greater than the page wight, adjust the size of VARIABLE columns to attempt reduce the width to the the maximum
     * size.
     * </ul>
     * <p>
     * Note: it is possible for the size of the columns to exceed the declared page width. In this case the table will extend beyond the desired page width.
     * </p>
     *
     * @param table the table to adjust.
     * @return a new TableDefinition with adjusted values.
     */
    protected TableDefinition adjustTableFormat(final TableDefinition table) {
        final List<TextStyle.Builder> styleBuilders = new ArrayList<>();
        for (int i = 0; i < table.columnTextStyles().size(); i++) {
            final TextStyle style = table.columnTextStyles().get(i);
            final TextStyle.Builder builder = TextStyle.builder().setTextStyle(style);
            styleBuilders.add(builder);
            final String header = table.headers().get(i);

            if (style.getMaxWidth() < header.length() || style.getMaxWidth() == TextStyle.UNSET_MAX_WIDTH) {
                builder.setMaxWidth(header.length());
            }
            if (style.getMinWidth() < header.length()) {
                builder.setMinWidth(header.length());
            }
            for (final List<String> row : table.rows()) {
                final String cell = row.get(i);
                if (cell.length() > builder.getMaxWidth()) {
                    builder.setMaxWidth(cell.length());
                }
            }
        }
        // calculate the total width.
        int calcWidth = 0;
        int adjustedMaxWidth = textStyleBuilder.getMaxWidth();
        for (final TextStyle.Builder builder : styleBuilders) {
            adjustedMaxWidth -= builder.getLeftPad();
            if (builder.isScalable()) {
                calcWidth += builder.getMaxWidth();
            } else {
                adjustedMaxWidth -= builder.getMaxWidth();
            }
        }
        // rescale if necessary
        if (calcWidth > adjustedMaxWidth) {
            final double fraction = adjustedMaxWidth * 1.0 / calcWidth;
            for (int i = 0; i < styleBuilders.size(); i++) {
                final TextStyle.Builder builder = styleBuilders.get(i);
                if (builder.isScalable()) {
                    // resize and remove the padding from the maxWidth calculation.
                    styleBuilders.set(i, resize(builder, fraction));
                }
            }
        }
        // regenerate the styles
        final List<TextStyle> styles = new ArrayList<>();
        for (final TextStyle.Builder builder : styleBuilders) {
            // adjust by removing the padding as it was not accounted for above.
            styles.add(builder.get());
        }
        return TableDefinition.from(table.caption(), styles, table.headers(), table.rows());
    }

    @Override
    public void appendHeader(final int level, final CharSequence text) throws IOException {
        if (!Util.isEmpty(text)) {
            if (level < 1) {
                throw new IllegalArgumentException("level must be at least 1");
            }
            final char[] fillChars = { '=', '%', '+', '_' };
            final int idx = Math.min(level, fillChars.length) - 1;
            final TextStyle style = textStyleBuilder.get();
            final Queue<String> queue = makeColumnQueue(text, style);
            queue.add(Util.repeatSpace(style.getLeftPad()) + Util.repeat(Math.min(text.length(), style.getMaxWidth()), fillChars[idx]));
            queue.add(BLANK_LINE);
            printQueue(queue);
        }
    }

    @Override
    public void appendList(final boolean ordered, final Collection<CharSequence> list) throws IOException {
        if (list != null && !list.isEmpty()) {
            final TextStyle.Builder builder = TextStyle.builder().setLeftPad(textStyleBuilder.getLeftPad()).setIndent(DEFAULT_LIST_INDENT);
            int i = 1;
            for (final CharSequence line : list) {
                final String entry = ordered ? String.format(" %s. %s", i++, Util.defaultValue(line, BLANK_LINE))
                        : String.format(" * %s", Util.defaultValue(line, BLANK_LINE));
                builder.setMaxWidth(Math.min(textStyleBuilder.getMaxWidth(), entry.length()));
                printQueue(makeColumnQueue(entry, builder.get()));
            }
            output.append(System.lineSeparator());
        }
    }

    @Override
    public void appendParagraph(final CharSequence paragraph) throws IOException {
        if (!Util.isEmpty(paragraph)) {
            final Queue<String> queue = makeColumnQueue(paragraph, textStyleBuilder.get());
            queue.add(BLANK_LINE);
            printQueue(queue);
        }
    }

    @Override
    public void appendTable(final TableDefinition rawTable) throws IOException {
        final TableDefinition table = adjustTableFormat(rawTable);
        // write the table
        appendParagraph(table.caption());
        final List<TextStyle> headerStyles = new ArrayList<>();
        for (final TextStyle style : table.columnTextStyles()) {
            headerStyles.add(TextStyle.builder().setTextStyle(style).setAlignment(TextStyle.Alignment.CENTER).get());
        }
        writeColumnQueues(makeColumnQueues(table.headers(), headerStyles), headerStyles);
        for (final List<String> row : table.rows()) {
            writeColumnQueues(makeColumnQueues(row, table.columnTextStyles()), table.columnTextStyles());
        }
        output.append(System.lineSeparator());
    }

    @Override
    public void appendTitle(final CharSequence title) throws IOException {
        if (!Util.isEmpty(title)) {
            final TextStyle style = textStyleBuilder.get();
            final Queue<String> queue = makeColumnQueue(title, style);
            queue.add(Util.repeatSpace(style.getLeftPad()) + Util.repeat(Math.min(title.length(), style.getMaxWidth()), '#'));
            queue.add(BLANK_LINE);
            printQueue(queue);
        }
    }

    /**
     * Gets the indent for the output.
     *
     * @return the indent of the page.
     */
    public int getIndent() {
        return textStyleBuilder.getIndent();
    }

    /**
     * Returns the left padding for the output.
     *
     * @return The left padding for the output.
     */
    public int getLeftPad() {
        return textStyleBuilder.getLeftPad();
    }

    /**
     * Gets the maximum width for the output
     *
     * @return the maximum width for the output.
     */
    public int getMaxWidth() {
        return textStyleBuilder.getMaxWidth();
    }

    /**
     * Gets the style builder used to format text that is not otherwise formatted.
     *
     * @return The style builder used to format text that is not otherwise formatted.
     */
    public TextStyle.Builder getTextStyleBuilder() {
        return textStyleBuilder;
    }

    /**
     * Creates a queue comprising strings extracted from columnData where the alignment and length are determined by the style.
     *
     * @param columnData The string to wrap
     * @param style      The TextStyle to guide the wrapping.
     * @return A queue of the string wrapped.
     */
    protected Queue<String> makeColumnQueue(final CharSequence columnData, final TextStyle style) {
        final String lpad = Util.repeatSpace(style.getLeftPad());
        final String indent = Util.repeatSpace(style.getIndent());
        final Queue<String> result = new LinkedList<>();
        int wrapPos = 0;
        int lastPos;
        final int wrappedMaxWidth = style.getMaxWidth() - indent.length();
        while (wrapPos < columnData.length()) {
            final int workingWidth = wrapPos == 0 ? style.getMaxWidth() : wrappedMaxWidth;
            lastPos = indexOfWrap(columnData, workingWidth, wrapPos);
            final CharSequence working = columnData.subSequence(wrapPos, lastPos);
            result.add(lpad + style.pad(wrapPos > 0, working));
            wrapPos = Util.indexOfNonWhitespace(columnData, lastPos);
            wrapPos = wrapPos == -1 ? lastPos + 1 : wrapPos;
        }
        return result;
    }

    /**
     * For each column in the {@code columnData} apply the associated {@link TextStyle} and generated a queue of strings that are the maximum size of the column
     * + the left pad.
     *
     * @param columnData The column data to output.
     * @param styles     the styles to apply.
     * @return A list of queues of strings that represent each column in the table.
     */
    protected List<Queue<String>> makeColumnQueues(final List<String> columnData, final List<TextStyle> styles) {
        final List<Queue<String>> result = new ArrayList<>();
        for (int i = 0; i < columnData.size(); i++) {
            result.add(makeColumnQueue(columnData.get(i), styles.get(i)));
        }
        return result;
    }

    /**
     * Prints a queue of text.
     *
     * @param queue the queue of text to print.
     * @throws IOException on output error.
     */
    private void printQueue(final Queue<String> queue) throws IOException {
        for (final String s : queue) {
            appendFormat("%s%n", Util.rtrim(s));
        }
    }

    /**
     * Prints wrapped text using the TextHelpAppendable output style.
     *
     * @param text the text to wrap
     * @throws IOException on output error.
     */
    public void printWrapped(final String text) throws IOException {
        printQueue(makeColumnQueue(text, this.textStyleBuilder.get()));
    }

    /**
     * Prints wrapped text.
     *
     * @param text  the text to wrap
     * @param style the style for the wrapped text.
     * @throws IOException on output error.
     */
    public void printWrapped(final String text, final TextStyle style) throws IOException {
        printQueue(makeColumnQueue(text, style));
    }

    /**
     * Resizes an original width based on the fractional size it should be.
     *
     * @param orig     the original size.
     * @param fraction the fractional adjustment.
     * @return the resized value.
     */
    private int resize(final int orig, final double fraction) {
        return (int) (orig * fraction);
    }

    /**
     * Resizes a TextStyle builder based on the fractional size.
     *
     * @param builder  the builder to adjust.
     * @param fraction the fractional size (for example percentage of the current size) that the builder should be.
     * @return the builder with the maximum width and indent values resized.
     */
    protected TextStyle.Builder resize(final TextStyle.Builder builder, final double fraction) {
        final double indentFrac = builder.getIndent() * 1.0 / builder.getMaxWidth();
        builder.setMaxWidth(Math.max(resize(builder.getMaxWidth(), fraction), builder.getMinWidth()));
        final int maxAdjust = builder.getMaxWidth() / 3;
        int newIndent = builder.getMaxWidth() == 1 ? 0 : builder.getIndent();
        if (newIndent > maxAdjust) {
            newIndent = Math.min(resize(builder.getIndent(), indentFrac), maxAdjust);
        }
        builder.setIndent(newIndent);
        return builder;
    }

    /**
     * Sets the indent for the output.
     *
     * @param indent the indent used for paragraphs.
     */
    public void setIndent(final int indent) {
        textStyleBuilder.setIndent(indent);
    }

    /**
     * Sets the left padding: the number of characters from the left edge to start output.
     *
     * @param leftPad the left padding.
     */
    public void setLeftPad(final int leftPad) {
        textStyleBuilder.setLeftPad(leftPad);
    }

    /**
     * Sets the maximum width for the output.
     *
     * @param maxWidth the maximum width for the output.
     */
    public void setMaxWidth(final int maxWidth) {
        textStyleBuilder.setMaxWidth(maxWidth);
    }

    /**
     * Writes one line from each of the {@code columnQueues} until all the queues are exhausted. If an exhausted queue is encountered while other queues
     * continue to have content the exhausted queue will produce empty text for the output width of the column (maximum width + left pad).
     *
     * @param columnQueues the List of queues that represent the columns of data.
     * @param styles       the TextStyle for each column.
     * @throws IOException on output error.
     */
    protected void writeColumnQueues(final List<Queue<String>> columnQueues, final List<TextStyle> styles) throws IOException {
        boolean moreData = true;
        final String lPad = Util.repeatSpace(textStyleBuilder.get().getLeftPad());
        while (moreData) {
            output.append(lPad);
            moreData = false;
            for (int i = 0; i < columnQueues.size(); i++) {
                final TextStyle style = styles.get(i);
                final Queue<String> columnQueue = columnQueues.get(i);
                final String line = columnQueue.poll();
                if (Util.isEmpty(line)) {
                    output.append(Util.repeatSpace(style.getMaxWidth() + style.getLeftPad()));
                } else {
                    output.append(line);
                }
                moreData |= !columnQueue.isEmpty();
            }
            output.append(System.lineSeparator());
        }
    }
}
