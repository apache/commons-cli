/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.commons.cli.config;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 */
public class CommandLineConfigurationTest
{

    /**
     * Test that once the configuration is parsed, the command line
     * configuration is created with the specified arguments and the listener
     * has been updated with the values specified by the CLI call. Since the
     * configuration is of type 'both' because options are specified as
     * char/string, we expect both short and long options to have been updated
     * in the listener.
     */
    @Test
    public void testProcessShortAndLongOptions() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_001_short_and_long_options.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        // short options:
        assertEquals("80", listener.getOptions().get("p"));
        assertEquals("192.168.1.2", listener.getOptions().get("h"));
        assertEquals("/tmp", listener.getOptions().get("P"));
        assertTrue(listener.getOptions().containsKey("F"));
        // long options:
        assertEquals("80", listener.getOptions().get("port"));
        assertEquals("192.168.1.2", listener.getOptions().get("host"));
        assertEquals("/tmp", listener.getOptions().get("path"));
        assertTrue(listener.getOptions().containsKey("fail"));
    }

    /**
     * Test of removeOptionListener method, ensuring the listener is not updated
     * once the process is invoked.
     */
    @Test
    public void testRemoveOptionListener() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_001_short_and_long_options.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp".split(" ");
        cliConfig.removeOptionListener(listener);
        cliConfig.process(is, "UTF-8", arguments);
        // short options:
        assertEquals(null, listener.getOptions().get("p"));
        assertEquals(null, listener.getOptions().get("h"));
        assertEquals(null, listener.getOptions().get("P"));
        assertFalse(listener.getOptions().containsKey("F"));
        // long options:
        assertEquals(null, listener.getOptions().get("port"));
        assertEquals(null, listener.getOptions().get("host"));
        assertEquals(null, listener.getOptions().get("path"));
        assertFalse(listener.getOptions().containsKey("fail"));
    }

    /**
     * Test that once the configuration is parsed, the command line
     * configuration is created with the specified arguments and the listener
     * has been updated with the values specified by the CLI call. Since the
     * configuration is of type 'short' because options are specified as a char,
     * we expect only short options to have been updated in the listener.
     */
    @Test
    public void testProcessShortOptionsConfig() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_002_short_options.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        // short options:
        assertEquals("80", listener.getOptions().get("p"));
        assertEquals("192.168.1.2", listener.getOptions().get("h"));
        assertEquals("/tmp", listener.getOptions().get("P"));
        assertTrue(listener.getOptions().containsKey("F"));
        // these long options should not be set:
        assertEquals(null, listener.getOptions().get("port"));
        assertEquals(null, listener.getOptions().get("host"));
        assertEquals(null, listener.getOptions().get("path"));
        assertFalse(listener.getOptions().containsKey("fail"));
    }

    /**
     * Test that once the configuration is parsed, the command line
     * configuration is created with the specified arguments and the listener
     * has been updated with the values specified by the CLI call. Since the
     * configuration is of type 'long' because options are specified as a
     * string, we expect only long options to have been updated in the listener.
     */
    @Test
    public void testProcessLongOptionsConfig() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_003_long_options.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--fail --host 192.168.1.2 --port 80 --path /tmp".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        // long options:
        assertEquals("80", listener.getOptions().get("port"));
        assertEquals("192.168.1.2", listener.getOptions().get("host"));
        assertEquals("/tmp", listener.getOptions().get("path"));
        assertTrue(listener.getOptions().containsKey("fail"));
        // these short options should not be set:
        assertEquals(null, listener.getOptions().get("p"));
        assertEquals(null, listener.getOptions().get("h"));
        assertEquals(null, listener.getOptions().get("P"));
        assertFalse(listener.getOptions().containsKey("F"));
    }

    /**
     * Test that once the configuration is parsed, the command line
     * configuration is created with the specified arguments and the listener
     * has been updated with the values specified by the CLI call. Since the
     * configuration is of type 'both' because options are specified as
     * char/string, we expect both short and long options to have been updated
     * in the listener.
     */
    @Test
    public void testGetOptions() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_001_short_and_long_options.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-F -h 192.168.1.2 -p 80 -P /tmp".split(" ");
        cliConfig.process(is, "UTF-8", arguments);
        List<Option> options = cliConfig.getOptions();
        // short options:
        assertEquals("80", getOptionValue(options, "p"));
        assertEquals("192.168.1.2", getOptionValue(options, "h"));
        assertEquals("/tmp", getOptionValue(options, "P"));
        assertNotNull(getOptionValue(options, "F"));
    }

    /**
     * Test of process method, ensuring that when an unknown option is provided,
     * the appropriate exception is thrown.
     */
    @Test
    public void testProcessFailsParseException() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_001_short_and_long_options.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--no-such-option -F -h 192.168.1.2".split(" ");
        try
        {
            cliConfig.process(is, "UTF-8", arguments);
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("Unrecognized option"));
            assertEquals(UnrecognizedOptionException.class,
                    ex.getCause().getClass());
        }
    }

    /**
     * Test that invoking help prints the help then quits with exit status 0.
     */
    @Test
    public void testProcessPrintHelpShortOption() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_015_print_short_option_help.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-h".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Print this then quit."));
        assertTrue(output.contains("Fail if no connection made, rather than retrying."));
        assertTrue(output.contains("Specify the host; optional. Use localhost if not set."));
        assertTrue(output.contains(" Protocol is optional, assumes HTTP."));
        assertTrue(output.contains("Port number to use. Required."));
        ps.close();
        os.close();
    }

    /**
     * Test that specifying auto-print help via the command line configuration
     * without the arguments containing the help option does not print the help.
     */
    @Test
    public void testProcessDoesNotPrintHelp() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_015_print_short_option_help.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "-f -H localhost".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertFalse(output.contains("Print this then quit."));
        assertFalse(output.contains("Fail if no connection made, rather than retrying."));
        assertFalse(output.contains("Specify the host; optional. Use localhost if not set."));
        assertFalse(output.contains(" Protocol is optional, assumes HTTP."));
        assertFalse(output.contains("Port number to use. Required."));
        ps.close();
        os.close();
    }

    /**
     * Test that invoking help prints the help then quits with exit status 0.
     */
    @Test
    public void testProcessPrintHelpLongOption() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_016_print_long_option_help.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help".split(" ");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("Print this then quit."));
        assertTrue(output.contains("Fail if no connection made, rather than retrying."));
        assertTrue(output.contains("Specify the host; optional. Use localhost if not set."));
        assertTrue(output.contains(" Protocol is optional, assumes HTTP."));
        assertTrue(output.contains("Port number to use. Required."));
        ps.close();
        os.close();
    }

    /**
     * Test that invoking help when hasArg = true throws an error.
     */
    @Test
    public void testProcessPrintHelpHasArgThrowsError() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_017_bad_help_option.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help foobarbaz".split(" ");
        try
        {
            cliConfig.process(is, "UTF-8", arguments);
            fail("Expected exception");
        }
        catch (ConfigurationException ex)
        {
            assertEquals("Error: Option help cannot have an argument"
                    + " associated with it.", ex.getMessage());
        }
    }

    /**
     * Test that the specified header and footer are included.
     */
    @Test
    public void testHelpHeaderFooter() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_018_header_and_footer.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help".split(" ");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("foo_command"));
        assertTrue(output.contains("Show some useful information"));
        assertTrue(output.contains("Copyright Apache Software Foundation"));
        ps.close();
        os.close();
    }

    /**
     * Test that the specified escaped header and footer are included.
     */
    @Test
    public void testHelpHeaderFooterEscaped() throws Exception
    {
        ConfigListener listener = new ConfigListener();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_019_header_and_footer_escaped.conf");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(listener);
        String[] arguments = "--help".split(" ");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
        cliConfig.process(is, "UTF-8", arguments);
        String output = os.toString("UTF8");
        assertTrue(output.contains("foo_command"));
        assertTrue(output.contains("Show some useful information, with some"
                + " extra escaped lines"));
        assertTrue(output.contains("Copyright Apache Software Foundation"
                + " Submit escaped lines to System.out()"));
    }

    /**
     * Get the option value from the list where the option name equals the
     * specified key.
     *
     * @param options non-{@code null}, non-empty option list.
     *
     * @param key non-{@code null} key to search for.
     *
     * @return the option value if it could be retrieved, or the empty string if
     * the option does not have an argument; {@code null} otherwise.
     */
    private String getOptionValue(final List<Option> options, String key)
    {
        String result = null;
        for (Option option : options)
        {
            if (key.equals(option.getOpt()))
            {
                if (option.hasArg())
                {
                    result = option.getValue();
                }
                else
                {
                    result = "";
                }
            }
        }
        return result;
    }

}
