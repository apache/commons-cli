package org.apache.commons.cli;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Iterator;

/**
 * @author John Keyes (jbjk at mac.com)
 */
public class GnuParser extends Parser {

    private ArrayList tokens = new ArrayList();

    private void init() {
        tokens.clear();
    }

    protected String[] flatten( Options options, 
                                String[] arguments, 
                                boolean stopAtNonOption )
    {
        init();
        for( int i = 0; i < arguments.length; i++ ) {
            Option option = options.getOption( arguments[i] );
            try {
                Option specialOption = options.getOption( arguments[i].substring(0,2) );
                if( specialOption != null && option == null ) {
                    tokens.add( arguments[i].substring(0,2) );
                    tokens.add( arguments[i].substring(2) );
                }
                else {
                    tokens.add( arguments[i] );
                }
            }
            catch( IndexOutOfBoundsException exp ) {
                tokens.add( arguments[i] );
            }
        }
        return (String[])tokens.toArray( new String[] {} );
    }
}