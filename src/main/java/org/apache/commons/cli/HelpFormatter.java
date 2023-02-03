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

package org.apache.commons.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A formatter of help messages for command line options.
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>
 * Options options = new Options();
 * options.addOption(OptionBuilder.withLongOpt("file").withDescription("The file to be processed").hasArg().withArgName("FILE").isRequired().create('f'));
 * options.addOption(OptionBuilder.withLongOpt("version").withDescription("Print the version of the application").create('v'));
 * options.addOption(OptionBuilder.withLongOpt("help").create('h'));
 *
 * String header = "Do something useful with an input file\n\n";
 * String footer = "\nPlease report issues at http://example.com/issues";
 *
 * HelpFormatter formatter = new HelpFormatter();
 * formatter.printHelp("myapp", header, options, footer, true);
 * </pre>
 *
 * This produces the following output:
 *
 * <pre>
 * usage: myapp -f &lt;FILE&gt; [-h] [-v]
 * Do something useful with an input file
 *
 *  -f,--file &lt;FILE&gt;   The file to be processed
 *  -h,--help
 *  -v,--version       Print the version of the application
 *
 * Please report issues at http://example.com/issues
 * </pre>
 */
public class HelpFormatter {

    /**
     * This class implements the {@code Comparator} interface for comparing Options.
     */
    private static class OptionComparator implements Comparator<Option>, Serializable {
        /** The serial version UID. */
        private static final long serialVersionUID = 5305467873966684014L;

        /**
         * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument
         * is less than, equal to, or greater than the second.
         *
         * @param opt1 The first Option to be compared.
         * @param opt2 The second Option to be compared.
         * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than
         *         the second.
         */
        @Override
        public int compare(final Option opt1, final Option opt2) {
            return opt1.getKey().compareToIgnoreCase(opt2.getKey());
        }
    }

    /** Default number of characters per line */
    public static final int DEFAULT_WIDTH = 74;

    /** Default padding to the left of each line */
    public static final int DEFAULT_LEFT_PAD = 1;

    /** number of space characters to be prefixed to each description line */
    public static final int DEFAULT_DESC_PAD = 3;

    /** The string to display at the beginning of the usage statement */
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

    /** Default prefix for shortOpts */
    public static final String DEFAULT_OPT_PREFIX = "-";

    /** Default prefix for long Option */
    public static final String DEFAULT_LONG_OPT_PREFIX = "--";

    /**
     * default separator displayed between a long Option and its value
     *
     * @since 1.3
     **/
    public static final String DEFAULT_LONG_OPT_SEPARATOR = " ";

    /** Default name for an argument */
    public static final String DEFAULT_ARG_NAME = "arg";

    /**
     * number of characters per line
     *
     * @deprecated Scope will be made private for next major version - use get/setWidth methods instead.
     */
    @Deprecated
    public int defaultWidth = DEFAULT_WIDTH;

    /**
     * amount of padding to the left of each line
     *
     * @deprecated Scope will be made private for next major version - use get/setLeftPadding methods instead.
     */
    @Deprecated
    public int defaultLeftPad = DEFAULT_LEFT_PAD;

    /**
     * the number of characters of padding to be prefixed to each description line
     *
     * @deprecated Scope will be made private for next major version - use get/setDescPadding methods instead.
     */
    @Deprecated
    public int defaultDescPad = DEFAULT_DESC_PAD;

    /**
     * the string to display at the beginning of the usage statement
     *
     * @deprecated Scope will be made private for next major version - use get/setSyntaxPrefix methods instead.
     */
    @Deprecated
    public String defaultSyntaxPrefix = DEFAULT_SYNTAX_PREFIX;

    /**
     * the new line string
     *
     * @deprecated Scope will be made private for next major version - use get/setNewLine methods instead.
     */
    @Deprecated
    public String defaultNewLine = System.getProperty("line.separator");

    /**
     * the shortOpt prefix
     *
     * @deprecated Scope will be made private for next major version - use get/setOptPrefix methods instead.
     */
    @Deprecated
    public String defaultOptPrefix = DEFAULT_OPT_PREFIX;

    /**
     * the long Opt prefix
     *
     * @deprecated Scope will be made private for next major version - use get/setLongOptPrefix methods instead.
     */
    @Deprecated
    public String defaultLongOptPrefix = DEFAULT_LONG_OPT_PREFIX;

