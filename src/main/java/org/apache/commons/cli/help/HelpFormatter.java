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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;

/**
 * A default formatter implementation for standard usage.
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
 * String header = "Do something useful with an input file";
 * String footer = "Please report issues at https://example.com/issues";
 *
 * HelpFormatter formatter = new HelpFormatter();
 * formatter.printHelp("myapp", header, options, footer, true);
 * </pre>
 * <p>
 * This produces the following output:
 * </p>
 *
 * <pre>
 *     {@code
 * usage: myapp -f <FILE> [-h] [-v]
 * Do something useful with an input file
 *
 *  -f,--file <FILE>   The file to be processed
 *  -h,--help
 *  -v,--version       Print the version of the application
 *
 * Please report issues at https://example.com/issues
 * }
 * </pre>
 *
 * @since 1.10.0
 */
public class HelpFormatter extends AbstractHelpFormatter {

    /**
     * A builder for the HelpFormatter. Intended to make more complex uses of the HelpFormatter class easier. Default values are:
     * <ul>
     * <li>showSince = true</li>
     * <li>helpAppendable = a {@link TextHelpAppendable} writing to {@code System.out}</li>
     * <li>optionFormatter.Builder = the default {@link OptionFormatter.Builder}</li>
     * </ul>
     */
    public static class Builder extends AbstractHelpFormatter.Builder<Builder, HelpFormatter> {

        /** If {@code true} show the "Since" column, otherwise ignore it. */
        private boolean showSince = true;

        /**
         * Constructs a new instace.
         * <p>
         * Sets {@code showSince} to {@code true}.
         * </p>
         */
        protected Builder() {
            // empty
        }

        /**
         * Sets the showSince flag.
         *
         * @param showSince the desired value of the showSince flag.
         * @return this.
         */
        public Builder setShowSince(final boolean showSince) {
            this.showSince = showSince;
            return this;
        }

        @Override
        public HelpFormatter get() {
            return new HelpFormatter(this);
        }
    }

    /** Default number of characters per line */
    public static final int DEFAULT_WIDTH = 74;

    /** Default padding to the left of each line */
    public static final int DEFAULT_LEFT_PAD = 1;

    /** The default number of spaces between columns in the options table */
    public static final int DEFAULT_COLUMN_SPACING = 5;

    /**
     * Constructs a new builder.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /** If {@code true} show the "Since" column, otherwise ignore it. */
    private final boolean showSince;

    /**
     * Constructs the Help formatter.
     *
     * @param builder the Builder to build from.
     */
    protected HelpFormatter(final Builder builder) {
        super(builder.getHelpAppendable(), builder.getOptionFormatBuilder(), builder.getComparator(), builder.getOptionGroupSeparator());
        this.showSince = builder.showSince;
    }

    /**
     * Gets the table definition for the options.
     *
     * @param options the collection of {@link Option} instances to create the table from.
     * @return A {@link TableDefinition} to display the options.
     */
    @Override
    public TableDefinition getTableDefinition(final Iterable<Option> options) {
        // set up the base TextStyle for the columns configured for the Option opt and arg values..
        final TextStyle.Builder builder = TextStyle.builder().setAlignment(TextStyle.Alignment.LEFT).setIndent(DEFAULT_LEFT_PAD).setScalable(false);
        final List<TextStyle> styles = new ArrayList<>();
        styles.add(builder.get());
        // set up showSince column
        builder.setScalable(true).setLeftPad(DEFAULT_COLUMN_SPACING);
        if (showSince) {
            builder.setAlignment(TextStyle.Alignment.CENTER);
            styles.add(builder.get());
        }
        // set up the description column.
        builder.setAlignment(TextStyle.Alignment.LEFT);
        styles.add(builder.get());
        // setup the rows for the table.
        final List<List<String>> rows = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();
        for (final Option option : options) {
            final List<String> row = new ArrayList<>();
            // create an option formatter to correctly format the parts of the option
            final OptionFormatter formatter = optionFormatBuilder.build(option);
            sb.setLength(0);
            // append the opt values.
            sb.append(formatter.getBothOpt());
            // append the arg name if it exists.
            if (option.hasArg()) {
                sb.append(" ").append(formatter.getArgName());
            }
            row.add(sb.toString());
            // append the "since" value if desired.
            if (showSince) {
                row.add(formatter.getSince());
            }
            // add the option description
            row.add(formatter.getDescription());
            rows.add(row);
        }
        // return the TableDefinition with the proper column headers.
        return TableDefinition.from("", styles, showSince ? Arrays.asList("Options", "Since", "Description") : Arrays.asList("Options", "Description"), rows);
    }
}
