/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//cli/src/java/org/apache/commons/cli/GnuParser.java,v 1.5 2002/08/14 22:27:39 jkeyes Exp $
 * $Revision: 1.5 $
 * $Date: 2002/08/14 22:27:39 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.cli;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

/**
 * GnuParser parses the command line arguments using the GNU style.
 * For example, -buildfile can only be interpreted as the option
 * 'buildfile'.
 *
 * @author John Keyes (jbjk at mac.com)
 */
public class GnuParser implements CommandLineParser {

    /** current options instance */
    private Options options;

    /** convience member for the command line */
    private CommandLine cmd;

    /** required options subset of options */
    private Map requiredOptions;

    /**
     * Parse the arguments according to the specified options.
     *
     * @param options the specified Options
     * @param arguments the command line arguments
     * @return the list of atomic option and value tokens
     * @throws ParseException if there are any problems encountered
     * while parsing the command line tokens.
     */
    public CommandLine parse( Options options, String[] arguments ) 
    throws ParseException
    {
        return parse( options, arguments, false );
    }

    /**
     * Parse the arguments according to the specified options.
     *
     * @param opts the specified Options
     * @param arguments the command line arguments
     * @param stopAtNonOption specifies whether to continue parsing the
     * arguments if a non option is encountered.
     * @return the CommandLine
     * @throws ParseException if there are any problems encountered
     * while parsing the command line tokens.
     */
    public CommandLine parse( Options opts, String[] arguments, boolean stopAtNonOption ) 
    throws ParseException
    {
        // set the member instances
        options = opts;
        cmd = new CommandLine();
        requiredOptions = options.getRequiredOptions();

        ListIterator iter = Arrays.asList( arguments ).listIterator();
        String token = null;

        // flag to indicate whether the remainder of the tokens should
        // be added to the other arguments list
        boolean eatTheRest = false;

        while ( iter.hasNext() ) {
            token = (String) iter.next();
            if ( token.equals("--") ) {
                eatTheRest = true;
            }
            else if ( token.startsWith("--") ) {
                //process the long-option
                processOption( token, iter );
            }
            else if ( token.startsWith("-") ) {
                if ( token.length() == 1) {
                    // It's not really an option, so
                    // just drop it on the list
                    if ( stopAtNonOption ) {
                        eatTheRest = true;
                    }
                    else {
                        cmd.addArg( token );
                    }
                }
                else {
                    processOption( token, iter );
                }
            }
            else {
                // It's just a normal non-option arg,
                // so dump it into the list of returned
                // values.

                cmd.addArg( token );

                if ( stopAtNonOption ) {
                    eatTheRest = true;
                }
            }

            if ( eatTheRest ) {
                while ( iter.hasNext() ) {
                    cmd.addArg( (String)iter.next() );
                }
            }
        }

        // see if all required options have been processed
        checkRequiredOptions( );

        return cmd;
    }

    /**
     * It the option can accept multiple argument values then
     * keep adding values until the next option token is encountered.
     *
     * @param opt the specified option
     * @param iter the iterator over the command line tokens
     */
    public void processArgs( Option opt, ListIterator iter ) 
    throws ParseException 
    {
        if( !iter.hasNext() ) {
            throw new MissingArgumentException( "no argument for:" + opt.getOpt() );
        }
        // loop until an option is found
        while( iter.hasNext() ) {
            String var = (String)iter.next();

            // its an option
            if( !var.equals( "-" ) && var.startsWith( "-" ) ) {
                // set the iterator pointer back a position
                iter.previous();
                break;
            }
            // its a value
            else if( !opt.addValue( var ) ) {
                iter.previous();
                return;
            }
        }
    }

    /**
     * Process the option represented by <code>arg</code>.
     * 
     * @param arg the string representation of an option
     * @param iter the command line token iterator
     */
    private void processOption( String arg, ListIterator iter ) 
    throws ParseException
    {
        String value = null;

        // see if it is a single character special option
        Option opt = (Option) options.getOption( arg );
        Option specialOption = (Option) options.getOption( arg.substring(0,2 ) );
        if( specialOption != null && opt == null) {
            opt = specialOption;
            value = arg.substring( 2 );
            char sep = opt.getValueSeparator();

            if( sep > 0 ) {
                int findex;
                while( ( findex = value.indexOf( sep ) ) != -1 ) {
                    String val = value.substring( 0, findex );
                    value = value.substring( findex + 1);
                    if( !opt.addValue( val ) ) {
                        cmd.addArg( val );
                    }
                }
                if( !opt.addValue( value ) ) {
                    cmd.addArg( value );
                }
            }
            else {
                // add the argument value
                opt.addValue( value );
            }
        }

        // if there is no option throw an UnrecognisedOptionException
        if( opt == null ) {
            throw new UnrecognizedOptionException("Unrecognized option: " + arg);
        }

        // if the option is a required option remove the option from
        // the requiredOptions list
        if ( opt.isRequired() ) {
            requiredOptions.remove( opt );
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

    /**
     * Ensures that all required options are present.
     *
     * @throws ParseException if all of the required options
     * are not present.
     */
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

}