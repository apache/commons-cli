package org.apache.commons.cli;

public class OptionBuilder {

    private static String longopt;
    private static String description;
    private static boolean arg;
    private static boolean required;
    private static boolean multipleArgs;
    private static Object type;

    private static OptionBuilder instance = new OptionBuilder();

    // private constructor
    private OptionBuilder() {
    }

    private static void reset() {
        description = null;
        longopt = null;
        type = null;
        arg = false;
        required = false;
        multipleArgs = false;
    }

    public static OptionBuilder withLongOpt( String longopt ) {
        instance.longopt = longopt;
        return instance;
    }

    public static OptionBuilder hasArg( ) {
        instance.arg = true;
        return instance;
    }

    public static OptionBuilder isRequired( ) {
        instance.required = true;
        return instance;
    }

    public static OptionBuilder hasMultipleArgs( ) {
        instance.multipleArgs = true;
        return instance;
    }

    public static OptionBuilder withType( Object type ) {
        instance.type = type;
        return instance;
    }

    public static OptionBuilder withDescription( String description ) {
        instance.description = description;
        return instance;
    }

    public static Option create( char opt )
    throws IllegalArgumentException
    {
        return create( String.valueOf( opt ) );
    }

    public static Option create( String opt ) 
    throws IllegalArgumentException
    {
        Option option = new Option( opt, arg, description );
        option.setLongOpt( longopt );
        option.setRequired( required );
        option.setMultipleArgs( multipleArgs );
        option.setType( type );
        instance.reset();
        return option;
    }
}