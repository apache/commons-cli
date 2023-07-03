from org.apache.commons.cli import DefaultParser
from org.apache.commons.cli import Options
from org.apache.commons.cli import Option
import pytest

def test_disable_partial_matching():
    parser = DefaultParser(False)

    options = Options()

    options.addOption(Option("d", "debug", False, "Turn on debug."))
    options.addOption(Option("e", "extract", False, "Turn on extract."))
    options.addOption(Option("o", "option", True, "Turn on option with argument."))

    line = parser.parse(options, ["-de", "--option=foobar"])

    assert line.hasOption("debug")
    assert line.hasOption("extract")
    assert line.hasOption("option")


def test_regular_partial_matching():
    parser = DefaultParser()

    options = Options()

    options.addOption(Option("d", "debug", False, "Turn on debug."))
    options.addOption(Option("e", "extract", False, "Turn on extract."))
    options.addOption(Option("o", "option", True, "Turn on option with argument."))

    line = parser.parse(options, ["-de", "--option=foobar"])
    
    assert line.hasOption("debug")
    assert not line.hasOption("extract")
    assert line.hasOption("option")


