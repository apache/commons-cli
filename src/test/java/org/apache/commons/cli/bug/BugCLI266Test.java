/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli.bug;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.Test;

class BugCLI266Test {

    private final List<String> insertedOrder = Arrays.asList("h", "d", "f", "x", "s", "p", "t", "w", "o");
    private final List<String> sortOrder = Arrays.asList("d", "f", "h", "o", "p", "s", "t", "w", "x");

    private void buildOptionsGroup(final Options options) {
        final OptionGroup optionGroup1 = new OptionGroup();
        final OptionGroup optionGroup2 = new OptionGroup();
        optionGroup1.setRequired(true);
        optionGroup2.setRequired(true);

        //@formatter:off
        optionGroup1.addOption(Option.builder("d")
        .longOpt("db")
        .hasArg()
        .argName("table-name").get());
        optionGroup1.addOption(Option.builder("f")
        .longOpt("flat-file")
        .hasArg()
        .argName("input.csv").get());
        //@formatter:on
        options.addOptionGroup(optionGroup1);
        //@formatter:off
        optionGroup2.addOption(Option.builder("x")
        .hasArg()
        .argName("arg1").get());
        optionGroup2.addOption(Option.builder("s").get());
        optionGroup2.addOption(Option.builder("p")
        .hasArg()
        .argName("arg1").get());
        //@formatter:on
        options.addOptionGroup(optionGroup2);
    }

    private Options getOptions() {
        final Options options = new Options();
        //@formatter:off
        final Option help = Option.builder("h")
        .longOpt("help")
        .desc("Prints this help message").get();
        //@formatter:on
        options.addOption(help);

        buildOptionsGroup(options);

        //@formatter:off
        final Option t = Option.builder("t")
        .required()
        .hasArg()
        .argName("file").get();
        final Option w = Option.builder("w")
        .required()
        .hasArg()
        .argName("word").get();
        final Option o = Option.builder("o")
        .hasArg()
        .argName("directory").get();
        //@formatter:on
        options.addOption(t);
        options.addOption(w);
        options.addOption(o);
        return options;
    }

    @Test
    void testOptionComparatorDefaultOrder() {
        final HelpFormatter formatter = new HelpFormatter();
        final List<Option> options = new ArrayList<>(getOptions().getOptions());
        Collections.sort(options, formatter.getOptionComparator());
        int i = 0;
        for (final Option o : options) {
            assertEquals(o.getOpt(), sortOrder.get(i));
            i++;
        }
    }

    @Test
    void testOptionComparatorInsertedOrder() {
        final Collection<Option> options = getOptions().getOptions();
        int i = 0;
        for (final Option o : options) {
            assertEquals(o.getOpt(), insertedOrder.get(i));
            i++;
        }
    }
}
