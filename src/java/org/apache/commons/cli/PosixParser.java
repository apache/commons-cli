/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//cli/src/java/org/apache/commons/cli/PosixParser.java,v 1.2 2002/06/19 21:31:16 jkeyes Exp $
 * $Revision: 1.2 $
 * $Date: 2002/06/19 21:31:16 $
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * PosixParser parses the command line arguments using the Posix style.
 * For example, -buildfile can only be interpreted as the option
 * 'b' with value 'uildfile' or it could be interpreted as the options
 * 'b','u','i','l','d','f','i','l','e'.
 *
 * @author John Keyes (jbjk at mac.com)
 */
public class PosixParser implements CommandLineParser {

    /**
     * Parse the arguments according to the specified options.
     * @param options the specified Options
     * @param arguments the command line arguments
     * @return the list of atomic option and value tokens
     */
    public List parse( Options options, List arguments ) {
        return parse( options, arguments, false );
    }

    /**
     * Parse the arguments according to the specified options.
     * @param options the specified Options
     * @param arguments the command line arguments
     * @param stopAtNonOption specifies whether to continue parsing the
     * arguments if a non option is encountered.
     * @return the list of atomic option and value tokens
     */
    public List parse( Options options, List arguments, boolean stopAtNonOption ) {
        List args = new LinkedList();
        
        Iterator argIter = arguments.iterator();
        String   eachArg = null;
        
        boolean eatTheRest = false;
        
        while ( argIter.hasNext() ) {
            eachArg = (String) argIter.next();
            
            if ( eachArg.equals("--") ) {
                // Look for -- to indicate end-of-options, and
                // just stuff it, along with everything past it
                // into the returned list.
                
                args.add( eachArg );
                eatTheRest = true;
            }
            else if ( eachArg.startsWith("--") ) {
                // It's a long-option, so doesn't need any
                // bursting applied to it.
                
                args.add( eachArg );
            }
            else if ( eachArg.startsWith("-") ) {
                // It might be a short arg needing
                // some bursting
                
                if ( eachArg.length() == 1) {
                    // It's not really an option, so
                    // just drop it on the list
                    
                    if ( stopAtNonOption ) {
                        eatTheRest = true;
                    }
                    else {
                        args.add( eachArg );
                    }
                }
                else if ( eachArg.length() == 2 ) {
                    // No bursting required
                    
                    args.add( eachArg );
                }
                else {
                    // Needs bursting.  Figure out
                    // if we have multiple options,
                    // or maybe an option plus an arg,
                    // or some combination thereof.
                    
                    for ( int i = 1 ; i < eachArg.length() ; ++i ) {
                        String optStr = "-" + eachArg.charAt(i);
                        Option opt    = (Option) options.getOption( String.valueOf( eachArg.charAt(i) ) );
                        
                        if ( (opt != null) && (opt.hasArg()) ) {
                            // If the current option has an argument,
                            // then consider the rest of the eachArg
                            // to be that argument.
                            
                            args.add( optStr );
                            
                            if ( (i+1) < eachArg.length() ) {
                                String optArg = eachArg.substring(i+1);
                                args.add( optArg );
                            }                            
                            break;
                        }
                        else {
                            // No argument, so prepend the single dash,
                            // and then drop it into the arglist.
                            
                            args.add( optStr );
                        }
                    }
                }
            }
            else {
                // It's just a normal non-option arg,
                // so dump it into the list of returned
                // values.
                
                args.add( eachArg );
                
                if ( stopAtNonOption ) {
                    eatTheRest = true;
                }
            }
            
            if ( eatTheRest ) {
                while ( argIter.hasNext() ) {
                    args.add( argIter.next() );
                }
            }
        }
        
        return args;
    }
}