    /**
     * the name of the argument
     *
     * @deprecated Scope will be made private for next major version - use get/setArgName methods instead.
     */
    @Deprecated
    public String defaultArgName = DEFAULT_ARG_NAME;

    /**
     * Comparator used to sort the options when they output in help text
     *
     * Defaults to case-insensitive alphabetical sorting by option key
     */
    protected Comparator<Option> optionComparator = new OptionComparator();

    /** The separator displayed between the long option and its value. */
    private String longOptSeparator = DEFAULT_LONG_OPT_SEPARATOR;

    /**
     * Appends the usage clause for an Option to a StringBuffer.
     *
     * @param buff the StringBuffer to append to
     * @param option the Option to append
     * @param required whether the Option is required or not
     */
    private void appendOption(final StringBuffer buff, final Option option, final boolean required) {
        if (!required) {
            buff.append("[");
        }

        if (option.getOpt() != null) {
            buff.append("-").append(option.getOpt());
        } else {
            buff.append("--").append(option.getLongOpt());
        }

        // if the Option has a value and a non blank argname
        if (option.hasArg() && (option.getArgName() == null || !option.getArgName().isEmpty())) {
            buff.append(option.getOpt() == null ? longOptSeparator : " ");
            buff.append("<").append(option.getArgName() != null ? option.getArgName() : getArgName()).append(">");
        }

        // if the Option is not a required option
        if (!required) {
            buff.append("]");
        }
    }

    /**
     * Appends the usage clause for an OptionGroup to a StringBuffer. The clause is wrapped in square brackets if the group
     * is required. The display of the options is handled by appendOption
     *
     * @param buff the StringBuffer to append to
     * @param group the group to append
     * @see #appendOption(StringBuffer,Option,boolean)
     */
    private void appendOptionGroup(final StringBuffer buff, final OptionGroup group) {
        if (!group.isRequired()) {
            buff.append("[");
        }

        final List<Option> optList = new ArrayList<>(group.getOptions());
        if (getOptionComparator() != null) {
            Collections.sort(optList, getOptionComparator());
        }
        // for each option in the OptionGroup
        for (final Iterator<Option> it = optList.iterator(); it.hasNext();) {
            // whether the option is required or not is handled at group level
            appendOption(buff, it.next(), true);

            if (it.hasNext()) {
                buff.append(" | ");
            }
        }

        if (!group.isRequired()) {
            buff.append("]");
        }
    }

    /**
     * Return a String of padding of length {@code len}.
     *
     * @param len The length of the String of padding to create.
     *
     * @return The String of padding
     */
    protected String createPadding(final int len) {
        final char[] padding = new char[len];
        Arrays.fill(padding, ' ');

        return new String(padding);
    }

    /**
     * Finds the next text wrap position after {@code startPos} for the text in {@code text} with the column width
     * {@code width}. The wrap point is the last position before startPos+width having a whitespace character (space,
     * \n, \r). If there is no whitespace character before startPos+width, it will return startPos+width.
     *
     * @param text The text being searched for the wrap position
     * @param width width of the wrapped text
     * @param startPos position from which to start the lookup whitespace character
     * @return position on which the text must be wrapped or -1 if the wrap position is at the end of the text
     */
    protected int findWrapPos(final String text, final int width, final int startPos) {
        // the line ends before the max wrap pos or a new line char found
        int pos = text.indexOf('\n', startPos);
        if (pos != -1 && pos <= width) {
            return pos + 1;
        }

        pos = text.indexOf('\t', startPos);
        if (pos != -1 && pos <= width) {
            return pos + 1;
        }

        if (startPos + width >= text.length()) {
            return -1;
        }

        // look for the last whitespace character before startPos+width
        for (pos = startPos + width; pos >= startPos; --pos) {
            final char c = text.charAt(pos);
            if (c == ' ' || c == '\n' || c == '\r') {
                break;
            }
        }

        // if we found it - just return
        if (pos > startPos) {
            return pos;
        }

        // if we didn't find one, simply chop at startPos+width
        pos = startPos + width;

        return pos == text.length() ? -1 : pos;
    }

