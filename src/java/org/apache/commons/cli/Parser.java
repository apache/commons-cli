package org.apache.commons.cli;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public abstract class Parser implements CommandLineParser {

    private CommandLine cmd;
    private Options options;
    private Map requiredOptions;

    abstract protected String[] flatten( Options opts, 
                                         String[] args, 
                                         boolean stopAtNonOption );

    public CommandLine parse( Options opts, String[] args ) 
    throws ParseException 
    {
        return parse( opts, args, false );
    }

    public CommandLine parse( Options opts, 
                              String[] args, 
                              boolean stopAtNonOption ) 
    throws ParseException 
    {
        options = opts;
        requiredOptions = options.getRequiredOptions();
        String[] tokens = flatten( opts, args, stopAtNonOption );
        List tokenList = Arrays.asList( tokens );
        ListIterator iterator = tokenList.listIterator();
        cmd = new CommandLine();
        boolean eatTheRest = false;
        while( iterator.hasNext() ) {
            String t = (String)iterator.next();
            if( "--".equals( t ) ) {
                eatTheRest = true;
            }
            else if( t.startsWith( "-" ) ) {
                if( t.length() == 1 ) {
                    // not an option, so just drop it on the argument list
                    if ( stopAtNonOption ) {
                        eatTheRest = true;
                    }
                    else {
                        cmd.addArg( t );
                    }
                }
                else {
                    processOption( t, iterator );
                }
            }
            else {
                cmd.addArg( t );
                if( stopAtNonOption ) {
                    eatTheRest = true;
                }
            }

            if( eatTheRest ) {
                while( iterator.hasNext() ) {
                    cmd.addArg( (String)iterator.next() );
                }
            }
        }
        checkRequiredOptions();
        return cmd;
    }

    private void checkRequiredOptions( ) 
    throws ParseException {

        // if there are required options that have not been
        // processsed
        if( requiredOptions.size() > 0 ) {
            Iterator iter = requiredOptions.values().iterator();
            StringBuffer buff = new StringBuffer();

            // loop through the required options
            while( iter.hasNext() ) {
                Option missing = (Option)iter.next();
                buff.append( "-" );
                buff.append( missing.getOpt() );
                buff.append( " " );
                buff.append( missing.getDescription() );
            }

            // throw the MissingOptionException
            throw new MissingOptionException( buff.toString() );
        }
    }

    public void processArgs( Option opt, ListIterator iter ) 
    throws ParseException 
    {
        if( !iter.hasNext() && !opt.hasOptionalArg() ) {
            throw new MissingArgumentException( "no argument for:" + opt.getOpt() );
        }
        // loop until an option is found
        while( iter.hasNext() ) {
            String var = (String)iter.next();
            if( options.hasOption( var ) ) {
                iter.previous();
                break;
            }

            // its a value
            else {
                if( !opt.addValue( var ) ) {
                    iter.previous();
                    break;
                }
            }
        }
    }

    private void processOption( String arg, ListIterator iter ) 
    throws ParseException
    {
        // get the option represented by arg
        Option opt = null;

        boolean hasOption = options.hasOption( arg );

        // if there is no option throw an UnrecognisedOptionException
        if( !hasOption ) {
            throw new UnrecognizedOptionException("Unrecognized option: " + arg);
        }
        else {
            opt = (Option) options.getOption( arg );
        }

        // if the option is a required option remove the option from
        // the requiredOptions list
        if ( opt.isRequired() ) {
            requiredOptions.remove( "-" + opt.getOpt() );
        }

        // if the option is in an OptionGroup make that option the selected
        // option of the group
        if ( options.getOptionGroup( opt ) != null ) {
            ( (OptionGroup)( options.getOptionGroup( opt ) ) ).setSelected( opt );
        }

        // if the option takes an argument value
        if ( opt.hasArg() ) {
            processArgs( opt, iter );
        }

        // set the option on the command line
        cmd.setOpt( opt );
    }
}