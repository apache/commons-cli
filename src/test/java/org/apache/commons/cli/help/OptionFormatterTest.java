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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.cli.DeprecatedAttributes;
import org.apache.commons.cli.Option;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class OptionFormatterTest {

    public static Stream<Arguments> deprecatedAttributesData() {
        final List<Arguments> lst = new ArrayList<>();

        final DeprecatedAttributes.Builder daBuilder = DeprecatedAttributes.builder();
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

    private void assertEquivalent(final OptionFormatter formatter, final OptionFormatter formatter2) {
        assertEquals(formatter.toSyntaxOption(), formatter2.toSyntaxOption());
        assertEquals(formatter.toSyntaxOption(true), formatter2.toSyntaxOption(true));
        assertEquals(formatter.toSyntaxOption(false), formatter2.toSyntaxOption(false));
        assertEquals(formatter.getOpt(), formatter2.getOpt());
        assertEquals(formatter.getLongOpt(), formatter2.getLongOpt());
        assertEquals(formatter.getBothOpt(), formatter2.getBothOpt());
        assertEquals(formatter.getDescription(), formatter2.getDescription());
        assertEquals(formatter.getArgName(), formatter2.getArgName());
        assertEquals(formatter.toOptional("foo"), formatter2.toOptional("foo"));
    }

    @Test
    public void testAsOptional() {
        OptionFormatter underTest;
        final Option option = Option.builder().option("o").longOpt("opt").hasArg().build();

        underTest = OptionFormatter.from(option);
        assertEquals("[what]", underTest.toOptional("what"));
        assertEquals("", underTest.toOptional(""), "enpty string should return empty string");
        assertEquals("", underTest.toOptional(null), "null should return empty string");

        underTest = new OptionFormatter.Builder().setOptionalDelimiters("-> ", " <-").build(option);
        assertEquals("-> what <-", underTest.toOptional("what"));

    }

    @Test
    public void testAsSyntaxOption() throws IOException {
        OptionFormatter underTest;

        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        underTest = OptionFormatter.from(option);
        assertEquals("[-o <arg>]", underTest.toSyntaxOption(), "optional arg failed");

        option = Option.builder().option("o").longOpt("opt").hasArg().argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("[-o <other>]", underTest.toSyntaxOption(), "optional 'other' arg failed");

        option = Option.builder().option("o").longOpt("opt").hasArg().required().argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("-o <other>", underTest.toSyntaxOption(), "required 'other' arg failed");

        option = Option.builder().option("o").longOpt("opt").required().argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("-o", underTest.toSyntaxOption(), "required no arg failed");

        option = Option.builder().option("o").argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("[-o]", underTest.toSyntaxOption(), "optional no arg arg failed");

        option = Option.builder().longOpt("opt").hasArg().argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("[--opt <other>]", underTest.toSyntaxOption(), "optional longOpt 'other' arg failed");

        option = Option.builder().longOpt("opt").required().hasArg().argName("other").build();
        underTest = OptionFormatter.from(option);
        assertEquals("--opt <other>", underTest.toSyntaxOption(), "required longOpt 'other' arg failed");

        option = Option.builder().option("ot").longOpt("opt").hasArg().build();
        underTest = OptionFormatter.from(option);
        assertEquals("[-ot <arg>]", underTest.toSyntaxOption(), "optional multi char opt arg failed");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("deprecatedAttributesData")
    public void testComplexDeprecationFormat(final DeprecatedAttributes da, final String expected) {
        final Option.Builder builder = Option.builder("o").deprecated(da);
        final Option.Builder builderWithDesc = Option.builder("o").desc("The description").deprecated(da);

        assertEquals(expected, OptionFormatter.COMPLEX_DEPRECATED_FORMAT.apply(builder.build()));
        assertEquals(expected + " The description", OptionFormatter.COMPLEX_DEPRECATED_FORMAT.apply(builderWithDesc.build()));
    }

    @Test
    public void testCopyConstructor() {
        final Function<Option, String> depFunc = o -> "Ooo Deprecated";
        final BiFunction<OptionFormatter, Boolean, String> fmtFunc = (o, b) -> "Yep, it worked";
        // @formatter:off
        final OptionFormatter.Builder builder = new OptionFormatter.Builder()
                .setLongOptPrefix("l")
                .setOptPrefix("s")
                .setArgumentNameDelimiters("{", "}")
                .setDefaultArgName("Some Argument")
                .setOptSeparator(" and ")
                .setOptionalDelimiters("?>", "<?")
                .setSyntaxFormatFunction(fmtFunc)
                .setDeprecatedFormatFunction(depFunc);
        // @formatter:on

        Option option = Option.builder("o").longOpt("opt").build();

        OptionFormatter formatter = builder.build(option);
        OptionFormatter.Builder builder2 = new OptionFormatter.Builder(formatter);
        assertEquivalent(formatter, builder2.build(option));

        option = Option.builder("o").longOpt("opt").deprecated().required().build();
        formatter = builder.build(option);
        builder2 = new OptionFormatter.Builder(formatter);
        assertEquivalent(formatter, builder2.build(option));
    }

    @Test
    public void testDefaultSyntaxFormat() {

        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter formatter = OptionFormatter.from(option);
        assertEquals("[-o <arg>]", formatter.toSyntaxOption());
        assertEquals("-o <arg>", formatter.toSyntaxOption(true));

        option = Option.builder().option("o").longOpt("opt").hasArg().required().build();
        formatter = OptionFormatter.from(option);
        assertEquals("-o <arg>", formatter.toSyntaxOption());
        assertEquals("[-o <arg>]", formatter.toSyntaxOption(false));
    }

    @Test
    public void testGetBothOpt() {
        OptionFormatter underTest;

        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        underTest = OptionFormatter.from(option);
        assertEquals("-o, --opt", underTest.getBothOpt());

        option = Option.builder().longOpt("opt").hasArg().build();
        underTest = OptionFormatter.from(option);
        assertEquals("--opt", underTest.getBothOpt());

        option = Option.builder().option("o").hasArg().build();
        underTest = OptionFormatter.from(option);
        assertEquals("-o", underTest.getBothOpt());
    }

    @Test
    public void testGetDescription() {
        final Option normalOption = Option.builder().option("o").longOpt("one").hasArg().desc("The description").build();

        final Option deprecatedOption = Option.builder().option("o").longOpt("one").hasArg().desc("The description").deprecated().build();

        final Option deprecatedOptionWithAttributes = Option.builder().option("o").longOpt("one").hasArg().desc("The description")
                .deprecated(DeprecatedAttributes.builder().setForRemoval(true).setSince("now").setDescription("Use something else").get()).build();

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
        assertEquals("[Deprecated for removal since now. Use something else] The description", builder.build(deprecatedOptionWithAttributes).getDescription(),
                "complex deprecated option failure");
    }

    @Test
    public void testSetArgumentNameDelimiters() {
        final Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter.Builder builder = new OptionFormatter.Builder().setArgumentNameDelimiters("with argument named ", ".");
        assertEquals("with argument named arg.", builder.build(option).getArgName());

        builder = new OptionFormatter.Builder().setArgumentNameDelimiters(null, "");
        assertEquals("arg", builder.build(option).getArgName());

        builder = new OptionFormatter.Builder().setArgumentNameDelimiters("", null);
        assertEquals("arg", builder.build(option).getArgName());

    }

    @Test
    public void testSetDefaultArgName() {
        final Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter.Builder builder = new OptionFormatter.Builder().setDefaultArgName("foo");
        assertEquals("<foo>", builder.build(option).getArgName());

        builder = new OptionFormatter.Builder().setDefaultArgName("");
        assertEquals("<arg>", builder.build(option).getArgName());

        builder = new OptionFormatter.Builder().setDefaultArgName(null);
        assertEquals("<arg>", builder.build(option).getArgName());
    }

    @Test
    public void testSetLongOptPrefix() {
        final Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter.Builder builder = new OptionFormatter.Builder().setLongOptPrefix("fo");
        assertEquals("foopt", builder.build(option).getLongOpt());

        builder = new OptionFormatter.Builder().setLongOptPrefix("");
        assertEquals("opt", builder.build(option).getLongOpt());

        builder = new OptionFormatter.Builder().setLongOptPrefix(null);
        assertEquals("opt", builder.build(option).getLongOpt());
    }

    @Test
    public void testSetOptArgumentSeparator() {
        final Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter.Builder builder = new OptionFormatter.Builder().setOptArgSeparator(" with argument named ");
        assertEquals("[-o with argument named <arg>]", builder.build(option).toSyntaxOption());

        builder = new OptionFormatter.Builder().setOptArgSeparator(null);
        assertEquals("[-o<arg>]", builder.build(option).toSyntaxOption());

        builder = new OptionFormatter.Builder().setOptArgSeparator("=");
        assertEquals("[-o=<arg>]", builder.build(option).toSyntaxOption());
    }

    @Test
    public void testSetOptSeparator() {
        final Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter.Builder builder = new OptionFormatter.Builder().setOptSeparator(" and ");
        assertEquals("-o and --opt", builder.build(option).getBothOpt());

        builder = new OptionFormatter.Builder().setOptSeparator("");
        assertEquals("-o--opt", builder.build(option).getBothOpt(), "Empty string should return default");

        builder = new OptionFormatter.Builder().setOptSeparator(null);
        assertEquals("-o--opt", builder.build(option).getBothOpt(), "null string should return default");
    }

    @Test
    public void testSetSyntaxFormatFunction() {
        final BiFunction<OptionFormatter, Boolean, String> func = (o, b) -> "Yep, it worked";
        final Option option = Option.builder().option("o").longOpt("opt").hasArg().build();

        OptionFormatter.Builder builder = new OptionFormatter.Builder().setSyntaxFormatFunction(func);
        assertEquals("Yep, it worked", builder.build(option).toSyntaxOption());

        builder = new OptionFormatter.Builder().setSyntaxFormatFunction(null);
        assertEquals("[-o <arg>]", builder.build(option).toSyntaxOption());
    }
}