    /**
     * Gets the 'argName'.
     *
     * @return the 'argName'
     */
    public String getArgName() {
        return defaultArgName;
    }

    /**
     * Gets the 'descPadding'.
     *
     * @return the 'descPadding'
     */
    public int getDescPadding() {
        return defaultDescPad;
    }

    /**
     * Gets the 'leftPadding'.
     *
     * @return the 'leftPadding'
     */
    public int getLeftPadding() {
        return defaultLeftPad;
    }

    /**
     * Gets the 'longOptPrefix'.
     *
     * @return the 'longOptPrefix'
     */
    public String getLongOptPrefix() {
        return defaultLongOptPrefix;
    }

    /**
     * Gets the separator displayed between a long option and its value.
     *
     * @return the separator
     * @since 1.3
     */
    public String getLongOptSeparator() {
        return longOptSeparator;
    }

    /**
     * Gets the 'newLine'.
     *
     * @return the 'newLine'
     */
    public String getNewLine() {
        return defaultNewLine;
    }

    /**
     * Comparator used to sort the options when they output in help text. Defaults to case-insensitive alphabetical sorting
     * by option key.
     *
     * @return the {@link Comparator} currently in use to sort the options
     * @since 1.2
     */
    public Comparator<Option> getOptionComparator() {
        return optionComparator;
    }

    /**
     * Gets the 'optPrefix'.
     *
     * @return the 'optPrefix'
     */
    public String getOptPrefix() {
        return defaultOptPrefix;
    }

    /**
     * Gets the 'syntaxPrefix'.
     *
     * @return the 'syntaxPrefix'
     */
    public String getSyntaxPrefix() {
        return defaultSyntaxPrefix;
    }

    /**
     * Gets the 'width'.
     *
     * @return the 'width'
     */
    public int getWidth() {
        return defaultWidth;
    }

    /**
     * Print the help for {@code options} with the specified command line syntax. This method prints help information
     * to System.out.
     *
     * @param width the number of characters to be displayed on each line
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param footer the banner to display at the end of the help
     */
    public void printHelp(final int width, final String cmdLineSyntax, final String header, final Options options, final String footer) {
        printHelp(width, cmdLineSyntax, header, options, footer, false);
    }

    /**
     * Print the help for {@code options} with the specified command line syntax. This method prints help information
     * to System.out.
     *
     * @param width the number of characters to be displayed on each line
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     */
    public void printHelp(final int width, final String cmdLineSyntax, final String header, final Options options, final String footer,
        final boolean autoUsage) {
        final PrintWriter pw = new PrintWriter(System.out);

        printHelp(pw, width, cmdLineSyntax, header, options, getLeftPadding(), getDescPadding(), footer, autoUsage);
        pw.flush();
    }

    /**
     * Print the help for {@code options} with the specified command line syntax.
     *
     * @param pw the writer to which the help will be written
     * @param width the number of characters to be displayed on each line
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param leftPad the number of characters of padding to be prefixed to each line
     * @param descPad the number of characters of padding to be prefixed to each description line
     * @param footer the banner to display at the end of the help
     *
     * @throws IllegalStateException if there is no room to print a line
     */
    public void printHelp(final PrintWriter pw, final int width, final String cmdLineSyntax, final String header, final Options options, final int leftPad,
        final int descPad, final String footer) {
        printHelp(pw, width, cmdLineSyntax, header, options, leftPad, descPad, footer, false);
    }

    /**
     * Print the help for {@code options} with the specified command line syntax.
     *
     * @param pw the writer to which the help will be written
     * @param width the number of characters to be displayed on each line
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param leftPad the number of characters of padding to be prefixed to each line
     * @param descPad the number of characters of padding to be prefixed to each description line
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     *
     * @throws IllegalStateException if there is no room to print a line
     */
    public void printHelp(final PrintWriter pw, final int width, final String cmdLineSyntax, final String header, final Options options, final int leftPad,
        final int descPad, final String footer, final boolean autoUsage) {
        if (cmdLineSyntax == null || cmdLineSyntax.isEmpty()) {
            throw new IllegalArgumentException("cmdLineSyntax not provided");
        }

        if (autoUsage) {
            printUsage(pw, width, cmdLineSyntax, options);
        } else {
            printUsage(pw, width, cmdLineSyntax);
        }

        if (header != null && !header.isEmpty()) {
            printWrapped(pw, width, header);
        }

        printOptions(pw, width, options, leftPad, descPad);

        if (footer != null && !footer.isEmpty()) {
            printWrapped(pw, width, footer);
        }
    }

