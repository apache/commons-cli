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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.DeprecatedAttributes;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
    public void getDescriptionTest() {
        Option normalOption = Option.builder().option("o").longOpt("one").hasArg()
                .desc("The description").build();

        Option deprecatedOption = Option.builder().option("o").longOpt("one").hasArg()
                .desc("The description").deprecated().build();

        Option deprecatedOptionWithAttributes = Option.builder().option("o").longOpt("one").hasArg()
                .desc("The description").deprecated(
                        DeprecatedAttributes.builder().setForRemoval(true).setSince("now")
                                .setDescription("Use something else").get()).build();

        assertEquals("The description", OptionFormatter.from(normalOption).getDescription(), "normal option failure");
        assertEquals("The description", OptionFormatter.from(deprecatedOption).getDescription(), "deprecated option failure");
        assertEquals("The description", OptionFormatter.from(deprecatedOptionWithAttributes).getDescription(), "complex deprecated option failure");

        OptionFormatter.Builder builder = new OptionFormatter.Builder().setDeprecatedFormatFunction(OptionFormatter.SIMPLE_DEPRECATED_FORMAT);

        assertEquals("The description", builder.build(normalOption).getDescription(), "normal option failure");
        assertEquals("[Deprecated] The description", builder.build(deprecatedOption).getDescription(), "deprecated option failure");
        assertEquals("[Deprecated] The description", builder.build(deprecatedOptionWithAttributes).getDescription(), "complex deprecated option failure");

        builder = new OptionFormatter.Builder().setDeprecatedFormatFunction(OptionFormatter.COMPLEX_DEPRECATED_FORMAT);

        assertEquals("The description", builder.build(normalOption).getDescription(), "normal option failure");
        assertEquals("[Deprecated] The description", builder.build(deprecatedOption).getDescription(), "deprecated option failure");
        assertEquals("[Deprecated for removal since now. Use something else] The description", builder.build(deprecatedOptionWithAttributes).getDescription(), "complex deprecated option failure");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("deprecatedAttributesData")
    public void complexDeprecationFormatTest(DeprecatedAttributes da, String expected) {
        Option.Builder builder = Option.builder("o").deprecated(da);
        Option.Builder builderWithDesc = Option.builder("o").desc("The description").deprecated(da);

        assertEquals(expected, OptionFormatter.COMPLEX_DEPRECATED_FORMAT.apply(builder.build()));
        assertEquals(expected+" The description", OptionFormatter.COMPLEX_DEPRECATED_FORMAT.apply(builderWithDesc.build()));
    }

    public static Stream<Arguments> deprecatedAttributesData() {
        List<Arguments> lst = new ArrayList<>();

        DeprecatedAttributes.Builder daBuilder = DeprecatedAttributes.builder();
        lst.add(Arguments.of(daBuilder.get(), "[Deprecated]"));

        daBuilder.setSince("now");
        lst.add(Arguments.of(daBuilder.get(), "[Deprecated since now]"));

        daBuilder.setForRemoval(true);
        lst.add(Arguments.of(daBuilder.get(), "[Deprecated for removal since now]"));

        daBuilder.setSince(null);
        lst.add(Arguments.of(daBuilder.get(), "[Deprecated for removal]"));

        daBuilder.setForRemoval(false);
        daBuilder.setDescription("Use something else");
        lst.add(Arguments.of(daBuilder.get(), "[Deprecated. Use something else]"));

        daBuilder.setForRemoval(true);
        lst.add(Arguments.of(daBuilder.get(), "[Deprecated for removal. Use something else]"));

        daBuilder.setForRemoval(false);
        daBuilder.setSince("then");
        lst.add(Arguments.of(daBuilder.get(), "[Deprecated since then. Use something else]"));

        daBuilder.setForRemoval(true);
        lst.add(Arguments.of(daBuilder.get(), "[Deprecated for removal since then. Use something else]"));

        return lst.stream();
    }

}
