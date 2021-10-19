/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli.bug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.junit.Assert;
import org.junit.Test;

public class BugCLI266Test {

    private final List<String> insertedOrder = Arrays.asList("h", "d", "f", "x", "s", "p", "t", "w", "o");
    private final List<String> sortOrder = Arrays.asList("d", "f", "h", "o", "p", "s", "t", "w", "x");

    private void buildOptionsGroup(final Options options) {
        final OptionGroup firstGroup = new OptionGroup();
        final OptionGroup secondGroup = new OptionGroup();
        firstGroup.setRequired(true);
        secondGroup.setRequired(true);

        //@formatter:off
        firstGroup.addOption(Option.builder("d")
                .longOpt("db")
                .hasArg()
                .argName("table-name")
                .build());
        firstGroup.addOption(Option.builder("f")
                .longOpt("flat-file")
                .hasArg()
                .argName("input.csv")
                .build());
        //@formatter:on
        options.addOptionGroup(firstGroup);
        //@formatter:off
        secondGroup.addOption(Option.builder("x")
                .hasArg()
                .argName("arg1")
                .build());
        secondGroup.addOption(Option.builder("s")
                .build());
        secondGroup.addOption(Option.builder("p")
                .hasArg()
                .argName("arg1")
                .build());
        //@formatter:on
        options.addOptionGroup(secondGroup);
    }

    private Options getOptions() {
        final Options options = new Options();
        //@formatter:off
        final Option help = Option.builder("h")
                .longOpt("help")
                .desc("Prints this help message")
                .build();
        //@formatter:on
        options.addOption(help);

        buildOptionsGroup(options);

        //@formatter:off
        final Option t = Option.builder("t")
                .required()
                .hasArg()
                .argName("file")
                .build();
        final Option w = Option.builder("w")
                .required()
                .hasArg()
                .argName("word")
                .build();
        final Option o = Option.builder("o")
                .hasArg()
                .argName("directory")
                .build();
        //@formatter:on
        options.addOption(t);
        options.addOption(w);
        options.addOption(o);
        return options;
    }

    @Test
    public void testOptionComparatorDefaultOrder() {
        final HelpFormatter formatter = new HelpFormatter();
        final List<Option> options = new ArrayList<>(getOptions().getOptions());
        Collections.sort(options, formatter.getOptionComparator());
        int i = 0;
        for (final Option o : options) {
            Assert.assertEquals(o.getOpt(), sortOrder.get(i));
            i++;
        }
    }

    @Test
    public void testOptionComparatorInsertedOrder() {
        final Collection<Option> options = getOptions().getOptions();
        int i = 0;
        for (final Option o : options) {
            Assert.assertEquals(o.getOpt(), insertedOrder.get(i));
            i++;
        }
    }
}
