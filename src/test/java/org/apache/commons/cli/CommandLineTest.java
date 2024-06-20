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

package org.apache.commons.cli;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("deprecation") // tests some deprecated classes
public class CommandLineTest {

    @Test
    public void testBuilder() {
        final CommandLine.Builder builder = new CommandLine.Builder();
        builder.addArg("foo").addArg("bar");
        builder.addOption(Option.builder("T").build());
        final CommandLine cmd = builder.build();

        assertEquals("foo", cmd.getArgs()[0]);
        assertEquals("bar", cmd.getArgList().get(1));
        assertEquals("T", cmd.getOptions()[0].getOpt());
    }

    @Test
    public void testBuilderNullArgs() {
        final CommandLine.Builder builder = new CommandLine.Builder();
        builder.addArg(null).addArg(null);
        builder.addOption(Option.builder("T").build());
        final CommandLine cmd = builder.build();

        assertEquals(0, cmd.getArgs().length);
        assertEquals("T", cmd.getOptions()[0].getOpt());
    }

    @Test
    public void testBuilderNullOption() {
        final CommandLine.Builder builder = new CommandLine.Builder();
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

    @Test
    public void testGetParsedOptionValue() throws Exception {
        final Options options = new Options();
        options.addOption(Option.builder("i").hasArg().type(Number.class).build());
        options.addOption(Option.builder("f").hasArg().build());

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, new String[] {"-i", "123", "-f", "foo"});

        assertEquals(123, ((Number) cmd.getParsedOptionValue("i")).intValue());
        assertEquals("foo", cmd.getParsedOptionValue("f"));
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

    // verifies that the deprecation handler has been called only once or not at all.
    void checkHandler(boolean optDep, List<Option> handler, Option opt) {
        if (optDep) {
            assertEquals(1, handler.size());
            assertEquals(opt, handler.get(0));
        } else {
            assertEquals( 0, handler.size());
        }
        handler.clear();
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
    public void getOptionValueTest(String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep, final String optValue, final boolean grpDep, final String grpValue, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final List<Option> handler = new ArrayList<>();
        final CommandLine commandLine = DefaultParser.builder().setDeprecatedHandler(handler::add).build().parse(options, args);
        final Supplier<String> thinger = () -> {return "thing";};
        OptionGroup otherGroup = new OptionGroup().addOption(Option.builder("o").longOpt("other").hasArg().build())
                .addOption(Option.builder().option("p").longOpt("part").hasArg().build());

        // test short option arg
        assertEquals(optValue, commandLine.getOptionValue(opt.getOpt()));
        checkHandler(optDep, handler, opt);

        // if null was expected then "thing" is the value
        assertEquals(optValue==null ? "thing" : optValue, commandLine.getOptionValue(opt.getOpt(), "thing"));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue==null ? "thing" : optValue, commandLine.getOptionValue(opt.getOpt(), thinger));
        checkHandler(optDep, handler, opt);

        // test long option arg
        assertEquals(optValue, commandLine.getOptionValue(opt.getLongOpt()));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue==null ? "thing" : optValue, commandLine.getOptionValue(opt.getLongOpt(), "thing"));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue==null ? "thing" : optValue, commandLine.getOptionValue(opt.getLongOpt(), thinger));
        checkHandler(optDep, handler, opt);


