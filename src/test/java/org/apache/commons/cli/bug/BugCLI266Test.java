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

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BugCLI266Test {

    private final List<String> insertedOrder  =   Arrays.asList("h", "d", "f", "x", "s", "p", "t", "w", "o");
    private final List<String> sortOrder      =   Arrays.asList("d", "f", "h", "o", "p", "s", "t", "w", "x");

    @Test
    public void testOptionComparatorDefaultOrder() throws ParseException {
        final HelpFormatter formatter = new HelpFormatter();
        final List<Option> options = new ArrayList<>(getOptions().getOptions());
        Collections.sort(options, formatter.getOptionComparator());
        int i = 0;
        for(final Option o: options) {
            Assert.assertEquals(o.getOpt(), sortOrder.get(i));
            i++;
        }
    }

    @Test
    public void testOptionComparatorInsertedOrder() throws ParseException {
        final Collection<Option> options = getOptions().getOptions();
        int i = 0;
        for(final Option o: options) {
            Assert.assertEquals(o.getOpt(), insertedOrder.get(i));
            i++;
        }
    }

    private Options getOptions() {
        final Options options = new Options();
        final Option help = Option.builder("h")
                .longOpt("help")
                .desc("Prints this help message")
                .build();
        options.addOption(help);

        buildOptionsGroup(options);

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
        options.addOption(t);
        options.addOption(w);
        options.addOption(o);
        return options;
    }

    private void buildOptionsGroup(final Options options) {
        final OptionGroup firstGroup = new OptionGroup();
        final OptionGroup secondGroup = new OptionGroup();
        firstGroup.setRequired(true);
        secondGroup.setRequired(true);

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
        options.addOptionGroup(firstGroup);

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
        options.addOptionGroup(secondGroup);
    }
}