    /**
     * Print the help for {@code options} with the specified command line syntax. This method prints help information
     * to System.out.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param options the Options instance
     */
    public void printHelp(final String cmdLineSyntax, final Options options) {
        printHelp(getWidth(), cmdLineSyntax, null, options, null, false);
    }

    /**
     * Print the help for {@code options} with the specified command line syntax. This method prints help information
     * to System.out.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param options the Options instance
     * @param autoUsage whether to print an automatically generated usage statement
     */
    public void printHelp(final String cmdLineSyntax, final Options options, final boolean autoUsage) {
        printHelp(getWidth(), cmdLineSyntax, null, options, null, autoUsage);
    }

    /**
     * Print the help for {@code options} with the specified command line syntax. This method prints help information
     * to System.out.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param footer the banner to display at the end of the help
     */
    public void printHelp(final String cmdLineSyntax, final String header, final Options options, final String footer) {
        printHelp(cmdLineSyntax, header, options, footer, false);
    }

    /**
     * Print the help for {@code options} with the specified command line syntax. This method prints help information
     * to System.out.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     */
    public void printHelp(final String cmdLineSyntax, final String header, final Options options, final String footer, final boolean autoUsage) {
        printHelp(getWidth(), cmdLineSyntax, header, options, footer, autoUsage);
    }

    /**
     * Print the help for the specified Options to the specified writer, using the specified width, left padding and
     * description padding.
     *
     * @param pw The printWriter to write the help to
     * @param width The number of characters to display per line
     * @param options The command line Options
     * @param leftPad the number of characters of padding to be prefixed to each line
     * @param descPad the number of characters of padding to be prefixed to each description line
     */
    public void printOptions(final PrintWriter pw, final int width, final Options options, final int leftPad, final int descPad) {
        final StringBuffer sb = new StringBuffer();

        renderOptions(sb, width, options, leftPad, descPad);
        pw.println(sb.toString());
    }

    /**
     * Print the cmdLineSyntax to the specified writer, using the specified width.
     *
     * @param pw The printWriter to write the help to
     * @param width The number of characters per line for the usage statement.
     * @param cmdLineSyntax The usage statement.
     */
    public void printUsage(final PrintWriter pw, final int width, final String cmdLineSyntax) {
        final int argPos = cmdLineSyntax.indexOf(' ') + 1;

        printWrapped(pw, width, getSyntaxPrefix().length() + argPos, getSyntaxPrefix() + cmdLineSyntax);
    }

    /**
     * Prints the usage statement for the specified application.
     *
     * @param pw The PrintWriter to print the usage statement
     * @param width The number of characters to display per line
     * @param app The application name
     * @param options The command line Options
     */
    public void printUsage(final PrintWriter pw, final int width, final String app, final Options options) {
        // initialize the string buffer
        final StringBuffer buff = new StringBuffer(getSyntaxPrefix()).append(app).append(" ");

        // create a list for processed option groups
        final Collection<OptionGroup> processedGroups = new ArrayList<>();

        final List<Option> optList = new ArrayList<>(options.getOptions());
        if (getOptionComparator() != null) {
            Collections.sort(optList, getOptionComparator());
        }
        // iterate over the options
        for (final Iterator<Option> it = optList.iterator(); it.hasNext();) {
            // get the next Option
            final Option option = it.next();

            // check if the option is part of an OptionGroup
            final OptionGroup group = options.getOptionGroup(option);

            // if the option is part of a group
            if (group != null) {
                // and if the group has not already been processed
                if (!processedGroups.contains(group)) {
                    // add the group to the processed list
                    processedGroups.add(group);

                    // add the usage clause
                    appendOptionGroup(buff, group);
                }

                // otherwise the option was displayed in the group
                // previously so ignore it.
            }

            // if the Option is not part of an OptionGroup
            else {
                appendOption(buff, option, option.isRequired());
            }

            if (it.hasNext()) {
                buff.append(" ");
            }
        }

        // call printWrapped
        printWrapped(pw, width, buff.toString().indexOf(' ') + 1, buff.toString());
    }

