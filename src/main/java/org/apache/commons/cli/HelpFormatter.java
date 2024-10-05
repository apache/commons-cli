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
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.cli.help.AbstractHelpFormatter;
import org.apache.commons.cli.help.OptionFormatter;
import org.apache.commons.cli.help.TextStyle;

/**
 * A formatter of help messages for command line options.
 * <p>
 * Example:
 * </p>
 * <pre>
 * Options options = new Options();
 * options.addOption(OptionBuilder.withLongOpt("file").withDescription("The file to be processed").hasArg().withArgName("FILE").isRequired().create('f'));
 * options.addOption(OptionBuilder.withLongOpt("version").withDescription("Print the version of the application").create('v'));
 * options.addOption(OptionBuilder.withLongOpt("help").create('h'));
 *
 * String header = "Do something useful with an input file\n\n";
 * String footer = "\nPlease report issues at https://example.com/issues";
 *
 * HelpFormatter formatter = new HelpFormatter();
 * formatter.printHelp("myapp", header, options, footer, true);
 * </pre>
 * <p>
 * This produces the following output:
 * </p>
 * <pre>
 * usage: myapp -f &lt;FILE&gt; [-h] [-v]
 * Do something useful with an input file
 *
 *  -f,--file &lt;FILE&gt;   The file to be processed
 *  -h,--help
 *  -v,--version       Print the version of the application
 *
 * Please report issues at https://example.com/issues
 * </pre>
 * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter}
 */
@Deprecated
public class HelpFormatter {

    /**
     * Builds {@link HelpFormatter}.
     *
     * @since 1.7.0
     */
    public static class Builder implements Supplier<HelpFormatter> {
        // TODO All other instance HelpFormatter instance variables.
        // Make HelpFormatter immutable for 2.0

        /**
         * A function to convert a description (not null) and a deprecated Option (not null) to help description
         */
        private static final Function<Option, String> DEFAULT_DEPRECATED_FORMAT = o -> "[Deprecated] " + getDescription(o);

        /**
         * Formatter for deprecated options.
         */
        private Function<Option, String> deprecatedFormatFunction = DEFAULT_DEPRECATED_FORMAT;

        /**
         * The output PrintWriter, defaults to wrapping {@link System#out}.
         */
        private PrintWriter printStream = createDefaultPrintWriter();

        /** The flag to determine if the since values should be dispalyed */
        private boolean showSince;

        @Override
        public HelpFormatter get() {
            return new HelpFormatter(deprecatedFormatFunction, printStream, showSince);
        }

        /**
         * Sets the output PrintWriter, defaults to wrapping {@link System#out}.
         *
         * @param printWriter the output PrintWriter, not null.
         * @return {@code this} instance.
         */
        public Builder setPrintWriter(final PrintWriter printWriter) {
            this.printStream = Objects.requireNonNull(printWriter, "printWriter");
            return this;
        }

        /**
         * Sets whether to show deprecated options.
         *
         * @param useDefaultFormat if {@code true} use the default format, otherwise clear the formatter.
         * @return {@code this} instance.
         */
        public Builder setShowDeprecated(final boolean useDefaultFormat) {
            return setShowDeprecated(useDefaultFormat ? DEFAULT_DEPRECATED_FORMAT : null);
        }

        /**
         * Sets whether to show deprecated options.
         *
         * @param deprecatedFormatFunction Specify the format for the deprecated options.
         * @return {@code this} instance.
         * @since 1.8.0
         */
        public Builder setShowDeprecated(final Function<Option, String> deprecatedFormatFunction) {
            this.deprecatedFormatFunction = deprecatedFormatFunction;
            return this;
        }

        /**
         * Sets whether to show the date the option was first added.
         * @param showSince if @{code true} the date the options was first added will be shown.
         * @return this builder.
         * @since 1.9.0
         */
        public Builder setShowSince(final boolean showSince) {
            this.showSince = showSince;
            return this;
        }
    }

    /**
     * This class implements the {@code Comparator} interface for comparing Options.
     * @deprecated use {@link org.apache.commons.cli.help.AbstractHelpFormatter#DEFAULT_COMPARATOR}
     */
    private static final class OptionComparator implements Comparator<Option>, Serializable {

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
    /** "Options" text for options header */
    private static final String HEADER_OPTIONS = "Options";

