package org.apache.commons.cli;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author John Keyes (jbjk at mac.com)
 */
public class PosixParser extends Parser {

    private ArrayList tokens = new ArrayList();
    private boolean eatTheRest;
    private Option currentOption;
    private Options options;

    private void init() {
        eatTheRest = false;
        tokens.clear();
        currentOption = null;
    }

    protected String[] flatten( Options options, 
                                String[] arguments, 
                                boolean stopAtNonOption )
    {
        init();
        this.options = options;

        // an iterator for the command line tokens
        Iterator iter = Arrays.asList( arguments ).iterator();
        String token = null;
        
        // process each command line token
        while ( iter.hasNext() ) {

            // get the next command line token
            token = (String) iter.next();

            // handle SPECIAL TOKEN
            if( token.startsWith( "--" ) ) {
                tokens.add( token );
            }
            // single hyphen
            else if( "-".equals( token ) ) {
                processSingleHyphen( token );
            }
            else if( token.startsWith( "-" ) ) {
                int tokenLength = token.length();
                if( tokenLength == 2 ) {
                    processOptionToken( token, stopAtNonOption );
                }
                // requires bursting
                else {
                    burstToken( token, stopAtNonOption );
                }
            }
            else {
                if( stopAtNonOption ) {
                    process( token );
                }
                else {
                    tokens.add( token );
                }
            }

            gobble( iter );
        }

        return (String[])tokens.toArray( new String[] {} );
    }

    private void gobble( Iterator iter ) {
        if( eatTheRest ) {
            while( iter.hasNext() ) {
                tokens.add( iter.next() );
            }
        }
    }

    private void process( String value ) {
        if( currentOption != null && currentOption.hasArg() ) {
            if( currentOption.hasArg() ) {
                tokens.add( value );
                currentOption = null;
            }
            else if (currentOption.hasArgs() ) {
                tokens.add( value );
            }
        }
        else {
            eatTheRest = true;
            tokens.add( "--" );
            tokens.add( value );
        }
    }

    private void processSingleHyphen( String hyphen ) {
        tokens.add( hyphen );
    }

    private void processOptionToken( String token, boolean stop ) {
        if( this.options.hasOption( token ) ) {
            currentOption = this.options.getOption( token );
            tokens.add( token );
        }
        else if( stop ) {
            eatTheRest = true;
        }
    }

    private void burstToken( String token, boolean stop ) {
        int tokenLength = token.length();

        for( int i = 1; i < tokenLength; i++) {
            String ch = String.valueOf( token.charAt( i ) );
            boolean hasOption = options.hasOption( ch );

            if( hasOption ) {
                tokens.add( "-" + ch );
                currentOption = options.getOption( ch );
                if( currentOption.hasArg() && token.length()!=i+1 ) {
                    tokens.add( token.substring( i+1 ) );
                    break;
                }
            }
            else if( stop ) {
                process( token.substring( i ) );
            }
            else {
                tokens.add( "-" + ch );
            }
        }
    }
}