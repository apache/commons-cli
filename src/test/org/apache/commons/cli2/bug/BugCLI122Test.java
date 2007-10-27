package org.apache.commons.cli2.bug;

import junit.framework.TestCase;
import org.apache.commons.cli2.*;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.validation.FileValidator;

/**
 * @author brianegge
 */
public class BugCLI122Test extends TestCase {
    public void testArgumentWhichStartsWithDash() throws OptionException {
        Argument wdArg = new ArgumentBuilder()
                .withName("anything")
                .withMaximum(1)
                .withMinimum(1)
                .withInitialSeparator('=')
                .create();

        Option wdOpt = new DefaultOptionBuilder().withArgument(wdArg)
                .withDescription("anything, foo or -foo")
                .withLongName("argument")
                .withShortName("a")
                .create();

        Group group = new GroupBuilder().withOption(wdOpt).create();

        Parser p = new Parser();
        p.setGroup(group);
        CommandLine normal = p.parse (new String[]{"-a", "foo"});
        assertNotNull(normal);
        assertEquals(normal.getValue(wdOpt), "foo");

        CommandLine withDash = p.parse (new String[]{"--argument", "\"-foo\""});
        assertNotNull(withDash);
        assertEquals("-foo", withDash.getValue(wdOpt));

        CommandLine withDashAndEquals = p.parse (new String[]{"--argument=-foo"});
        assertNotNull(withDashAndEquals);
        assertEquals("-foo", withDashAndEquals.getValue(wdOpt));
    }
}
