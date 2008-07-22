package org.apache.commons.cli2.bug;

import junit.framework.TestCase;
import org.apache.commons.cli2.option.PropertyOption;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.builder.GroupBuilder;

/**
 * @author brianegge
 */
public class BugCLI126Test extends TestCase {
    public void testMultiplePropertyArgs() throws OptionException {
        PropertyOption conf = new PropertyOption("-P", "Properties for this process", 1);
        PropertyOption env = new PropertyOption("-C", "Properties for child processes", 2);
        GroupBuilder builder = new GroupBuilder();
        Group options = builder.withOption(conf).withOption(env).create();

        Parser parser = new Parser();
        parser.setGroup(options);
        CommandLine line =
            parser.parseAndHelp(
                new String[] {
                    "-Phome=.",
                    "-Chome=/"
                    });
        assertEquals(".", line.getProperty(conf, "home"));
        assertEquals("/", line.getProperty(env, "home"));
    }
}
