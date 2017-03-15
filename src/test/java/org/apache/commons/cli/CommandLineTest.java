/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.Test;

@SuppressWarnings("deprecation") // tests some deprecated classes
public class CommandLineTest
{
    @Test
    public void testGetOptionProperties() throws Exception
    {
        String[] args = new String[] { "-Dparam1=value1", "-Dparam2=value2", "-Dparam3", "-Dparam4=value4", "-D", "--property", "foo=bar" };

        Options options = new Options();
        options.addOption(OptionBuilder.withValueSeparator().hasOptionalArgs(2).create('D'));
        options.addOption(OptionBuilder.withValueSeparator().hasArgs(2).withLongOpt("property").create());

        Parser parser = new GnuParser();
        CommandLine cl = parser.parse(options, args);

        Properties props = cl.getOptionProperties("D");
        assertNotNull("null properties", props);
        assertEquals("number of properties in " + props, 4, props.size());
        assertEquals("property 1", "value1", props.getProperty("param1"));
        assertEquals("property 2", "value2", props.getProperty("param2"));
        assertEquals("property 3", "true", props.getProperty("param3"));
        assertEquals("property 4", "value4", props.getProperty("param4"));

        assertEquals("property with long format", "bar", cl.getOptionProperties("property").getProperty("foo"));
    }
    
    @Test
    public void testGetOptionPropertiesWithOption() throws Exception
    {
        String[] args = new String[] { "-Dparam1=value1", "-Dparam2=value2", "-Dparam3", "-Dparam4=value4", "-D", "--property", "foo=bar" };

        Options options = new Options();
        Option option_D = OptionBuilder.withValueSeparator().hasOptionalArgs(2).create('D');
        Option option_property = OptionBuilder.withValueSeparator().hasArgs(2).withLongOpt("property").create();
        options.addOption(option_D);
        options.addOption(option_property);

        Parser parser = new GnuParser();
        CommandLine cl = parser.parse(options, args);

        Properties props = cl.getOptionProperties(option_D);
        assertNotNull("null properties", props);
        assertEquals("number of properties in " + props, 4, props.size());
        assertEquals("property 1", "value1", props.getProperty("param1"));
        assertEquals("property 2", "value2", props.getProperty("param2"));
        assertEquals("property 3", "true", props.getProperty("param3"));
        assertEquals("property 4", "value4", props.getProperty("param4"));

        assertEquals("property with long format", "bar", cl.getOptionProperties(option_property).getProperty("foo"));
    }

    @Test
    public void testGetOptions()
    {
        CommandLine cmd = new CommandLine();
        assertNotNull(cmd.getOptions());
        assertEquals(0, cmd.getOptions().length);
        
        cmd.addOption(new Option("a", null));
        cmd.addOption(new Option("b", null));
        cmd.addOption(new Option("c", null));
        
        assertEquals(3, cmd.getOptions().length);
    }

    @Test
    public void testGetParsedOptionValue() throws Exception {
        Options options = new Options();
        options.addOption(OptionBuilder.hasArg().withType(Number.class).create("i"));
        options.addOption(OptionBuilder.hasArg().create("f"));
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, new String[] { "-i", "123", "-f", "foo" });
        
        assertEquals(123, ((Number) cmd.getParsedOptionValue("i")).intValue());
        assertEquals("foo", cmd.getParsedOptionValue("f"));
    }
    
    @Test
    public void testGetParsedOptionValueWithChar() throws Exception {
        Options options = new Options();
        options.addOption(Option.builder("i").hasArg().type(Number.class).build());
        options.addOption(Option.builder("f").hasArg().build());
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, new String[] { "-i", "123", "-f", "foo" });
        
        assertEquals(123, ((Number) cmd.getParsedOptionValue('i')).intValue());
        assertEquals("foo", cmd.getParsedOptionValue('f'));
    }
    
    @Test
    public void testGetParsedOptionValueWithOption() throws Exception {
        Options options = new Options();
        Option opt_i = Option.builder("i").hasArg().type(Number.class).build();
        Option opt_f = Option.builder("f").hasArg().build();
        options.addOption(opt_i);
        options.addOption(opt_f);
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, new String[] { "-i", "123", "-f", "foo" });
        
        assertEquals(123, ((Number) cmd.getParsedOptionValue(opt_i)).intValue());
        assertEquals("foo", cmd.getParsedOptionValue(opt_f));
    }
    
    @Test
    public void testNullhOption() throws Exception {
        Options options = new Options();
        Option opt_i = Option.builder("i").hasArg().type(Number.class).build();
        Option opt_f = Option.builder("f").hasArg().build();
        options.addOption(opt_i);
        options.addOption(opt_f);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, new String[] { "-i", "123", "-f", "foo" });
        assertNull(cmd.getOptionValue((Option)null));
        assertNull(cmd.getParsedOptionValue((Option)null));
    }

    @Test
    public void testBuilder()
        throws Exception
    {
        CommandLine.Builder builder = new CommandLine.Builder();
        builder.addArg( "foo" ).addArg( "bar" );
        builder.addOption( Option.builder( "T" ).build() );
        CommandLine cmd = builder.build();

        assertEquals( "foo", cmd.getArgs()[0] );
        assertEquals( "bar", cmd.getArgList().get( 1 ) );
        assertEquals( "T", cmd.getOptions()[0].getOpt() );
    }
}
