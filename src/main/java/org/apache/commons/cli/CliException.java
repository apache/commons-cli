package org.apache.commons.cli;

/**
 * Base class for exceptions related to command-line parsing.
 */
public class CliException extends ParseException {
    private static final long serialVersionUID = 1L;

    /** The option related to the exception */
    protected Option option;

    public CliException(String message) {
        super(message);
    }

    public Option getOption() {
        return option;
    }
}

