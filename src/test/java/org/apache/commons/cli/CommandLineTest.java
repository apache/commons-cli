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

package org.apache.commons.cli;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CommandLineTest {

    private enum Count { ONE, TWO, THREE }

    private static Stream<Arguments> createHasOptionParameters() throws ParseException {
        final List<Arguments> lst = new ArrayList<>();
        final Option optT = Option.builder().option("T").longOpt("tee").deprecated().optionalArg(true).build();
        final Option optU = Option.builder("U").longOpt("you").optionalArg(true).build();
        final OptionGroup optionGroup = new OptionGroup().addOption(optT).addOption(optU);

        final String[] foobar = { "foo", "bar" };
        // T set
        lst.add(Arguments.of(new String[] {"-T"}, optT, optionGroup, true, true, true, true, optT));
        lst.add(Arguments.of(new String[] {"-T", "foo"}, optT, optionGroup, true, true, true, true, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optT, optionGroup, true, true, true, true, optT));
        lst.add(Arguments.of(new String[] {"--tee", "foo"}, optT, optionGroup, true, true, true, true, optT));

        lst.add(Arguments.of(new String[] {"-U"}, optT, optionGroup, false, false, false, true, optU));
        lst.add(Arguments.of(new String[] {"-U", "foo", "bar"}, optT, optionGroup, false, false, false, true, optU));
        lst.add(Arguments.of(new String[] {"--you"}, optT, optionGroup, false, false, false, true, optU));
        lst.add(Arguments.of(new String[] {"--you", "foo", "bar"}, optT, optionGroup, false, false, false, true, optU));

        // U set
        lst.add(Arguments.of(new String[] {"-T"}, optU, optionGroup, false, false, true, true, optT));
        lst.add(Arguments.of(new String[] {"-T", "foo", "bar"}, optU, optionGroup, false, false, true, true, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optU, optionGroup, false, false, true, true, optT));
        lst.add(Arguments.of(new String[] {"--tee", "foo", "bar"}, optU, optionGroup, false, false, true, true, optT));

        lst.add(Arguments.of(new String[] {"-U"}, optU, optionGroup, false, true, false, true, optU));
        lst.add(Arguments.of(new String[] {"-U", "foo", "bar"}, optU, optionGroup, false, true, false, true, optU));
        lst.add(Arguments.of(new String[] {"--you"}, optU, optionGroup, false, true, false, true, optU));
        lst.add(Arguments.of(new String[] {"--you", "foo", "bar"},  optU, optionGroup, false, true, false, true, optU));

        return lst.stream();
    }

    private static Stream<Arguments> createOptionValueParameters() throws ParseException {
        final List<Arguments> lst = new ArrayList<>();
        final Option optT = Option.builder().option("T").longOpt("tee").deprecated().optionalArg(true).build();
        final Option optU = Option.builder("U").longOpt("you").optionalArg(true).build();
        final OptionGroup optionGroup = new OptionGroup().addOption(optT).addOption(optU);

        // T set
        lst.add(Arguments.of(new String[] {"-T"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "foo"}, optT, optionGroup, true, "foo", true, "foo", optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"--tee", "foo"}, optT, optionGroup, true, "foo", true, "foo", optT));

        lst.add(Arguments.of(new String[] {"-U"}, optT, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"-U", "foo"}, optT, optionGroup, false, null, false, "foo", optU));
        lst.add(Arguments.of(new String[] {"--you"}, optT, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"--you", "foo"}, optT, optionGroup, false, null, false, "foo", optU));

        // U set
        lst.add(Arguments.of(new String[] {"-T"}, optU, optionGroup, false, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "foo"}, optU, optionGroup, false, null, true, "foo", optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optU, optionGroup, false, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"--tee", "foo"}, optU, optionGroup, false, null, true, "foo", optT));

        lst.add(Arguments.of(new String[] {"-U"}, optU, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"-U", "foo"}, optU, optionGroup, false, "foo", false, "foo", optU));
        lst.add(Arguments.of(new String[] {"--you"}, optU, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"--you", "foo"},  optU, optionGroup, false, "foo", false, "foo", optU));

        return lst.stream();
    }

    private static Stream<Arguments> createOptionValuesParameters() throws ParseException {
        final List<Arguments> lst = new ArrayList<>();
        final Option optT = Option.builder().option("T").longOpt("tee").numberOfArgs(2).deprecated().optionalArg(true).build();
        final Option optU = Option.builder("U").longOpt("you").numberOfArgs(2).optionalArg(true).build();
        final OptionGroup optionGroup = new OptionGroup().addOption(optT).addOption(optU);

        final String[] foobar = { "foo", "bar" };
        // T set
        lst.add(Arguments.of(new String[] {"-T"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "foo", "bar"}, optT, optionGroup, true, foobar, true, foobar, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"--tee", "foo", "bar"}, optT, optionGroup, true, foobar, true, foobar, optT));

        lst.add(Arguments.of(new String[] {"-U"}, optT, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"-U", "foo", "bar"}, optT, optionGroup, false, null, false, foobar, optU));
        lst.add(Arguments.of(new String[] {"--you"}, optT, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"--you", "foo", "bar"}, optT, optionGroup, false, null, false, foobar, optU));

        // U set
        lst.add(Arguments.of(new String[] {"-T"}, optU, optionGroup, false, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "foo", "bar"}, optU, optionGroup, false, null, true, foobar, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optU, optionGroup, false, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"--tee", "foo", "bar"}, optU, optionGroup, false, null, true, foobar, optT));

        lst.add(Arguments.of(new String[] {"-U"}, optU, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"-U", "foo", "bar"}, optU, optionGroup, false, foobar, false, foobar, optU));
        lst.add(Arguments.of(new String[] {"--you"}, optU, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"--you", "foo", "bar"},  optU, optionGroup, false, foobar, false, foobar, optU));

        return lst.stream();
    }

    private static Stream<Arguments> createParsedOptionValueParameters() throws ParseException {
        final List<Arguments> lst = new ArrayList<>();
        final Option optT = Option.builder().option("T").longOpt("tee").deprecated().type(Integer.class).optionalArg(true).build();
        final Option optU = Option.builder("U").longOpt("you").type(Integer.class).optionalArg(true).build();
        final OptionGroup optionGroup = new OptionGroup().addOption(optT).addOption(optU);
        final Integer expected = Integer.valueOf(1);

        // T set
        lst.add(Arguments.of(new String[] {"-T"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "1"}, optT, optionGroup, true, expected, true, expected, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"--tee", "1"}, optT, optionGroup, true, expected, true, expected, optT));

        lst.add(Arguments.of(new String[] {"-U"}, optT, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"-U", "1"}, optT, optionGroup, false, null, false, expected, optU));
        lst.add(Arguments.of(new String[] {"--you"}, optT, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"--you", "1"}, optT, optionGroup, false, null, false, expected, optU));

        // U set
        lst.add(Arguments.of(new String[] {"-T"}, optU, optionGroup, false, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "1"}, optU, optionGroup, false, null, true, expected, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optU, optionGroup, false, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"--tee", "1"}, optU, optionGroup, false, null, true, expected, optT));

        lst.add(Arguments.of(new String[] {"-U"}, optU, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"-U", "1"}, optU, optionGroup, false, expected, false, expected, optU));
        lst.add(Arguments.of(new String[] {"--you"}, optU, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"--you", "1"},  optU, optionGroup, false, expected, false, expected, optU));

        return lst.stream();
    }

    private static Stream<Arguments> createParsedOptionValuesParameters() throws ParseException {
        final List<Arguments> lst = new ArrayList<>();
        final Option optT = Option.builder().option("T").longOpt("tee").deprecated().type(Integer.class).optionalArg(true).hasArgs().build();
        final Option optU = Option.builder("U").longOpt("you").type(Integer.class).optionalArg(true).hasArgs().build();
        final OptionGroup optionGroup = new OptionGroup().addOption(optT).addOption(optU);
        final Integer[] expected = new Integer[]{1, 2};

        // T set
        lst.add(Arguments.of(new String[] {"-T"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "1", "2"}, optT, optionGroup, true, expected, true, expected, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"--tee", "1", "2"}, optT, optionGroup, true, expected, true, expected, optT));

        lst.add(Arguments.of(new String[] {"-U"}, optT, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"-U", "1", "2"}, optT, optionGroup, false, null, false, expected, optU));
        lst.add(Arguments.of(new String[] {"--you"}, optT, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"--you", "1", "2"}, optT, optionGroup, false, null, false, expected, optU));

        // U set
        lst.add(Arguments.of(new String[] {"-T"}, optU, optionGroup, false, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "1", "2"}, optU, optionGroup, false, null, true, expected, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optU, optionGroup, false, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"--tee", "1", "2"}, optU, optionGroup, false, null, true, expected, optT));

        lst.add(Arguments.of(new String[] {"-U"}, optU, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"-U", "1", "2"}, optU, optionGroup, false, expected, false, expected, optU));
        lst.add(Arguments.of(new String[] {"--you"}, optU, optionGroup, false, null, false, null, optU));
        lst.add(Arguments.of(new String[] {"--you", "1", "2"},  optU, optionGroup, false, expected, false, expected, optU));

        return lst.stream();
    }

    char asChar(final Option opt) {
        return opt.getOpt().charAt(0);
    }

    private void assertWritten(final boolean optDep, final ByteArrayOutputStream baos) {
        System.out.flush();
        if (optDep) {
            assertEquals("Option 'T''tee': Deprecated", baos.toString().trim());
        } else {
            assertEquals("", baos.toString());
        }
        baos.reset();
    }

    /**
     * verifies that the deprecation handler has been called only once or not at all.
     * @param optDep {@code true} if the dependency should have been logged.
     * @param handler The list that the deprecation is logged to.
     * @param opt The option that triggered the logging. May be (@code null} if {@code optDep} is {@code false}.
     */
    void checkHandler(final boolean optDep, final List<Option> handler, final Option opt) {
        if (optDep) {
            assertEquals(1, handler.size());
            assertEquals(opt, handler.get(0));
        } else {
            assertEquals(0, handler.size());
        }
        handler.clear();
    }

    @Test
    public void testBadGetParsedOptionValue() throws Exception {

        final Options options = new Options();
        options.addOption(Option.builder("i").hasArg().type(Number.class).build());
        options.addOption(Option.builder("c").hasArg().converter(s -> Count.valueOf(s.toUpperCase())).build());

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, new String[] {"-i", "foo", "-c", "bar"});

        assertEquals(NumberFormatException.class, assertThrows(ParseException.class, () -> cmd.getParsedOptionValue("i")).getCause().getClass());
        assertEquals(IllegalArgumentException.class, assertThrows(ParseException.class, () -> cmd.getParsedOptionValue("c")).getCause().getClass());
    }

    @Test
    public void testBuilderBuild() {
        // @formatter:off
        final CommandLine cmd = CommandLine.builder()
                .addArg("foo")
                .addArg("bar")
                .addOption(Option.builder("T").build())
                .build();
        // @formatter:on
        assertEquals("foo", cmd.getArgs()[0]);
        assertEquals("bar", cmd.getArgList().get(1));
        assertEquals("T", cmd.getOptions()[0].getOpt());
    }

    @Test
    public void testBuilderGet() {
        // @formatter:off
        final CommandLine cmd = CommandLine.builder()
                .addArg("foo")
                .addArg("bar")
                .addOption(Option.builder("T").build())
                .get();
        // @formatter:on
        assertEquals("foo", cmd.getArgs()[0]);
        assertEquals("bar", cmd.getArgList().get(1));
        assertEquals("T", cmd.getOptions()[0].getOpt());
    }

    @Test
    public void testBuilderNullArgs() {
        final CommandLine.Builder builder = CommandLine.builder();
        builder.addArg(null).addArg(null);
        builder.addOption(Option.builder("T").build());
        final CommandLine cmd = builder.build();

        assertEquals(0, cmd.getArgs().length);
        assertEquals("T", cmd.getOptions()[0].getOpt());
    }

    @Test
    public void testBuilderNullOption() {
        final CommandLine.Builder builder = CommandLine.builder();
        builder.addArg("foo").addArg("bar");
        builder.addOption(null);
        builder.addOption(null);
        builder.addOption(null);
        final CommandLine cmd = builder.build();

        assertEquals("foo", cmd.getArgs()[0]);
        assertEquals("bar", cmd.getArgList().get(1));
        assertEquals(0, cmd.getOptions().length);
    }

    @Test
    public void testGetOptionProperties() throws Exception {
        final String[] args = {"-Dparam1=value1", "-Dparam2=value2", "-Dparam3", "-Dparam4=value4", "-D", "--property", "foo=bar"};

        final Options options = new Options();
        options.addOption(Option.builder("D").valueSeparator().optionalArg(true).numberOfArgs(2).build());
        options.addOption(Option.builder().valueSeparator().numberOfArgs(2).longOpt("property").build());

        final Parser parser = new GnuParser();
        final CommandLine cl = parser.parse(options, args);

        final Properties props = cl.getOptionProperties("D");
        assertNotNull(props, "null properties");
        assertEquals(4, props.size(), "number of properties in " + props);
        assertEquals("value1", props.getProperty("param1"), "property 1");
        assertEquals("value2", props.getProperty("param2"), "property 2");
        assertEquals("true", props.getProperty("param3"), "property 3");
        assertEquals("value4", props.getProperty("param4"), "property 4");

        assertEquals("bar", cl.getOptionProperties("property").getProperty("foo"), "property with long format");
    }

    @Test
    public void testGetOptionPropertiesWithOption() throws Exception {
        final String[] args = {"-Dparam1=value1", "-Dparam2=value2", "-Dparam3", "-Dparam4=value4", "-D", "--property", "foo=bar"};

        final Options options = new Options();
        final Option optionD = Option.builder("D").valueSeparator().numberOfArgs(2).optionalArg(true).build();
        final Option optionProperty = Option.builder().valueSeparator().numberOfArgs(2).longOpt("property").build();
        options.addOption(optionD);
        options.addOption(optionProperty);

        final Parser parser = new GnuParser();
        final CommandLine cl = parser.parse(options, args);

        final Properties props = cl.getOptionProperties(optionD);
        assertNotNull(props, "null properties");
        assertEquals(4, props.size(), "number of properties in " + props);
        assertEquals("value1", props.getProperty("param1"), "property 1");
        assertEquals("value2", props.getProperty("param2"), "property 2");
        assertEquals("true", props.getProperty("param3"), "property 3");
        assertEquals("value4", props.getProperty("param4"), "property 4");

        assertEquals("bar", cl.getOptionProperties(optionProperty).getProperty("foo"), "property with long format");
    }

    @Test
    public void testGetOptionsBuilder() {
        final CommandLine cmd = CommandLine.builder().build();
        assertNotNull(cmd.getOptions());
        assertEquals(0, cmd.getOptions().length);

        cmd.addOption(null);
        cmd.addOption(new Option("a", null));
        cmd.addOption(new Option("b", null));
        cmd.addOption(new Option("c", null));

        assertEquals(3, cmd.getOptions().length);
    }

    @Test
    public void testGetOptionsCtor() {
        final CommandLine cmd = new CommandLine();
        assertNotNull(cmd.getOptions());
        assertEquals(0, cmd.getOptions().length);

        cmd.addOption(new Option("a", null));
        cmd.addOption(new Option("b", null));
        cmd.addOption(new Option("c", null));
        cmd.addOption(null);

        assertEquals(3, cmd.getOptions().length);
    }

    /**
     * Test for get option value with and without default values.  Verifies that deprecated options only report as
     * deprecated once.
     * @param args the argument strings to parse.
     * @param opt the option to check for values with.
     * @param optionGroup the option group to check for values with.
     * @param optDep {@code true} if the opt is deprecated.
     * @param optValue  The value expected from opt.
     * @param grpDep {@code true} if the group is deprecated.
     * @param grpValue the value expected from the group.
     * @param grpOpt the option that is expected to be processed by the group.
     * @throws ParseException on parse error.
     */
    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("createOptionValueParameters")
    public void testGetOptionValue(final String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep,
                                   final String optValue, final boolean grpDep, final String grpValue, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final List<Option> handler = new ArrayList<>();
        final CommandLine commandLine = DefaultParser.builder().setDeprecatedHandler(handler::add).get().parse(options, args);
        final Supplier<String> thinger = () -> "thing";
        final OptionGroup otherGroup = new OptionGroup().addOption(Option.builder("o").longOpt("other").hasArg().build())
                .addOption(Option.builder().option("p").longOpt("part").hasArg().build());
        final OptionGroup nullGroup = null;

        // test char option
        assertEquals(optValue, commandLine.getOptionValue(asChar(opt)));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(asChar(opt), "thing"));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(asChar(opt), thinger));
        checkHandler(optDep, handler, opt);

        // test short option arg
        assertEquals(optValue, commandLine.getOptionValue(opt.getOpt()));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt.getOpt(), "thing"));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt.getOpt(), thinger));
        checkHandler(optDep, handler, opt);

        // test long option arg
        assertEquals(optValue, commandLine.getOptionValue(opt.getLongOpt()));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt.getLongOpt(), "thing"));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt.getLongOpt(), thinger));
        checkHandler(optDep, handler, opt);

        // test Option arg
        assertEquals(optValue, commandLine.getOptionValue(opt));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt, "thing"));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt, thinger));
        checkHandler(optDep, handler, opt);

        // test option group  arg
        assertEquals(grpValue, commandLine.getOptionValue(optionGroup));
        checkHandler(grpDep, handler, grpOpt);

        assertEquals(grpValue == null ? "thing" : grpValue, commandLine.getOptionValue(optionGroup, "thing"));
        checkHandler(grpDep, handler, grpOpt);

        assertEquals(grpValue == null ? "thing" : grpValue, commandLine.getOptionValue(optionGroup, thinger));
        checkHandler(grpDep, handler, grpOpt);

        // test other group arg
        assertNull(commandLine.getOptionValue(otherGroup));
        checkHandler(false, handler, grpOpt);

        assertEquals("thing", commandLine.getOptionValue(otherGroup, "thing"));
        checkHandler(false, handler, grpOpt);

        assertEquals("thing", commandLine.getOptionValue(otherGroup, thinger));
        checkHandler(false, handler, grpOpt);

        // test null Group arg
        assertNull(commandLine.getOptionValue(nullGroup));
        checkHandler(false, handler, grpOpt);

        assertEquals("thing", commandLine.getOptionValue(nullGroup, "thing"));
        checkHandler(false, handler, grpOpt);

        assertEquals("thing", commandLine.getOptionValue(nullGroup, thinger));
        checkHandler(false, handler, grpOpt);

        // test not an option
        assertNull(commandLine.getOptionValue("Nope"));
        checkHandler(false, handler, opt);

        assertEquals("thing", commandLine.getOptionValue("Nope", "thing"));
        checkHandler(false, handler, opt);

        assertEquals("thing", commandLine.getOptionValue("Nope", thinger));
        checkHandler(false, handler, opt);
    }

    /**
     * Test for get option values with and without default values.  Verifies that deprecated options only report as
     * deprecated once.
     * @param args the argument strings to parse.
     * @param opt the option to check for values with.
     * @param optionGroup the option group to check for values with.
     * @param optDep {@code true} if the opt is deprecated.
     * @param optValue  The value expected from opt.
     * @param grpDep {@code true} if the group is deprecated.
     * @param grpValue the value expected from the group.
     * @param grpOpt the option that is expected to be processed by the group.
     * @throws ParseException on parse error.
     */
    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("createOptionValuesParameters")
    public void testGetOptionValues(final String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep,
                                    final String[] optValue, final boolean grpDep, final String[] grpValue, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final List<Option> handler = new ArrayList<>();
        final CommandLine commandLine = DefaultParser.builder().setDeprecatedHandler(handler::add).get().parse(options, args);
        final OptionGroup otherGroup = new OptionGroup().addOption(Option.builder("o").longOpt("other").hasArg().build())
                .addOption(Option.builder().option("p").longOpt("part").hasArg().build());
        final OptionGroup nullGroup = null;

        // test char option arg
        assertArrayEquals(optValue, commandLine.getOptionValues(asChar(opt)));
        checkHandler(optDep, handler, opt);

        // test short option arg
        assertArrayEquals(optValue, commandLine.getOptionValues(opt.getOpt()));
        checkHandler(optDep, handler, opt);

        // test long option arg
        assertArrayEquals(optValue, commandLine.getOptionValues(opt.getLongOpt()));
        checkHandler(optDep, handler, opt);

        // test Option arg
        assertArrayEquals(optValue, commandLine.getOptionValues(opt));
        checkHandler(optDep, handler, opt);

        // test OptionGroup arg
        assertArrayEquals(grpValue, commandLine.getOptionValues(optionGroup));
        checkHandler(grpDep, handler, grpOpt);

        // test not an option
        assertNull(commandLine.getOptionValues("Nope"));
        checkHandler(false, handler, opt);

        // test other group arg
        assertNull(commandLine.getOptionValues(otherGroup));
        checkHandler(false, handler, grpOpt);

        // test null group arg
        assertNull(commandLine.getOptionValues(nullGroup));
        checkHandler(false, handler, grpOpt);
    }

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("createParsedOptionValueParameters")
    public void testGetParsedOptionValue(final String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep,
                                         final Integer optValue, final boolean grpDep, final Integer grpValue, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final List<Option> handler = new ArrayList<>();
        final CommandLine commandLine = DefaultParser.builder().setDeprecatedHandler(handler::add).get().parse(options, args);
        final Supplier<Integer> thinger = () -> 2;
        final OptionGroup otherGroup = new OptionGroup().addOption(Option.builder("o").longOpt("other").hasArg().build())
                .addOption(Option.builder().option("p").longOpt("part").hasArg().build());
        final OptionGroup nullGroup = null;
        final Integer thing = 2;

        // test char option arg
        assertEquals(optValue, commandLine.getParsedOptionValue(asChar(opt)));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValue(asChar(opt), thing));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValue(asChar(opt), thinger));
        checkHandler(optDep, handler, opt);

        // test short option arg
        assertEquals(optValue, commandLine.getParsedOptionValue(opt.getOpt()));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValue(opt.getOpt(), thing));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValue(opt.getOpt(), thinger));
        checkHandler(optDep, handler, opt);

        // test long option arg
        assertEquals(optValue, commandLine.getParsedOptionValue(opt.getLongOpt()));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValue(opt.getLongOpt(), thing));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValue(opt.getLongOpt(), thinger));
        checkHandler(optDep, handler, opt);

        // test Option arg
        assertEquals(optValue, commandLine.getParsedOptionValue(opt));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValue(opt, thing));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValue(opt, thinger));
        checkHandler(optDep, handler, opt);

        // test OptionGroup arg
        assertEquals(grpValue, commandLine.getParsedOptionValue(optionGroup));
        checkHandler(grpDep, handler, grpOpt);

        assertEquals(grpValue == null ? thing : grpValue, commandLine.getParsedOptionValue(optionGroup, thing));
        checkHandler(grpDep, handler, grpOpt);

        assertEquals(grpValue == null ? thing : grpValue, commandLine.getParsedOptionValue(optionGroup, thinger));
        checkHandler(grpDep, handler, grpOpt);

        // test other Group arg
        assertNull(commandLine.getParsedOptionValue(otherGroup));
        checkHandler(false, handler, grpOpt);

        assertEquals(thing, commandLine.getParsedOptionValue(otherGroup, thing));
        checkHandler(false, handler, grpOpt);

        assertEquals(thing, commandLine.getParsedOptionValue(otherGroup, thinger));
        checkHandler(false, handler, grpOpt);

        // test null Group arg
        assertNull(commandLine.getParsedOptionValue(nullGroup));
        checkHandler(false, handler, grpOpt);

        assertEquals(thing, commandLine.getParsedOptionValue(nullGroup, thing));
        checkHandler(false, handler, grpOpt);

        assertEquals(thing, commandLine.getParsedOptionValue(nullGroup, thinger));
        checkHandler(false, handler, grpOpt);

        // test not an option
        assertNull(commandLine.getParsedOptionValue("Nope"));
        checkHandler(false, handler, opt);

        assertEquals(thing, commandLine.getParsedOptionValue("Nope", thing));
        checkHandler(false, handler, opt);

        assertEquals(thing, commandLine.getParsedOptionValue("Nope", thinger));
        checkHandler(false, handler, opt);
    }

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("createParsedOptionValuesParameters")
    public void testGetParsedOptionValues(final String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep,
                                         final Integer[] optValue, final boolean grpDep, final Integer[] grpValue, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final List<Option> handler = new ArrayList<>();
        final CommandLine commandLine = DefaultParser.builder().setDeprecatedHandler(handler::add).get().parse(options, args);
        final Supplier<Integer[]> thinger = () -> new Integer[]{2, 3};
        final OptionGroup otherGroup = new OptionGroup().addOption(Option.builder("o").longOpt("other").hasArg().build())
                .addOption(Option.builder().option("p").longOpt("part").hasArg().build());
        final OptionGroup nullGroup = null;
        final Integer[] thing = {2, 3};

        // test char option arg
        assertArrayEquals(optValue, commandLine.getParsedOptionValues(asChar(opt)));
        checkHandler(optDep, handler, opt);

        assertArrayEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValues(asChar(opt), thing));
        checkHandler(optDep, handler, opt);

        assertArrayEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValues(asChar(opt), thinger));
        checkHandler(optDep, handler, opt);

        // test short option arg
        assertArrayEquals(optValue, commandLine.getParsedOptionValues(opt.getOpt()));
        checkHandler(optDep, handler, opt);

        assertArrayEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValues(opt.getOpt(), thing));
        checkHandler(optDep, handler, opt);

        assertArrayEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValues(opt.getOpt(), thinger));
        checkHandler(optDep, handler, opt);

        // test long option arg
        assertArrayEquals(optValue, commandLine.getParsedOptionValues(opt.getLongOpt()));
        checkHandler(optDep, handler, opt);

        assertArrayEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValues(opt.getLongOpt(), thing));
        checkHandler(optDep, handler, opt);

        assertArrayEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValues(opt.getLongOpt(), thinger));
        checkHandler(optDep, handler, opt);

        // test Option arg
        assertArrayEquals(optValue, commandLine.getParsedOptionValues(opt));
        checkHandler(optDep, handler, opt);

        assertArrayEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValues(opt, thing));
        checkHandler(optDep, handler, opt);

        assertArrayEquals(optValue == null ? thing : optValue, commandLine.getParsedOptionValues(opt, thinger));
        checkHandler(optDep, handler, opt);

        // test OptionGroup arg
        assertArrayEquals(grpValue, commandLine.getParsedOptionValues(optionGroup));
        checkHandler(grpDep, handler, grpOpt);

        assertArrayEquals(grpValue == null ? thing : grpValue, commandLine.getParsedOptionValues(optionGroup, thing));
        checkHandler(grpDep, handler, grpOpt);

        assertArrayEquals(grpValue == null ? thing : grpValue, commandLine.getParsedOptionValues(optionGroup, thinger));
        checkHandler(grpDep, handler, grpOpt);

        // test other Group arg
        assertNull(commandLine.getParsedOptionValues(otherGroup));
        checkHandler(false, handler, grpOpt);

        assertArrayEquals(thing, commandLine.getParsedOptionValues(otherGroup, thing));
        checkHandler(false, handler, grpOpt);

        assertArrayEquals(thing, commandLine.getParsedOptionValues(otherGroup, thinger));
        checkHandler(false, handler, grpOpt);

        // test null Group arg
        assertNull(commandLine.getParsedOptionValues(nullGroup));
        checkHandler(false, handler, grpOpt);

        assertArrayEquals(thing, commandLine.getParsedOptionValues(nullGroup, thing));
        checkHandler(false, handler, grpOpt);

        assertArrayEquals(thing, commandLine.getParsedOptionValues(nullGroup, thinger));
        checkHandler(false, handler, grpOpt);

        // test not an option
        assertNull(commandLine.getParsedOptionValues("Nope"));
        checkHandler(false, handler, opt);

        assertArrayEquals(thing, commandLine.getParsedOptionValues("Nope", thing));
        checkHandler(false, handler, opt);

        assertArrayEquals(thing, commandLine.getParsedOptionValues("Nope", thinger));
        checkHandler(false, handler, opt);
    }

    /**
     * Tests the hasOption calls.
     * @param args the argument strings to parse.
     * @param opt the option to check for values with.
     * @param optionGroup the option group to check for values with.
     * @param optDep {@code true} if the opt is deprecated.
     * @param has {@code true} if the opt is present.
     * @param grpDep {@code true} if the group is deprecated.
     * @param hasGrp {@code true} if the group is present.
     * @param grpOpt the option that is expected to be processed by the group.
     * @throws ParseException on parsing error.
     */
    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("createHasOptionParameters")
    public void testHasOption(final String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep,
                              final boolean has, final boolean grpDep, final boolean hasGrp, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final List<Option> handler = new ArrayList<>();
        final CommandLine commandLine = DefaultParser.builder().setDeprecatedHandler(handler::add).get().parse(options, args);
        final OptionGroup otherGroup = new OptionGroup().addOption(Option.builder("o").longOpt("other").hasArg().build())
                .addOption(Option.builder().option("p").longOpt("part").hasArg().build());
        final OptionGroup nullGroup = null;

        // test char option arg
        assertEquals(has, commandLine.hasOption(asChar(opt)));
        checkHandler(optDep, handler, opt);

        // test short option arg
        assertEquals(has, commandLine.hasOption(opt.getOpt()));
        checkHandler(optDep, handler, opt);

        // test long option arg
        assertEquals(has, commandLine.hasOption(opt.getLongOpt()));
        checkHandler(optDep, handler, opt);

        // test Option arg
        assertEquals(has, commandLine.hasOption(opt));
        checkHandler(optDep, handler, opt);

        // test OptionGroup arg
        assertEquals(hasGrp, commandLine.hasOption(optionGroup));
        checkHandler(grpDep, handler, grpOpt);

        // test other group arg
        assertFalse(commandLine.hasOption(otherGroup));
        checkHandler(false, handler, grpOpt);

        // test null group arg
        assertFalse(commandLine.hasOption(nullGroup));
        checkHandler(false, handler, grpOpt);

        // test not an option
        assertFalse(commandLine.hasOption("Nope"));
        checkHandler(false, handler, opt);
    }

    /**
     * Tests the hasOption calls.
     * @param args the argument strings to parse.
     * @param opt the option to check for values with.
     * @param optionGroup the option group to check for values with.
     * @param optDep {@code true} if the opt is deprecated.
     * @param has {@code true} if the opt is present.
     * @param grpDep {@code true} if the group is deprecated.
     * @param hasGrp {@code true} if the group is present.
     * @param grpOpt the option that is expected to be processed by the group.
     * @throws ParseException on parsing error.
     */
    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("createHasOptionParameters")
    public void testHasOptionNoDeprecationHandler(final String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep,
                              final boolean has, final boolean grpDep, final boolean hasGrp, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final CommandLine commandLine = DefaultParser.builder().get().parse(options, args);
        final PrintStream ps = System.out;
        try {
            System.setOut(new PrintStream(baos));

            // test char option arg
            assertEquals(has, commandLine.hasOption(asChar(opt)));
            assertWritten(optDep, baos);

            // test short option arg
            assertEquals(has, commandLine.hasOption(opt.getOpt()));
            assertWritten(optDep, baos);

            // test long option arg
            assertEquals(has, commandLine.hasOption(opt.getLongOpt()));
            assertWritten(optDep, baos);

            // test Option arg
            assertEquals(has, commandLine.hasOption(opt));
            assertWritten(optDep, baos);

            // test OptionGroup arg
            assertEquals(hasGrp, commandLine.hasOption(optionGroup));
            assertWritten(grpDep, baos);

            // test not an option
            assertFalse(commandLine.hasOption("Nope"));
            assertWritten(false, baos);
        } finally {
            System.setOut(ps);
        }
    }

    /**
     * Tests the hasOption calls.
     * @param args the argument strings to parse.
     * @param opt the option to check for values with.
     * @param optionGroup the option group to check for values with.
     * @param optDep {@code true} if the opt is deprecated.
     * @param has {@code true} if the opt is present.
     * @param grpDep {@code true} if the group is deprecated.
     * @param hasGrp {@code true} if the group is present.
     * @param grpOpt the option that is expected to be processed by the group.
     * @throws ParseException on parsing error.
     */
    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("createHasOptionParameters")
    public void testHasOptionNullDeprecationHandler(final String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep,
                                                  final boolean has, final boolean grpDep, final boolean hasGrp, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final CommandLine commandLine = DefaultParser.builder().setDeprecatedHandler(null).get().parse(options, args);
        final PrintStream ps = System.out;
        try {
            System.setOut(new PrintStream(baos));

            // test char option arg
            assertEquals(has, commandLine.hasOption(asChar(opt)));
            assertWritten(false, baos);

            // test short option arg
            assertEquals(has, commandLine.hasOption(opt.getOpt()));
            assertWritten(false, baos);

            // test long option arg
            assertEquals(has, commandLine.hasOption(opt.getLongOpt()));
            assertWritten(false, baos);

            // test Option arg
            assertEquals(has, commandLine.hasOption(opt));
            assertWritten(false, baos);

            // test OptionGroup arg
            assertEquals(hasGrp, commandLine.hasOption(optionGroup));
            assertWritten(false, baos);

            // test not an option
            assertFalse(commandLine.hasOption("Nope"));
            assertWritten(false, baos);
        } finally {
            System.setOut(ps);
        }
    }

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("createOptionValueParameters")
    public void testNoDeprecationHandler(final String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep,
                                   final String optValue, final boolean grpDep, final String grpValue, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final CommandLine commandLine = DefaultParser.builder().get().parse(options, args);
        final Supplier<String> thinger = () -> "thing";
        final Supplier<String> nullSupplier = null;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = System.out;
        try {
            System.setOut(new PrintStream(baos));

            final OptionGroup otherGroup = new OptionGroup().addOption(Option.builder("o").longOpt("other").hasArg().build())
                    .addOption(Option.builder().option("p").longOpt("part").hasArg().build());
            final OptionGroup nullGroup = null;

            // test char option
            assertEquals(optValue, commandLine.getOptionValue(asChar(opt)));
            assertWritten(optDep, baos);

            assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(asChar(opt), "thing"));
            assertWritten(optDep, baos);

            assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(asChar(opt), thinger));
            assertWritten(optDep, baos);

            assertEquals(optValue, commandLine.getOptionValue(asChar(opt), nullSupplier));
            assertWritten(optDep, baos);

            // test short option arg
            assertEquals(optValue, commandLine.getOptionValue(opt.getOpt()));
            assertWritten(optDep, baos);

            assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt.getOpt(), "thing"));
            assertWritten(optDep, baos);

            assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt.getOpt(), thinger));
            assertWritten(optDep, baos);

            assertEquals(optValue, commandLine.getOptionValue(opt.getOpt(), nullSupplier));
            assertWritten(optDep, baos);

            // test long option arg
            assertEquals(optValue, commandLine.getOptionValue(opt.getLongOpt()));
            assertWritten(optDep, baos);

            assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt.getLongOpt(), "thing"));
            assertWritten(optDep, baos);

            assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt.getLongOpt(), thinger));
            assertWritten(optDep, baos);

            assertEquals(optValue, commandLine.getOptionValue(opt.getLongOpt(), nullSupplier));
            assertWritten(optDep, baos);

            // test Option arg
            assertEquals(optValue, commandLine.getOptionValue(opt));
            assertWritten(optDep, baos);

            assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt, "thing"));
            assertWritten(optDep, baos);

            assertEquals(optValue == null ? "thing" : optValue, commandLine.getOptionValue(opt, thinger));
            assertWritten(optDep, baos);

            assertEquals(optValue, commandLine.getOptionValue(opt, nullSupplier));
            assertWritten(optDep, baos);

            // test optionGroup  arg
            assertEquals(grpValue, commandLine.getOptionValue(optionGroup));
            assertWritten(grpDep, baos);

            assertEquals(grpValue == null ? "thing" : grpValue, commandLine.getOptionValue(optionGroup, "thing"));
            assertWritten(grpDep, baos);

            assertEquals(grpValue == null ? "thing" : grpValue, commandLine.getOptionValue(optionGroup, thinger));
            assertWritten(grpDep, baos);

            assertEquals(grpValue, commandLine.getOptionValue(optionGroup, nullSupplier));
            assertWritten(grpDep, baos);

            // test other group arg
            assertNull(commandLine.getOptionValue(otherGroup));
            assertWritten(false, baos);

            assertEquals("thing", commandLine.getOptionValue(otherGroup, "thing"));
            assertWritten(false, baos);

            assertEquals("thing", commandLine.getOptionValue(otherGroup, thinger));
            assertWritten(false, baos);

            assertNull(commandLine.getOptionValue(otherGroup, nullSupplier));
            assertWritten(false, baos);

            // test null Group arg
            assertNull(commandLine.getOptionValue(nullGroup));
            assertWritten(false, baos);

            assertEquals("thing", commandLine.getOptionValue(nullGroup, "thing"));
            assertWritten(false, baos);

            assertEquals("thing", commandLine.getOptionValue(nullGroup, thinger));
            assertWritten(false, baos);

            assertNull(commandLine.getOptionValue(nullGroup, nullSupplier));
            assertWritten(false, baos);

            // test not an option
            assertNull(commandLine.getOptionValue("Nope"));
            assertWritten(false, baos);

            assertEquals("thing", commandLine.getOptionValue("Nope", "thing"));
            assertWritten(false, baos);

            assertEquals("thing", commandLine.getOptionValue("Nope", thinger));
            assertWritten(false, baos);

            assertNull(commandLine.getOptionValue("Nope", nullSupplier));
            assertWritten(false, baos);
        } finally {
            System.setOut(ps);
        }
    }

    @Test
    public void testNullOption() throws Exception {
        final Options options = new Options();
        final Option optI = Option.builder("i").hasArg().type(Number.class).build();
        final Option optF = Option.builder("f").hasArg().build();
        options.addOption(optI);
        options.addOption(optF);
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, new String[] {"-i", "123", "-f", "foo"});
        assertNull(cmd.getOptionValue((Option) null));
        assertNull(cmd.getParsedOptionValue((Option) null));
        assertNull(cmd.getOptionValue((OptionGroup) null));
        assertNull(cmd.getParsedOptionValue((OptionGroup) null));
    }
}