        // test Option arg
        assertEquals(optValue, commandLine.getOptionValue(opt));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue==null ? "thing" : optValue, commandLine.getOptionValue(opt, "thing"));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue==null ? "thing" : optValue, commandLine.getOptionValue(opt, thinger));
        checkHandler(optDep, handler, opt);

        // test OptionGroup arg
        assertEquals("thing", commandLine.getOptionValue(otherGroup, "thing"));
        checkHandler(false, handler, grpOpt);

        assertEquals("thing", commandLine.getOptionValue(otherGroup, thinger));
        checkHandler(false, handler, grpOpt);

        // test not an option
        assertNull(commandLine.getOptionValue("Nope"));
        checkHandler(false, handler, opt);

        assertEquals("thing", commandLine.getOptionValue("Nope", "thing"));
        checkHandler(false, handler, opt);

        assertEquals("thing", commandLine.getOptionValue("Nope", thinger));
        checkHandler(false, handler, opt);
    }

    private static Stream<Arguments> createOptionValueParameters() throws MalformedURLException, ParseException {
        List<Arguments> lst = new ArrayList<>();
        final Option optT = Option.builder().option("T").longOpt("tee").deprecated().optionalArg(true).build();
        final Option optU = Option.builder("U").longOpt("you").optionalArg(true).build();
        OptionGroup optionGroup = new OptionGroup().addOption(optT).addOption(optU);

        // T set
        lst.add(Arguments.of(new String[] {"-T"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "foo"}, optT, optionGroup, true, "foo", true, "foo", optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optT, optionGroup, true, null, true, null, optT ));
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
    public void getOptionValuesTest(String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep, final String[] optValue, final boolean grpDep, final String[] grpValue, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final List<Option> handler = new ArrayList<>();
        final CommandLine commandLine = DefaultParser.builder().setDeprecatedHandler(handler::add).build().parse(options, args);
        final String[] things = {"thing1", "thing2"};
        final Supplier<String[]> thinger = () -> {return new String[]{"thing1", "thing2"};};
        OptionGroup otherGroup = new OptionGroup().addOption(Option.builder("o").longOpt("other").hasArg().build())
                .addOption(Option.builder().option("p").longOpt("part").hasArg().build());

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
    }

    private static Stream<Arguments> createOptionValuesParameters() throws MalformedURLException, ParseException {
        List<Arguments> lst = new ArrayList<>();
        final Option optT = Option.builder().option("T").longOpt("tee").numberOfArgs(2).deprecated().optionalArg(true).build();
        final Option optU = Option.builder("U").longOpt("you").numberOfArgs(2).optionalArg(true).build();
        final OptionGroup optionGroup = new OptionGroup().addOption(optT).addOption(optU);

        String[] foobar = { "foo", "bar" };
        // T set
        lst.add(Arguments.of(new String[] {"-T"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "foo", "bar"}, optT, optionGroup, true, foobar, true, foobar, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optT, optionGroup, true, null, true, null, optT ));
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
    public void hasOptionTest(String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep, final boolean has, final boolean grpDep, final boolean hasGrp, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final List<Option> handler = new ArrayList<>();
        final CommandLine commandLine = DefaultParser.builder().setDeprecatedHandler(handler::add).build().parse(options, args);

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

        // test not an option
        assertFalse(commandLine.hasOption("Nope"));
        checkHandler(false, handler, opt);
    }

    private static Stream<Arguments> createHasOptionParameters() throws MalformedURLException, ParseException {
        List<Arguments> lst = new ArrayList<>();
        final Option optT = Option.builder().option("T").longOpt("tee").deprecated().optionalArg(true).build();
        final Option optU = Option.builder("U").longOpt("you").optionalArg(true).build();
        final OptionGroup optionGroup = new OptionGroup().addOption(optT).addOption(optU);

        String[] foobar = { "foo", "bar" };
        // T set
        lst.add(Arguments.of(new String[] {"-T"}, optT, optionGroup, true, true, true, true, optT));
        lst.add(Arguments.of(new String[] {"-T", "foo"}, optT, optionGroup, true, true, true, true, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optT, optionGroup, true, true, true, true, optT ));
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

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("createParsedOptionValueParameters")
    public void getParsedOptionValueTest(String[] args, final Option opt, final OptionGroup optionGroup, final boolean optDep, final Integer optValue, final boolean grpDep, final Integer grpValue, final Option grpOpt) throws ParseException {
        final Options options = new Options().addOptionGroup(optionGroup);
        final List<Option> handler = new ArrayList<>();
        final CommandLine commandLine = DefaultParser.builder().setDeprecatedHandler(handler::add).build().parse(options, args);
        final Supplier<Integer> thinger = () -> {return 2;};
        OptionGroup otherGroup = new OptionGroup().addOption(Option.builder("o").longOpt("other").hasArg().build())
                .addOption(Option.builder().option("p").longOpt("part").hasArg().build());
        Integer thing = 2;

        // test short option arg
        assertEquals(optValue, commandLine.getParsedOptionValue(opt.getOpt()));
        checkHandler(optDep, handler, opt);

        // if null was expected then "thing" is the value
        assertEquals(optValue==null ? thing : optValue, commandLine.getParsedOptionValue(opt.getOpt(), thing));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue==null ? thing : optValue, commandLine.getParsedOptionValue(opt.getOpt(), thinger));
        checkHandler(optDep, handler, opt);

        // test long option arg
        assertEquals(optValue, commandLine.getParsedOptionValue(opt.getLongOpt()));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue==null ? thing : optValue, commandLine.getParsedOptionValue(opt.getLongOpt(), thing));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue==null ? thing : optValue, commandLine.getParsedOptionValue(opt.getLongOpt(), thinger));
        checkHandler(optDep, handler, opt);


        // test Option arg
        assertEquals(optValue, commandLine.getParsedOptionValue(opt));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue==null ? thing : optValue, commandLine.getParsedOptionValue(opt, thing));
        checkHandler(optDep, handler, opt);

        assertEquals(optValue==null ? thing : optValue, commandLine.getParsedOptionValue(opt, thinger));
        checkHandler(optDep, handler, opt);

        // test OptionGroup arg
        assertEquals(thing, commandLine.getParsedOptionValue(otherGroup, thing));
        checkHandler(false, handler, grpOpt);

        assertEquals(thing, commandLine.getParsedOptionValue(otherGroup, thinger));
        checkHandler(false, handler, grpOpt);

        // test not an option
        assertNull(commandLine.getParsedOptionValue("Nope"));
        checkHandler(false, handler, opt);

        assertEquals(thing, commandLine.getParsedOptionValue("Nope", thing));
        checkHandler(false, handler, opt);

        assertEquals(thing, commandLine.getParsedOptionValue("Nope", thinger));
        checkHandler(false, handler, opt);
    }

    private static Stream<Arguments> createParsedOptionValueParameters() throws MalformedURLException, ParseException {
        List<Arguments> lst = new ArrayList<>();
        final Option optT = Option.builder().option("T").longOpt("tee").deprecated().type(Integer.class).optionalArg(true).build();
        final Option optU = Option.builder("U").longOpt("you").type(Integer.class).optionalArg(true).build();
        OptionGroup optionGroup = new OptionGroup().addOption(optT).addOption(optU);
        Integer expected = Integer.valueOf(1);

        // T set
        lst.add(Arguments.of(new String[] {"-T"}, optT, optionGroup, true, null, true, null, optT));
        lst.add(Arguments.of(new String[] {"-T", "1"}, optT, optionGroup, true, expected, true, expected, optT));
        lst.add(Arguments.of(new String[] {"--tee"}, optT, optionGroup, true, null, true, null, optT ));
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

}
