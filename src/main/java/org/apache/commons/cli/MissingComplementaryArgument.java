package org.apache.commons.cli;

/**
 * Thrown when during the parsing we identify that there are missing complementary arguments
 * for an option that we have used
 */
public class MissingComplementaryArgument extends ParseException {

    /**
     * This exception {@code serialVersionUID}.
     */
    private static final long serialVersionUID = -7098538588704965232L;

    /**
     * Construct a new <code>ParseException</code>
     * with the specified detail message.
     *
     * @param message the detail message
     */
    public MissingComplementaryArgument(String message) {
        super(message);
    }
}