    /** "Since" text for options header */
    private static final String HEADER_SINCE = "Since";

    /** "Description" test for options header */
    private static final String HEADER_DESCRIPTION = "Description";

    /** Default number of characters per line */
    public static final int DEFAULT_WIDTH = 74;

    /** Default padding to the left of each line */
    public static final int DEFAULT_LEFT_PAD = 1;

    /** Number of space characters to be prefixed to each description line */
    public static final int DEFAULT_DESC_PAD = 3;

    /** The string to display at the beginning of the usage statement */
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

    /** Default prefix for shortOpts */
    public static final String DEFAULT_OPT_PREFIX = "-";

    /** Default prefix for long Option */
    public static final String DEFAULT_LONG_OPT_PREFIX = "--";

    /**
     * Default separator displayed between a long Option and its value
     *
     * @since 1.3
     */
    public static final String DEFAULT_LONG_OPT_SEPARATOR = " ";

    /** Default name for an argument */
    public static final String DEFAULT_ARG_NAME = "arg";

    /**
     * Creates a new builder.
     *
     * @return a new builder.
     * @since 1.7.0
     */
    public static Builder builder() {
        return new Builder();
    }

    private static PrintWriter createDefaultPrintWriter() {
        return new PrintWriter(System.out);
    }

    /**
     * Gets the option description or an empty string if the description is {@code null}.
     * @param option The option to get the description from.
     * @return the option description or an empty string if the description is {@code null}.
     * @since 1.8.0
     * @deprecated use {@link Util#defaultValue(CharSequence, CharSequence)}
     */
    @Deprecated
    public static String getDescription(final Option option) {
        final String desc = option.getDescription();
        return desc == null ? "" : desc;
    }

    /**
     * Number of characters per line
     *
     * @deprecated Scope will be made private for next major version - use get/setWidth methods instead.
     */
    @Deprecated
    public int defaultWidth = DEFAULT_WIDTH;

    /**
     * Amount of padding to the left of each line
     *
     * @deprecated Scope will be made private for next major version - use get/setLeftPadding methods instead.
     */
    @Deprecated
    public int defaultLeftPad = DEFAULT_LEFT_PAD;

    /**
     * The number of characters of padding to be prefixed to each description line
     *
     * @deprecated Scope will be made private for next major version - use get/setDescPadding methods instead.
     */
    @Deprecated
    public int defaultDescPad = DEFAULT_DESC_PAD;

    /**
     * The string to display at the beginning of the usage statement
     *
     * @deprecated Scope will be made private for next major version - use get/setSyntaxPrefix methods instead.
     */
    @Deprecated
    public String defaultSyntaxPrefix = DEFAULT_SYNTAX_PREFIX;

    /**
     * The new line string
     *
     * @deprecated Scope will be made private for next major version - use get/setNewLine methods instead.
     */
    @Deprecated
    public String defaultNewLine = System.lineSeparator();

    /**
     * The shortOpt prefix
     *
     * @deprecated Scope will be made private for next major version - use get/setOptPrefix methods instead.
     */
    @Deprecated
    public String defaultOptPrefix = DEFAULT_OPT_PREFIX;

    /**
     * The long Opt prefix
     *
     * @deprecated Scope will be made private for next major version - use get/setLongOptPrefix methods instead.
     */
    @Deprecated
    public String defaultLongOptPrefix = DEFAULT_LONG_OPT_PREFIX;

    /**
     * The name of the argument
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

    /**
     * Function to format the description for a deprecated option.
     */
    private final Function<Option, String> deprecatedFormatFunction;

    /**
     * Where to print help.
     */
    private final PrintWriter printWriter;

    /** Flag to determine if since field should be displayed */
    private final boolean showSince;

    /**
     * The separator displayed between the long option and its value.
     */
    private String longOptSeparator = DEFAULT_LONG_OPT_SEPARATOR;

    /**
     * Constructs a new instance.
     */
    public HelpFormatter() {
        this(null, createDefaultPrintWriter(), false);
    }

    /**
     * Constructs a new instance.
     * @param printWriter TODO
     */
    private HelpFormatter(final Function<Option, String> deprecatedFormatFunction, final PrintWriter printWriter, final boolean showSince) {
        // TODO All other instance HelpFormatter instance variables.
        // Make HelpFormatter immutable for 2.0
        this.deprecatedFormatFunction = deprecatedFormatFunction;
        this.printWriter = printWriter;
        this.showSince = showSince;
    }