    /**
     * Print the specified text to the specified PrintWriter.
     *
     * @param pw The printWriter to write the help to
     * @param width The number of characters to display per line
     * @param nextLineTabStop The position on the next line for the first tab.
     * @param text The text to be written to the PrintWriter
     */
    public void printWrapped(final PrintWriter pw, final int width, final int nextLineTabStop, final String text) {
        final StringBuffer sb = new StringBuffer(text.length());

        renderWrappedTextBlock(sb, width, nextLineTabStop, text);
        pw.println(sb.toString());
    }

    /**
     * Print the specified text to the specified PrintWriter.
     *
     * @param pw The printWriter to write the help to
     * @param width The number of characters to display per line
     * @param text The text to be written to the PrintWriter
     */
    public void printWrapped(final PrintWriter pw, final int width, final String text) {
        printWrapped(pw, width, 0, text);
    }

    /**
     * Render the specified Options and return the rendered Options in a StringBuffer.
     *
     * @param sb The StringBuffer to place the rendered Options into.
     * @param width The number of characters to display per line
     * @param options The command line Options
     * @param leftPad the number of characters of padding to be prefixed to each line
     * @param descPad the number of characters of padding to be prefixed to each description line
     *
     * @return the StringBuffer with the rendered Options contents.
     */
    protected StringBuffer renderOptions(final StringBuffer sb, final int width, final Options options, final int leftPad, final int descPad) {
        final String lpad = createPadding(leftPad);
        final String dpad = createPadding(descPad);

        // first create list containing only <lpad>-a,--aaa where
        // -a is opt and --aaa is long opt; in parallel look for
        // the longest opt string this list will be then used to
        // sort options ascending
        int max = 0;
        final List<StringBuffer> prefixList = new ArrayList<>();

        final List<Option> optList = options.helpOptions();

        if (getOptionComparator() != null) {
            Collections.sort(optList, getOptionComparator());
        }

        for (final Option option : optList) {
            final StringBuffer optBuf = new StringBuffer();

            if (option.getOpt() == null) {
                optBuf.append(lpad).append("   ").append(getLongOptPrefix()).append(option.getLongOpt());
            } else {
                optBuf.append(lpad).append(getOptPrefix()).append(option.getOpt());

                if (option.hasLongOpt()) {
                    optBuf.append(',').append(getLongOptPrefix()).append(option.getLongOpt());
                }
            }

            if (option.hasArg()) {
                final String argName = option.getArgName();
                if (argName != null && argName.isEmpty()) {
                    // if the option has a blank argname
                    optBuf.append(' ');
                } else {
                    optBuf.append(option.hasLongOpt() ? longOptSeparator : " ");
                    optBuf.append("<").append(argName != null ? option.getArgName() : getArgName()).append(">");
                }
            }

            prefixList.add(optBuf);
            max = Math.max(optBuf.length(), max);
        }

        int x = 0;

        for (final Iterator<Option> it = optList.iterator(); it.hasNext();) {
            final Option option = it.next();
            final StringBuilder optBuf = new StringBuilder(prefixList.get(x++).toString());

            if (optBuf.length() < max) {
                optBuf.append(createPadding(max - optBuf.length()));
            }

            optBuf.append(dpad);

            final int nextLineTabStop = max + descPad;

            if (option.getDescription() != null) {
                optBuf.append(option.getDescription());
            }

            renderWrappedText(sb, width, nextLineTabStop, optBuf.toString());

            if (it.hasNext()) {
                sb.append(getNewLine());
            }
        }

        return sb;
    }

    /**
     * Render the specified text and return the rendered Options in a StringBuffer.
     *
     * @param sb The StringBuffer to place the rendered text into.
     * @param width The number of characters to display per line
     * @param nextLineTabStop The position on the next line for the first tab.
     * @param text The text to be rendered.
     *
     * @return the StringBuffer with the rendered Options contents.
     */
    protected StringBuffer renderWrappedText(final StringBuffer sb, final int width, int nextLineTabStop, String text) {
        int pos = findWrapPos(text, width, 0);

        if (pos == -1) {
            sb.append(rtrim(text));

            return sb;
        }
        sb.append(rtrim(text.substring(0, pos))).append(getNewLine());

        if (nextLineTabStop >= width) {
            // stops infinite loop happening
            nextLineTabStop = 1;
        }

        // all following lines must be padded with nextLineTabStop space characters
        final String padding = createPadding(nextLineTabStop);

        while (true) {
            text = padding + text.substring(pos).trim();
            pos = findWrapPos(text, width, 0);

            if (pos == -1) {
                sb.append(text);

                return sb;
            }

            if (text.length() > width && pos == nextLineTabStop - 1) {
                pos = width;
            }

            sb.append(rtrim(text.substring(0, pos))).append(getNewLine());
        }
    }

