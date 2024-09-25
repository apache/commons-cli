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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

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
 */
public class HelpFormatter extends AbstractHelpFormatter {
    /** Default number of characters per line */
    public static final int DEFAULT_WIDTH = 74;

    /** Default padding to the left of each line */
    public static final int DEFAULT_LEFT_PAD = 1;

    /** Number of space characters to be prefixed to each description line */
    public static final int DEFAULT_INDENT = 3;

    public static final int DEFAULT_COLUMN_SPACING = 5;

    private boolean showSince;

    public static class Builder {
        private boolean showSince;
        private Serializer serializer;
        private OptionFormatter.Builder optionFormatBuilder;
        private Function<Iterable<Option>, TableDef> defaultTableBuilder;

        public Builder() {
            showSince = true;
            serializer = null;
            optionFormatBuilder = new OptionFormatter.Builder();
            defaultTableBuilder = null;
        }

        public Builder setShowSince(boolean showSince) {
            this.showSince = showSince;
            return this;
        }

        public Builder setSerializer(Serializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder setOptionFormatBuilder(OptionFormatter.Builder optionFormatBuilder) {
            this.optionFormatBuilder = optionFormatBuilder;
            return this;
        }

        public Builder setDefaultTableBuilder(Function<Iterable<Option>, TableDef> defaultTableBuilder) {
            this.defaultTableBuilder = defaultTableBuilder;
            return this;
        }

        private Builder sanityCheck() {
            if (serializer == null) {
                serializer = new TextSerializer(System.out);
            }
            if (optionFormatBuilder == null) {
                optionFormatBuilder = new OptionFormatter.Builder();
            }
            return this;
        }

        public HelpFormatter build() {
            sanityCheck();
            return new HelpFormatter(this);
        }
    }

    /**
     * Constructs a new instance.
     */
    public HelpFormatter() {
        this(new Builder().sanityCheck());
    }

    public HelpFormatter(Serializer serializer) {
        this(new Builder().setSerializer(serializer).sanityCheck());
    }

    private HelpFormatter(Builder builder) {
        super(builder.serializer, builder.optionFormatBuilder, builder.defaultTableBuilder);
        this.showSince = builder.showSince;
        if (this.tableDefBuilder == null) {
            tableDefBuilder = this::defaultTableBuilder;
        }
    }

    public void setShowSince(boolean showSince) {
        this.showSince = showSince;
    }

    public TableDef defaultTableBuilder(Iterable<Option> options) {
        TextStyle.Builder builder = new TextStyle.Builder().setAlignment(TextStyle.Alignment.LEFT)
                .setIndent(DEFAULT_LEFT_PAD).setScaling(TextStyle.Scaling.FIXED);
        List<TextStyle> styles = new ArrayList<>();
        styles.add(builder.get());
        builder.setScaling(TextStyle.Scaling.VARIABLE).setLeftPad(DEFAULT_COLUMN_SPACING);
        if (showSince) {
            builder.setAlignment(TextStyle.Alignment.CENTER);
            styles.add(builder.get());
        }
        builder.setAlignment(TextStyle.Alignment.LEFT);
        styles.add(builder.get());

        List<List<String>> rows = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (Option option : options) {
            List<String> row = new ArrayList<>();
            OptionFormatter formatter = optionFormatBuilder.build(option);
            sb.setLength(0);
            sb.append(formatter.getBothOpt());
            if (option.hasArg()) {
                sb.append(" ").append(formatter.getArgName());
            }
            row.add(sb.toString());
            if (showSince) {
                row.add(formatter.getSince());
            }
            row.add(option.isDeprecated() ? formatter.getDeprecated() : formatter.getDescription());
            rows.add(row);
        }

        return showSince ? TableDef.from("", styles, Arrays.asList("Options", "Since", "Description"), rows) :
                TableDef.from("", styles, Arrays.asList("Options", "Description"), rows);
    }
}
