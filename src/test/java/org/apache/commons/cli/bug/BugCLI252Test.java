package org.apache.commons.cli.bug;

import org.apache.commons.cli.*;
import org.junit.Test;

public class BugCLI252Test extends DefaultParserTest {

    @Test
    public void testExactOptionNameMatch() throws ParseException {
        new DefaultParser().parse(getOptions(), new String[]{"--prefix"});
    }

    @Test(expected = AmbiguousOptionException.class)
    public void testAmbiquousOptionName() throws ParseException {
        new DefaultParser().parse(getOptions(), new String[]{"--pref"});
    }

    private Options getOptions() {
        Options options = new Options();
        options.addOption(Option.builder().longOpt("prefix").build());
        options.addOption(Option.builder().longOpt("prefixplusplus").build());
        return options;
    }

}
