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

    @Test
    public void testAsSyntaxOption() throws IOException {
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
    public void testGetDescription() {
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
        assertEquals("[Deprecated for removal since now. Use something else] The description",
                builder.build(deprecatedOptionWithAttributes).getDescription(),
                "complex deprecated option failure");
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("deprecatedAttributesData")
    public void testComplexDeprecationFormat(final DeprecatedAttributes da, final String expected) {
        Option.Builder builder = Option.builder("o").deprecated(da);
        Option.Builder builderWithDesc = Option.builder("o").desc("The description").deprecated(da);

        assertEquals(expected, OptionFormatter.COMPLEX_DEPRECATED_FORMAT.apply(builder.build()));
        assertEquals(expected + " The description", OptionFormatter.COMPLEX_DEPRECATED_FORMAT.apply(builderWithDesc.build()));
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

    @Test
    public void testAsOptional() {
        OptionFormatter underTest;
        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();

        underTest = OptionFormatter.from(option);
        assertEquals("[what]", underTest.asOptional("what"));
        assertEquals("", underTest.asOptional(""), "enpty string should return empty string");
        assertEquals("", underTest.asOptional(null), "null should return empty string");

        underTest = new OptionFormatter.Builder().setOptionalDelimiters("-> ", " <-").build(option);
        assertEquals("-> what <-", underTest.asOptional("what"));

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
    public void testDefaultSyntaxFormat() {

        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter formatter = OptionFormatter.from(option);
        assertEquals("[-o <arg>]", formatter.asSyntaxOption());
        assertEquals("-o <arg>", formatter.asSyntaxOption(true));

        option = Option.builder().option("o").longOpt("opt").hasArg().required().build();
        formatter = OptionFormatter.from(option);
        assertEquals("-o <arg>", formatter.asSyntaxOption());
        assertEquals("[-o <arg>]", formatter.asSyntaxOption(false));
    }

    @Test
    public void testSetDefaultArgName() {
        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter.Builder builder = new OptionFormatter.Builder().setDefaultArgName("foo");
        assertEquals("<foo>", builder.build(option).getArgName());

        builder = new OptionFormatter.Builder().setDefaultArgName("");
        assertEquals("<arg>", builder.build(option).getArgName());

        builder = new OptionFormatter.Builder().setDefaultArgName(null);
        assertEquals("<arg>", builder.build(option).getArgName());
    }

    @Test
    public void testSetLongOptPrefix() {
        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter.Builder builder = new OptionFormatter.Builder().setLongOptPrefix("fo");
        assertEquals("foopt", builder.build(option).getLongOpt());

        builder = new OptionFormatter.Builder().setLongOptPrefix("");
        assertEquals("opt", builder.build(option).getLongOpt());

        builder = new OptionFormatter.Builder().setLongOptPrefix(null);
        assertEquals("opt", builder.build(option).getLongOpt());
    }

    @Test
    public void testSetOptSeparator() {
        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter.Builder builder = new OptionFormatter.Builder().setOptSeparator(" and ");
        assertEquals("-o and --opt", builder.build(option).getBothOpt());

        builder = new OptionFormatter.Builder().setOptSeparator("");
        assertEquals("-o--opt", builder.build(option).getBothOpt(), "Empty string should return default");


        builder = new OptionFormatter.Builder().setOptSeparator(null);
        assertEquals("-o--opt", builder.build(option).getBothOpt(), "null string should return default");
    }

    @Test
    public void testSetArgumentNameDelimiters() {
        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter.Builder builder = new OptionFormatter.Builder().setArgumentNameDelimiters("with argument named ", ".");
        assertEquals("with argument named arg.", builder.build(option).getArgName());

        builder = new OptionFormatter.Builder().setArgumentNameDelimiters(null, "");
        assertEquals("arg", builder.build(option).getArgName());

        builder = new OptionFormatter.Builder().setArgumentNameDelimiters("", null);
        assertEquals("arg", builder.build(option).getArgName());

    }

    @Test
    public void testSetOptArgumentSeparator() {
        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();
        OptionFormatter.Builder builder = new OptionFormatter.Builder().setOptArgSeparator(" with argument named ");
        assertEquals("[-o with argument named <arg>]", builder.build(option).asSyntaxOption());

        builder = new OptionFormatter.Builder().setOptArgSeparator(null);
        assertEquals("[-o<arg>]", builder.build(option).asSyntaxOption());

        builder = new OptionFormatter.Builder().setOptArgSeparator("=");
        assertEquals("[-o=<arg>]", builder.build(option).asSyntaxOption());
    }

    @Test
    public void testSetSyntaxFormatFunction() {
        BiFunction<OptionFormatter, Boolean, String> func = (o, b) -> "Yep, it worked";
        Option option = Option.builder().option("o").longOpt("opt").hasArg().build();

        OptionFormatter.Builder builder = new OptionFormatter.Builder().setSyntaxFormatFunction(func);
        assertEquals("Yep, it worked", builder.build(option).asSyntaxOption());

        builder = new OptionFormatter.Builder().setSyntaxFormatFunction(null);
        assertEquals("[-o <arg>]", builder.build(option).asSyntaxOption());
    }

    private void assertEquivalent(final OptionFormatter formatter, final OptionFormatter formatter2) {
        assertEquals(formatter.asSyntaxOption(), formatter2.asSyntaxOption());
        assertEquals(formatter.asSyntaxOption(true), formatter2.asSyntaxOption(true));
        assertEquals(formatter.asSyntaxOption(false), formatter2.asSyntaxOption(false));
        assertEquals(formatter.getOpt(), formatter2.getOpt());
        assertEquals(formatter.getLongOpt(), formatter2.getLongOpt());
        assertEquals(formatter.getBothOpt(), formatter2.getBothOpt());
        assertEquals(formatter.getDescription(), formatter2.getDescription());
        assertEquals(formatter.getArgName(), formatter2.getArgName());
        assertEquals(formatter.asOptional("foo"), formatter2.asOptional("foo"));
    }
    @Test
    public void testCopyConstructor() {
        Function<Option, String> depFunc = o -> "Ooo Deprecated";
        BiFunction<OptionFormatter, Boolean, String> fmtFunc = (o, b) -> "Yep, it worked";
        OptionFormatter.Builder builder = new OptionFormatter.Builder()
                .setLongOptPrefix("l")
                .setOptPrefix("s")
                .setArgumentNameDelimiters("{", "}")
                .setDefaultArgName("Some Argument")
                .setOptSeparator(" and ")
                .setOptionalDelimiters("?>", "<?")
                .setSyntaxFormatFunction(fmtFunc)
                .setDeprecatedFormatFunction(depFunc);

        Option option = Option.builder("o").longOpt("opt").build();

        OptionFormatter formatter = builder.build(option);
        OptionFormatter.Builder builder2 = new OptionFormatter.Builder(formatter);
        assertEquivalent(formatter, builder2.build(option));

        option = Option.builder("o").longOpt("opt").deprecated().required().build();
        formatter = builder.build(option);
        builder2 = new OptionFormatter.Builder(formatter);
        assertEquivalent(formatter, builder2.build(option));
    }
}