    /**
     * Render the specified text width a maximum width. This method differs from renderWrappedText by not removing leading
     * spaces after a new line.
     *
     * @param sb The StringBuffer to place the rendered text into.
     * @param width The number of characters to display per line
     * @param nextLineTabStop The position on the next line for the first tab.
     * @param text The text to be rendered.
     */
    private Appendable renderWrappedTextBlock(final StringBuffer sb, final int width, final int nextLineTabStop, final String text) {
        try {
            final BufferedReader in = new BufferedReader(new StringReader(text));
            String line;
            boolean firstLine = true;
            while ((line = in.readLine()) != null) {
                if (!firstLine) {
                    sb.append(getNewLine());
                } else {
                    firstLine = false;
                }
                renderWrappedText(sb, width, nextLineTabStop, line);
            }
        } catch (final IOException e) { // NOPMD
            // cannot happen
        }

        return sb;
    }

    /**
     * Remove the trailing whitespace from the specified String.
     *
     * @param s The String to remove the trailing padding from.
     *
     * @return The String of without the trailing padding
     */
    protected String rtrim(final String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        int pos = s.length();

        while (pos > 0 && Character.isWhitespace(s.charAt(pos - 1))) {
            --pos;
        }

        return s.substring(0, pos);
    }

    /**
     * Sets the 'argName'.
     *
     * @param name the new value of 'argName'
     */
    public void setArgName(final String name) {
        this.defaultArgName = name;
    }

    /**
     * Sets the 'descPadding'.
     *
     * @param padding the new value of 'descPadding'
     */
    public void setDescPadding(final int padding) {
        this.defaultDescPad = padding;
    }

    /**
     * Sets the 'leftPadding'.
     *
     * @param padding the new value of 'leftPadding'
     */
    public void setLeftPadding(final int padding) {
        this.defaultLeftPad = padding;
    }

    /**
     * Sets the 'longOptPrefix'.
     *
     * @param prefix the new value of 'longOptPrefix'
     */
    public void setLongOptPrefix(final String prefix) {
        this.defaultLongOptPrefix = prefix;
    }

    /**
     * Set the separator displayed between a long option and its value. Ensure that the separator specified is supported by
     * the parser used, typically ' ' or '='.
     *
     * @param longOptSeparator the separator, typically ' ' or '='.
     * @since 1.3
     */
    public void setLongOptSeparator(final String longOptSeparator) {
        this.longOptSeparator = longOptSeparator;
    }

    /**
     * Sets the 'newLine'.
     *
     * @param newline the new value of 'newLine'
     */
    public void setNewLine(final String newline) {
        this.defaultNewLine = newline;
    }

    /**
     * Set the comparator used to sort the options when they output in help text. Passing in a null comparator will keep the
     * options in the order they were declared.
     *
     * @param comparator the {@link Comparator} to use for sorting the options
     * @since 1.2
     */
    public void setOptionComparator(final Comparator<Option> comparator) {
        this.optionComparator = comparator;
    }

    /**
     * Sets the 'optPrefix'.
     *
     * @param prefix the new value of 'optPrefix'
     */
    public void setOptPrefix(final String prefix) {
        this.defaultOptPrefix = prefix;
    }

    /**
     * Sets the 'syntaxPrefix'.
     *
     * @param prefix the new value of 'syntaxPrefix'
     */
    public void setSyntaxPrefix(final String prefix) {
        this.defaultSyntaxPrefix = prefix;
    }

    /**
     * Sets the 'width'.
     *
     * @param width the new value of 'width'
     */
    public void setWidth(final int width) {
        this.defaultWidth = width;
    }

}
