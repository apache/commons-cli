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
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptionFormatterTest {

    @Test
    public void asSyntaxOptionTest() throws IOException {
        OptionFormatter underTest;

        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        underTest = OptionFormatter.from(option);
        assertEquals("[-o <arg>]", underTest.asSyntaxOption(), "optional arg failed");

        option = Option.builder().option("o").longOpt("opt").hasArg().argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("[-o <other>]", underTest.asSyntaxOption(), "optional 'other' arg failed");

        option = Option.builder().option("o").longOpt("opt").hasArg().required().argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("-o <other>", underTest.asSyntaxOption(), "required 'other' arg failed");

        option = Option.builder().option("o").longOpt("opt").required().argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("-o", underTest.asSyntaxOption(), "required no arg failed");

        option = Option.builder().option("o").argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("[-o]", underTest.asSyntaxOption(), "optional no arg arg failed");

        option = Option.builder().longOpt("opt").hasArg().argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("[--opt <other>]", underTest.asSyntaxOption(), "optional longOpt 'other' arg failed");

        option = Option.builder().longOpt("opt").required().hasArg().argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("--opt <other>", underTest.asSyntaxOption(), "required longOpt 'other' arg failed");

        option = Option.builder().option("ot").longOpt("opt").hasArg().build();
        underTest = OptionFormatter.from(option);
        assertEquals("[-ot <arg>]", underTest.asSyntaxOption(), "optional multi char opt arg failed");
    }

    @Test
    public void asSyntaxOptionGroupTest() throws IOException {
        OptionFormatter.Builder builder = new OptionFormatter.Builder();
        OptionGroup group = new OptionGroup()
                .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
                .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
                .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build())
                .addOption(Option.builder().option("f").argName("other").build())
                .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
                .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
                .addOption(Option.builder().option("s").longOpt("sevem").hasArg().build());
        assertEquals("[-f | --five <other> | -o <arg> | -s <arg> | --six <other> | -t <other> | -th]",
                OptionFormatter.asSyntaxOptions(builder, group));

        group.setRequired(true);
        assertEquals("-f | --five <other> | -o <arg> | -s <arg> | --six <other> | -t <other> | -th",
                OptionFormatter.asSyntaxOptions(builder, group));
    }

    @Test
    public void asSyntaxOptionOptionsTest() throws IOException {
        OptionFormatter.Builder builder = new OptionFormatter.Builder();
        Options options = new Options()
                .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
                .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
                .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build())
                .addOption(Option.builder().option("f").argName("other").build())
                .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
                .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
                .addOption(Option.builder().option("s").longOpt("seven").hasArg().build());
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> -t <other> -th",
                OptionFormatter.asSyntaxOptions(builder, options),
                "generic options failed");

        options = new Options()
                .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
                .addOptionGroup(
                        new OptionGroup()
                                .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
                                .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build()))
                .addOption(Option.builder().option("f").argName("other").build())
                .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
                .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
                .addOption(Option.builder().option("s").longOpt("seven").hasArg().build());
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> [-t <other> | -th]",
                OptionFormatter.asSyntaxOptions(builder, options),
                "option with group failed");

        OptionGroup group1 = new OptionGroup()
                .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
                .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build());
        group1.setRequired(true);
        options = new Options()
                .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
                .addOptionGroup(group1)
                .addOption(Option.builder().option("f").argName("other").build())
                .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
                .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
                .addOption(Option.builder().option("s").longOpt("seven").hasArg().build());
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> -t <other> | -th",
                OptionFormatter.asSyntaxOptions(builder, options),
                "options with required group failed");
    }

    @Test
    public void asSyntaxOptionIterableTest() throws IOException {
        OptionFormatter.Builder builder = new OptionFormatter.Builder();
        List<Option> options = new ArrayList<>();

        options.add(Option.builder().option("o").longOpt("one").hasArg().build());
        options.add(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build());
        options.add(Option.builder().option("th").longOpt("three").required().argName("other").build());
        options.add(Option.builder().option("f").argName("other").build());
        options.add(Option.builder().longOpt("five").hasArg().argName("other").build());
        options.add(Option.builder().longOpt("six").required().hasArg().argName("other").build());
        options.add(Option.builder().option("s").longOpt("sevem").hasArg().build());
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> -t <other> -th",
                OptionFormatter.asSyntaxOptions(builder, options));

    }
}
