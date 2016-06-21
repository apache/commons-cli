package org.apache.commons.cli.bug;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BugCLI266Test {

    private List<String> insertedOrder  =   Arrays.asList("h", "d", "f", "x", "s", "p", "t", "w", "o");
    private List<String> sortOrder      =   Arrays.asList("d", "f", "h", "o", "p", "s", "t", "w", "x");

    @Test
    public void testOptionComparatorDefaultOrder() throws ParseException {
        HelpFormatter formatter = new HelpFormatter();
        List<Option> options = new ArrayList<Option>(getOptions().getOptions());
        Collections.sort(options, formatter.getOptionComparator());
        int i = 0;
        for(Option o: options) {
            Assert.assertEquals(o.getOpt(), sortOrder.get(i));
            i++;
        }
    }

    @Test
    public void testOptionComparatorInsertedOrder() throws ParseException {
        Collection<Option> options = getOptions().getOptions();
        int i = 0;
        for(Option o: options) {
            Assert.assertEquals(o.getOpt(), insertedOrder.get(i));
            i++;
        }
    }

    private Options getOptions() {
        Options options = new Options();
        Option help = Option.builder("h")
                .longOpt("help")
                .desc("Prints this help message")
                .build();
        options.addOption(help);

        buildOptionsGroup(options);

        Option t = Option.builder("t")
                .required()
                .hasArg()
                .argName("file")
                .build();
        Option w = Option.builder("w")
                .required()
                .hasArg()
                .argName("word")
                .build();
        Option o = Option.builder("o")
                .hasArg()
                .argName("directory")
                .build();
        options.addOption(t);
        options.addOption(w);
        options.addOption(o);
        return options;
    }

    private void buildOptionsGroup(Options options) {
        OptionGroup firstGroup = new OptionGroup();
        OptionGroup secondGroup = new OptionGroup();
        firstGroup.setRequired(true);
        secondGroup.setRequired(true);

        firstGroup.addOption(Option.builder("d")
                .longOpt("db")
                .hasArg()
                .argName("table-name")
                .build());
        firstGroup.addOption(Option.builder("f")
                .longOpt("flat-file")
                .hasArg()
                .argName("input.csv")
                .build());
        options.addOptionGroup(firstGroup);

        secondGroup.addOption(Option.builder("x")
                .hasArg()
                .argName("arg1")
                .build());
        secondGroup.addOption(Option.builder("s")
                .build());
        secondGroup.addOption(Option.builder("p")
                .hasArg()
                .argName("arg1")
                .build());
        options.addOptionGroup(secondGroup);
    }
}
