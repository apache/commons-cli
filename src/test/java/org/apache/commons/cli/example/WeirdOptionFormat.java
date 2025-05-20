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
package org.apache.commons.cli.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.cli.help.TextStyle;
import org.apache.commons.lang3.StringUtils;

public class WeirdOptionFormat implements Function<Iterable<Option>, TableDefinition> {
    private final String[] headers = { "Opt", "Since", "Required", "LongOpt", "Deprecated", "Arg Name", "Type", "Description" };

    private final List<TextStyle> styles;

    public WeirdOptionFormat() {
        styles = new ArrayList<>();
        final TextStyle.Builder builder = TextStyle.builder();
        styles.add(builder.setLeftPad(1).setIndent(3).get());
        styles.add(builder.setLeftPad(5).get());
        styles.add(builder.get());
        styles.add(builder.get());
        styles.add(builder.get());
        styles.add(builder.get());
        styles.add(builder.get());
        styles.add(builder.get());
    }

    @Override
    public TableDefinition apply(final Iterable<Option> options) {
        final List<List<String>> rows = new ArrayList<>();
        for (final Option option : options) {
            final List<String> row = new ArrayList<>();
            row.add(option.getOpt());
            row.add(StringUtils.defaultIfEmpty(option.getSince(), "--"));
            row.add(option.isRequired() ? "T" : "F");
            row.add(option.getLongOpt());
            row.add(option.isDeprecated() ? "T" : "F");
            row.add(option.hasArg() ? option.getArgName() : "--");
            row.add(option.getType() == null ? "--" : option.getValue().toString());
            row.add(option.getDescription());
            rows.add(row);
        }
        return TableDefinition.from("", styles, Arrays.asList(headers), rows);
    }
}
