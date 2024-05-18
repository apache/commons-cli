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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

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
    public void testDeprecatedDefaultOption() {
        final CommandLine.Builder builder = new CommandLine.Builder();
        builder.addArg("foo").addArg("bar");
        final Option opt = Option.builder().option("T").deprecated().build();
        builder.addOption(opt);
        final AtomicReference<Option> handler = new AtomicReference<>();
        final CommandLine cmd = builder.build();
        cmd.getOptionValue(opt.getOpt());
        handler.set(null);
        cmd.getOptionValue("Nope");
        assertNull(handler.get());
    }

    @Test
    public void testDeprecatedOption() {
        final CommandLine.Builder builder = new CommandLine.Builder();
        builder.addArg("foo").addArg("bar");
        final Option opt = Option.builder().option("T").longOpt("tee").deprecated().build();
        builder.addOption(opt);
        // verify one and only one call
        final List<Option> handler = new ArrayList<>();
        final CommandLine cmd = builder.setDeprecatedHandler(handler::add).build();
        // test short option arg
        cmd.getOptionValue(opt.getOpt());
        assertEquals(1, handler.size());
        assertSame(opt, handler.get(0));
        handler.clear();

        // test long option arg
        cmd.getOptionValue(opt.getLongOpt());
        assertEquals(1, handler.size());
        assertSame(opt, handler.get(0));
        handler.clear();

        // test Option arg
        cmd.getOptionValue(opt);
        assertEquals(1, handler.size());
        assertSame(opt, handler.get(0));
        handler.clear();

        // test not an option
        cmd.getOptionValue("Nope");
        assertEquals(0, handler.size());
    }

    @Test
    public void testDeprecatedParsedOptionValue() throws ParseException {
        final CommandLine.Builder builder = new CommandLine.Builder();
        builder.addArg("foo").addArg("bar");
        final Option opt = Option.builder().option("T").longOpt("tee").deprecated().build();
        builder.addOption(opt);
        // verify one and only one call
        final List<Option> handler = new ArrayList<>();
        final CommandLine cmd = builder.setDeprecatedHandler(handler::add).build();

        // test short option arg
        cmd.getParsedOptionValue(opt.getOpt());
        assertEquals(1, handler.size());
        assertSame(opt, handler.get(0));
        handler.clear();

        // test long option arg
        cmd.getParsedOptionValue(opt.getLongOpt());
        assertEquals(1, handler.size());
        assertSame(opt, handler.get(0));
        handler.clear();

        // test option arg
        cmd.getParsedOptionValue(opt);
        assertEquals(1, handler.size());
        assertSame(opt, handler.get(0));
        handler.clear();


        // test not an option
        cmd.getParsedOptionValue("Nope");
        assertEquals(0, handler.size());
    }

    @Test
    public void testGetOptionProperties() throws Exception {
        final String[] args = {"-Dparam1=value1", "-Dparam2=value2", "-Dparam3", "-Dparam4=value4", "-D", "--property", "foo=bar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withValueSeparator().hasOptionalArgs(2).create('D'));
        options.addOption(OptionBuilder.withValueSeparator().hasArgs(2).withLongOpt("property").create());

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
        final Option optionD = OptionBuilder.withValueSeparator().hasOptionalArgs(2).create('D');
        final Option optionProperty = OptionBuilder.withValueSeparator().hasArgs(2).withLongOpt("property").create();
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
        options.addOption(OptionBuilder.hasArg().withType(Number.class).create("i"));
        options.addOption(OptionBuilder.hasArg().create("f"));

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, new String[] {"-i", "123", "-f", "foo"});

        assertEquals(123, ((Number) cmd.getParsedOptionValue("i")).intValue());
        assertEquals("foo", cmd.getParsedOptionValue("f"));
    }

    @Test
    public void testGetParsedOptionValueUsingDefault() throws Exception {
        final Options options = new Options();
        final Option optI = Option.builder("i").hasArg().type(Number.class).build();
        final Option optF = Option.builder("f").hasArg().build();
        options.addOption(optI);
        options.addOption(optF);

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, new String[] {"-i", "123"});
        final Supplier<String> nullSupplier = null;

        assertEquals(123, ((Number) cmd.getParsedOptionValue(optI)).intValue());
        assertEquals("foo", cmd.getParsedOptionValue(optF, "foo"));
        assertEquals("foo", cmd.getParsedOptionValue(optF, () -> "foo"));
        assertNull(cmd.getParsedOptionValue(optF, null));
        assertNull(cmd.getParsedOptionValue(optF, nullSupplier));
        assertNull(cmd.getParsedOptionValue(optF, () -> null));

        assertEquals("foo", cmd.getParsedOptionValue("f", "foo"));
        assertEquals("foo", cmd.getParsedOptionValue("f", () -> "foo"));
        assertNull(cmd.getParsedOptionValue("f", null));
        assertNull(cmd.getParsedOptionValue("f", nullSupplier));
        assertNull(cmd.getParsedOptionValue("f", () -> null));

        assertEquals("foo", cmd.getParsedOptionValue('f', "foo"));
        assertEquals("foo", cmd.getParsedOptionValue('f', () -> "foo"));
        assertNull(cmd.getParsedOptionValue('f', null));
        assertNull(cmd.getParsedOptionValue('f', nullSupplier));
        assertNull(cmd.getParsedOptionValue('f', () -> null));

    }

    @Test
    public void testGetParsedOptionValueWithChar() throws Exception {
        final Options options = new Options();
        options.addOption(Option.builder("i").hasArg().type(Number.class).build());
        options.addOption(Option.builder("f").hasArg().build());

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, new String[] {"-i", "123", "-f", "foo"});

        assertEquals(123, ((Number) cmd.getParsedOptionValue('i')).intValue());
        assertEquals("foo", cmd.getParsedOptionValue('f'));
    }

    @Test
    public void testGetParsedOptionValueWithOption() throws Exception {
        final Options options = new Options();
        final Option optI = Option.builder("i").hasArg().type(Number.class).build();
        final Option optF = Option.builder("f").hasArg().build();
        options.addOption(optI);
        options.addOption(optF);

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, new String[] {"-i", "123", "-f", "foo"});

        assertEquals(123, ((Number) cmd.getParsedOptionValue(optI)).intValue());
        assertEquals("foo", cmd.getParsedOptionValue(optF));
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
    }
}