    /**
     * Appends the usage clause for an Option to a StringBuffer.
     *
     * @param buff the StringBuffer to append to
     * @param option the Option to append
     * @param required whether the Option is required or not
     */
    private void appendOption(final StringBuilder buff, final Option option, final boolean required) {
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
     * @param buff the StringBuilder to append to
     * @param group the group to append
     * @see #appendOption(StringBuilder,Option,boolean)
     */
    private void appendOptionGroup(final StringBuilder buff, final OptionGroup group) {
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
     * Renders the specified Options and return the rendered Options in a StringBuffer.
     *
     * @param sb The StringBuffer to place the rendered Options into.
     * @param width The number of characters to display per line
     * @param options The command line Options
     * @param leftPad the number of characters of padding to be prefixed to each line
     * @param descPad the number of characters of padding to be prefixed to each description line
     * @return the StringBuffer with the rendered Options contents.
     * @throws IOException if an I/O error occurs.
     * @deprecated This method is replaced by the {@link org.apache.commons.cli.help.TableDefinition} and its use in
     * new help system.
     */
    @Deprecated
    <A extends Appendable> A appendOptions(final A sb, final int width, final Options options, final int leftPad, final int descPad) throws IOException {
        final String lpad = createPadding(leftPad);
        final String dpad = createPadding(descPad);
        // first create list containing only <lpad>-a,--aaa where
        // -a is opt and --aaa is long opt; in parallel look for
        // the longest opt string this list will be then used to
        // sort options ascending
        int max = 0;
        final int maxSince = showSince ? determineMaxSinceLength(options) + leftPad : 0;
        final List<StringBuilder> prefixList = new ArrayList<>();
        final List<Option> optList = options.helpOptions();
        if (getOptionComparator() != null) {
            Collections.sort(optList, getOptionComparator());
        }
        for (final Option option : optList) {
            final StringBuilder optBuf = new StringBuilder();
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
            max = Math.max(optBuf.length() + maxSince, max);
        }
        final int nextLineTabStop = max + descPad;
        if (showSince) {
            final StringBuilder optHeader = new StringBuilder(HEADER_OPTIONS).append(createPadding(max - maxSince - HEADER_OPTIONS.length() + leftPad))
                    .append(HEADER_SINCE);
            optHeader.append(createPadding(max - optHeader.length())).append(lpad).append(HEADER_DESCRIPTION);
            appendWrappedText(sb, width, nextLineTabStop, optHeader.toString());
            sb.append(getNewLine());
        }

        int x = 0;
        for (final Iterator<Option> it = optList.iterator(); it.hasNext();) {
            final Option option = it.next();
            final StringBuilder optBuf = new StringBuilder(prefixList.get(x++).toString());
            if (optBuf.length() < max) {
                optBuf.append(createPadding(max - maxSince - optBuf.length()));
                if (showSince) {
                    optBuf.append(lpad).append(option.getSince() == null ? "-" : option.getSince());
                }
                optBuf.append(createPadding(max - optBuf.length()));
            }
            optBuf.append(dpad);

            if (deprecatedFormatFunction != null && option.isDeprecated()) {
                optBuf.append(deprecatedFormatFunction.apply(option).trim());
            } else if (option.getDescription() != null) {
                optBuf.append(option.getDescription());
            }
            appendWrappedText(sb, width, nextLineTabStop, optBuf.toString());
            if (it.hasNext()) {
                sb.append(getNewLine());
            }
        }
        return sb;
    }

    /**
     * Renders the specified text and return the rendered Options in a StringBuffer.
     *
     * @param <A> The Appendable implementation.
     * @param appendable The StringBuffer to place the rendered text into.
     * @param width The number of characters to display per line
     * @param nextLineTabStop The position on the next line for the first tab.
     * @param text The text to be rendered.
     * @return the StringBuffer with the rendered Options contents.
     * @throws IOException if an I/O error occurs.
     * @deprecated this method is replaced by {@link org.apache.commons.cli.help.TextHelpWriter#printWrapped(String, TextStyle)}
     */
    @Deprecated
    <A extends Appendable> A appendWrappedText(final A appendable, final int width, final int nextLineTabStop, final String text) throws IOException {
        String render = text;
        int nextLineTabStopPos = nextLineTabStop;
        int pos = findWrapPos(render, width, 0);
        if (pos == -1) {
            appendable.append(rtrim(render));
            return appendable;
        }
        appendable.append(rtrim(render.substring(0, pos))).append(getNewLine());
        if (nextLineTabStopPos >= width) {
            // stops infinite loop happening
            nextLineTabStopPos = 1;
        }
        // all following lines must be padded with nextLineTabStop space characters
        final String padding = createPadding(nextLineTabStopPos);
        while (true) {
            render = padding + render.substring(pos).trim();
            pos = findWrapPos(render, width, 0);
            if (pos == -1) {
                appendable.append(render);
                return appendable;
            }
            if (render.length() > width && pos == nextLineTabStopPos - 1) {
                pos = width;
            }
            appendable.append(rtrim(render.substring(0, pos))).append(getNewLine());
        }
    }

    /**
     * Creates a String of padding of length {@code len}.
     *
     * @param len The length of the String of padding to create.
     *
     * @return The String of padding
     * @deprecated use {@link Util#createPadding(int)}.
     */
    @Deprecated
    protected String createPadding(final int len) {
        return Util.createPadding(len);
    }

    private int determineMaxSinceLength(final Options options) {
        final int minLen = HEADER_SINCE.length();
        final int len = options.getOptions().stream().map(o -> o.getSince() == null ? minLen : o.getSince().length()).max(Integer::compareTo).orElse(minLen);
        return len < minLen ? minLen : len;
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
     * @deprecated use {@link Util#findWrapPos(CharSequence, int, int)}
     */
    @Deprecated
    protected int findWrapPos(final String text, final int width, final int startPos) {
        // the line ends before the max wrap pos or a new line char found
        int pos = text.indexOf(Char.LF, startPos);
        if (pos != -1 && pos <= width) {
            return pos + 1;
        }
        pos = text.indexOf(Char.TAB, startPos);
        if (pos != -1 && pos <= width) {
            return pos + 1;
        }
        if (startPos + width >= text.length()) {
            return -1;
        }
        // look for the last whitespace character before startPos+width
        for (pos = startPos + width; pos >= startPos; --pos) {
            final char c = text.charAt(pos);
            if (c == Char.SP || c == Char.LF || c == Char.CR) {
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
     * @deprecated use {@link OptionFormatter#getArgName()} or {@link OptionFormatter.Builder#getArgName()}
     */
    @Deprecated
    public String getArgName() {
        return defaultArgName;
    }

    /**
     * Gets the 'descPadding'.
     *
     * @return the 'descPadding'
     * @deprecated text formatting is now handled by {@link org.apache.commons.cli.help.TextStyle}
     */
    @Deprecated
    public int getDescPadding() {
        return defaultDescPad;
    }

    /**
     * Gets the 'leftPadding'.
     *
     * @return the 'leftPadding'
     * @deprecated text formatting is now handled by {@link org.apache.commons.cli.help.TextStyle}
     */
    @Deprecated
    public int getLeftPadding() {
        return defaultLeftPad;
    }

    /**
     * Gets the 'longOptPrefix'.
     *
     * @return the 'longOptPrefix'
     * @deprecated Use {@link OptionFormatter#getLongOpt()}
     */
    @Deprecated
    public String getLongOptPrefix() {
        return defaultLongOptPrefix;
    }

    /**
     * Gets the separator displayed between a long option and its value.
     *
     * @return the separator
     * @since 1.3
     * @deprecated text formatting is now handled by {@link org.apache.commons.cli.help.TextStyle}
     */
    @Deprecated
    public String getLongOptSeparator() {
        return longOptSeparator;
    }

    /**
     * Gets the 'newLine'.
     *
     * @return the 'newLine'
     * @deprecated text output is now handled by {@link org.apache.commons.cli.help.TextHelpWriter}
     */
    @Deprecated
    public String getNewLine() {
        return defaultNewLine;
    }

    /**
     * Comparator used to sort the options when they output in help text. Defaults to case-insensitive alphabetical sorting
     * by option key.
     *
     * @return the {@link Comparator} currently in use to sort the options
     * @since 1.2
     * @deprecated use {@link AbstractHelpFormatter#getComparator()}
     */
    @Deprecated
    public Comparator<Option> getOptionComparator() {
        return optionComparator;
    }

    /**
     * Gets the 'optPrefix'.
     *
     * @return the 'optPrefix'
     * @deprecated use {@link OptionFormatter#getOpt()}
     */
    @Deprecated
    public String getOptPrefix() {
        return defaultOptPrefix;
    }

    /**
     * Gets the 'syntaxPrefix'.
     *
     * @return the 'syntaxPrefix'
     * @deprecated use {@link AbstractHelpFormatter#getSyntaxPrefix()}
     */
    @Deprecated
    public String getSyntaxPrefix() {
        return defaultSyntaxPrefix;
    }

    /**
     * Gets the 'width'.
     *
     * @return the 'width'
     * @deprecated text formatting is now handled by {@link org.apache.commons.cli.help.TextStyle}
     */
    @Deprecated
    public int getWidth() {
        return defaultWidth;
    }

    /**
     * Prints the help for {@code options} with the specified command line syntax. This method prints help information
     * to  {@link System#out}  by default.
     *
     * @param width the number of characters to be displayed on each line
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param footer the banner to display at the end of the help
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#printHelp(String, String, Options, String, boolean)}
     */
    @Deprecated
    public void printHelp(final int width, final String cmdLineSyntax, final String header, final Options options, final String footer) {
        printHelp(width, cmdLineSyntax, header, options, footer, false);
    }

    /**
     * Prints the help for {@code options} with the specified command line syntax. This method prints help information
     * to {@link System#out} by default.
     *
     * @param width the number of characters to be displayed on each line
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#printHelp(String, String, Options, String, boolean)}
     */
    @Deprecated
    public void printHelp(final int width, final String cmdLineSyntax, final String header, final Options options, final String footer,
        final boolean autoUsage) {
        final PrintWriter pw = new PrintWriter(printWriter);
        printHelp(pw, width, cmdLineSyntax, header, options, getLeftPadding(), getDescPadding(), footer, autoUsage);
        pw.flush();
    }

    /**
     * Prints the help for {@code options} with the specified command line syntax.
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
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#printHelp(String, String, Options, String, boolean)}
     */
    @Deprecated
    public void printHelp(final PrintWriter pw, final int width, final String cmdLineSyntax, final String header, final Options options, final int leftPad,
        final int descPad, final String footer) {
        printHelp(pw, width, cmdLineSyntax, header, options, leftPad, descPad, footer, false);
    }

    /**
     * Prints the help for {@code options} with the specified command line syntax.
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
     * @throws IllegalStateException if there is no room to print a line
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#printHelp(String, String, Options, String, boolean)}
     */
    @Deprecated
    public void printHelp(final PrintWriter pw, final int width, final String cmdLineSyntax, final String header, final Options options, final int leftPad,
        final int descPad, final String footer, final boolean autoUsage) {
        if (Util.isEmpty(cmdLineSyntax)) {
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
     * Prints the help for {@code options} with the specified command line syntax. This method prints help information
     * to {@link System#out} by default.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param options the Options instance
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#printHelp(String, String, Options, String, boolean)}
     */
    @Deprecated
    public void printHelp(final String cmdLineSyntax, final Options options) {
        printHelp(getWidth(), cmdLineSyntax, null, options, null, false);
    }

    /**
     * Prints the help for {@code options} with the specified command line syntax. This method prints help information
     * to {@link System#out} by default.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param options the Options instance
     * @param autoUsage whether to print an automatically generated usage statement
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#printHelp(String, String, Options, String, boolean)}
     */
    @Deprecated
    public void printHelp(final String cmdLineSyntax, final Options options, final boolean autoUsage) {
        printHelp(getWidth(), cmdLineSyntax, null, options, null, autoUsage);
    }

    /**
     * Prints the help for {@code options} with the specified command line syntax. This method prints help information
     * to {@link System#out} by default.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param footer the banner to display at the end of the help
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#printHelp(String, String, Options, String, boolean)}
     */
    @Deprecated
    public void printHelp(final String cmdLineSyntax, final String header, final Options options, final String footer) {
        printHelp(cmdLineSyntax, header, options, footer, false);
    }

    /**
     * Prints the help for {@code options} with the specified command line syntax. This method prints help information
     * to {@link System#out} by default.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#printHelp(String, String, Options, String, boolean)}
     */
    @Deprecated
    public void printHelp(final String cmdLineSyntax, final String header, final Options options, final String footer, final boolean autoUsage) {
        printHelp(getWidth(), cmdLineSyntax, header, options, footer, autoUsage);
    }

    /**
     * Prints the help for the specified Options to the specified writer, using the specified width, left padding and
     * description padding.
     *
     * @param pw The printWriter to write the help to
     * @param width The number of characters to display per line
     * @param options The command line Options
     * @param leftPad the number of characters of padding to be prefixed to each line
     * @param descPad the number of characters of padding to be prefixed to each description line
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#printOptions(Options)}
     */
    @Deprecated
    public void printOptions(final PrintWriter pw, final int width, final Options options, final int leftPad, final int descPad) {
        try {
            pw.println(appendOptions(new StringBuilder(), width, options, leftPad, descPad));
        } catch (final IOException e) {
            // Cannot happen
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Prints the cmdLineSyntax to the specified writer, using the specified width.
     *
     * @param pw The printWriter to write the help to
     * @param width The number of characters per line for the usage statement.
     * @param cmdLineSyntax The usage statement.
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#getSyntaxPrefix()} and
     * {@link org.apache.commons.cli.help.TextHelpWriter#printWrapped(String)}
     */
    @Deprecated
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
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter#getSyntaxPrefix()} and
     * {@link org.apache.commons.cli.help.TextHelpWriter#printWrapped(String)}
     */
    @Deprecated
    public void printUsage(final PrintWriter pw, final int width, final String app, final Options options) {
        // initialize the string buffer
        final StringBuilder buff = new StringBuilder(getSyntaxPrefix()).append(app).append(Char.SP);
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
                buff.append(Char.SP);
            }
        }

        // call printWrapped
        printWrapped(pw, width, buff.toString().indexOf(' ') + 1, buff.toString());
    }

    /**
     * Prints the specified text to the specified PrintWriter.
     *
     * @param pw The printWriter to write the help to
     * @param width The number of characters to display per line
     * @param nextLineTabStop The position on the next line for the first tab.
     * @param text The text to be written to the PrintWriter
     * @deprecated use {@link org.apache.commons.cli.help.TextHelpWriter#printWrapped(String)}
     */
    @Deprecated
    public void printWrapped(final PrintWriter pw, final int width, final int nextLineTabStop, final String text) {
        pw.println(renderWrappedTextBlock(new StringBuilder(text.length()), width, nextLineTabStop, text));
    }

    /**
     * Prints the specified text to the specified PrintWriter.
     *
     * @param pw The printWriter to write the help to
     * @param width The number of characters to display per line
     * @param text The text to be written to the PrintWriter
     * @deprecated use {@link org.apache.commons.cli.help.TextHelpWriter#printWrapped(String)}
     */
    @Deprecated    public void printWrapped(final PrintWriter pw, final int width, final String text) {
        printWrapped(pw, width, 0, text);
    }

    /**
     * Renders the specified Options and return the rendered Options in a StringBuffer.
     *
     * @param sb The StringBuffer to place the rendered Options into.
     * @param width The number of characters to display per line
     * @param options The command line Options
     * @param leftPad the number of characters of padding to be prefixed to each line
     * @param descPad the number of characters of padding to be prefixed to each description line
     *
     * @return the StringBuffer with the rendered Options contents.
     * @deprecated Options tables are defined in {@link org.apache.commons.cli.help.TableDefinition}
     */
    @Deprecated
    protected StringBuffer renderOptions(final StringBuffer sb, final int width, final Options options, final int leftPad, final int descPad) {
        try {
            return appendOptions(sb, width, options, leftPad, descPad);
        } catch (final IOException e) {
            // Cannot happen
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Renders the specified text and return the rendered Options in a StringBuffer.
     *
     * @param sb The StringBuffer to place the rendered text into.
     * @param width The number of characters to display per line
     * @param nextLineTabStop The position on the next line for the first tab.
     * @param text The text to be rendered.
     *
     * @return the StringBuffer with the rendered Options contents.
     * @deprecated use {@link org.apache.commons.cli.help.TextHelpWriter#printWrapped(String)}
     */
    @Deprecated
    protected StringBuffer renderWrappedText(final StringBuffer sb, final int width, final int nextLineTabStop, final String text) {
        try {
            return appendWrappedText(sb, width, nextLineTabStop, text);
        } catch (final IOException e) {
            // Cannot happen.
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Renders the specified text width a maximum width. This method differs from renderWrappedText by not removing leading
     * spaces after a new line.
     *
     * @param appendable The StringBuffer to place the rendered text into.
     * @param width The number of characters to display per line
     * @param nextLineTabStop The position on the next line for the first tab.
     * @param text The text to be rendered.
     */
    private <A extends Appendable> A renderWrappedTextBlock(final A appendable, final int width, final int nextLineTabStop, final String text) {
        try {
            final BufferedReader in = new BufferedReader(new StringReader(text));
            String line;
            boolean firstLine = true;
            while ((line = in.readLine()) != null) {
                if (!firstLine) {
                    appendable.append(getNewLine());
                } else {
                    firstLine = false;
                }
                appendWrappedText(appendable, width, nextLineTabStop, line);
            }
        } catch (final IOException e) { // NOPMD
            // cannot happen
        }
        return appendable;
    }

    /**
     * Removes the trailing whitespace from the specified String.
     *
     * @param s The String to remove the trailing padding from.
     * @return The String of without the trailing padding
     * @deprecated use {@link Util#rtrim(String)}
     */
    @Deprecated
    protected String rtrim(final String s) {
        if (Util.isEmpty(s)) {
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
     * @deprecated use {@link OptionFormatter.Builder#setDefaultArgName(String)}
     */
    @Deprecated
    public void setArgName(final String name) {
        this.defaultArgName = name;
    }

    /**
     * Sets the 'descPadding'.
     *
     * @param padding the new value of 'descPadding'
     * @deprecated use {@link TextStyle.Builder#setIndent(int)}
     */
    @Deprecated
    public void setDescPadding(final int padding) {
        this.defaultDescPad = padding;
    }

    /**
     * Sets the 'leftPadding'.
     *
     * @param padding the new value of 'leftPadding'
     * @deprecated use {@link TextStyle.Builder#setLeftPad(int)}
     */
    @Deprecated
    public void setLeftPadding(final int padding) {
        this.defaultLeftPad = padding;
    }

    /**
     * Sets the 'longOptPrefix'.
     *
     * @param prefix the new value of 'longOptPrefix'
     * @deprecated use {@link OptionFormatter.Builder#setLongOptPrefix(String)}
     */
    @Deprecated
    public void setLongOptPrefix(final String prefix) {
        this.defaultLongOptPrefix = prefix;
    }

    /**
     * Sets the separator displayed between a long option and its value. Ensure that the separator specified is supported by
     * the parser used, typically ' ' or '='.
     *
     * @param longOptSeparator the separator, typically ' ' or '='.
     * @since 1.3
     * @deprecated use {@link OptionFormatter.Builder#setOptArgSeparator(String)}
     */
    @Deprecated
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
     * Sets the comparator used to sort the options when they output in help text. Passing in a null comparator will keep the
     * options in the order they were declared.
     *
     * @param comparator the {@link Comparator} to use for sorting the options
     * @since 1.2
     * @deprecated use {@link org.apache.commons.cli.help.HelpFormatter.Builder#setComparator(Comparator)}
     */
    @Deprecated
    public void setOptionComparator(final Comparator<Option> comparator) {
        this.optionComparator = comparator;
    }

    /**
     * Sets the 'optPrefix'.
     *
     * @param prefix the new value of 'optPrefix'
     * @deprecated use {@link OptionFormatter.Builder#setOptPrefix(String)}
     */
    @Deprecated
    public void setOptPrefix(final String prefix) {
        this.defaultOptPrefix = prefix;
    }

    /**
     * Sets the 'syntaxPrefix'.
     *
     * @param prefix the new value of 'syntaxPrefix'
     * @deprecated use {@link AbstractHelpFormatter#setSyntaxPrefix(String)}
     */
    @Deprecated
    public void setSyntaxPrefix(final String prefix) {
        this.defaultSyntaxPrefix = prefix;
    }

    /**
     * Sets the 'width'.
     *
     * @param width the new value of 'width'
     * @deprecated text formatting is now handled by {@link org.apache.commons.cli.help.TextStyle}
     */
    @Deprecated
    public void setWidth(final int width) {
        this.defaultWidth = width;
    }

}
