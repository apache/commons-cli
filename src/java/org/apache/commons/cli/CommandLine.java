/*
 * $Header: /home/cvs/jakarta-commons-sandbox/cli/src/java/org/apache/commons/cli/CommandLine.java,v 1.4 2002/06/06 22:32:37 bayard Exp $
 * $Revision: 1.4 $
 * $Date: 2002/06/06 22:32:37 $
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

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/** <p>Represents list of arguments parsed against
 * a {@link Options} descriptor.<p>
 *
 * <p>It allows querying of a boolean {@link #hasOption(String opt)},
 * in addition to retrieving the {@link #getOptionValue(String opt)}
 * for options requiring arguments.</p>
 *
 * <p>Additionally, any left-over or unrecognized arguments,
 * are available for further processing.</p>
 *
 * @author bob mcwhirter (bob @ werken.com)
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author John Keyes (jbjk at mac.com)
 *
 * @version $Revision: 1.4 $
 */
public class CommandLine {
    
    /** the unrecognised options/arguments */
    private List args    = new LinkedList();

    /** the recognised options/arguments */
    private Map  options = new HashMap();

    /** the option types */
    private Map  types   = new HashMap();

    /**
     * <p>Creates a command line.</p>
     */
    CommandLine() {
    }
    
    /** <p>Query to see if an option has been set.</p>
     *
     * @param opt Short single-character name of the option
     * @return true if set, false if not
     */
    public boolean hasOption(String opt) {
        return options.containsKey( opt );
    }

    public Object getOptionObject(String opt) {
        String[] result = (String[])options.get( opt );
        Object type = types.get( opt );
        String res = result == null ? null : result[0];
        if(res == null) {
            return null;
        }
        return TypeHandler.createValue(res, type);
    }

    /** <p>Retrieve the argument, if any,  of an option.</p>
     *
     * @param opt Short single-character name of the option
     * @return Value of the argument if option is set, and has an argument, else null.
     */
    public String getOptionValue(String opt) {
        String[] result = (String[])options.get( opt );
        return result == null ? null : result[0];
    }

    /** <p>Retrieves the array of values, if any, of an option.</p>
     *
     * @param opt Single-character name of the option
     * @return An array of values if the option is set, and has an argument, else null.
     */
    public String[] getOptionValues(String opt) {
        String[] result = (String[])options.get( opt );
        return result == null ? null : result;
    }
    
    /** <p>Retrieve the argument, if any,  of an option.</p>
     *
     * @param opt Short single-character name of the option
     * @param defaultValue is the default value to be returned if the option is not specified
     * @return Value of the argument if option is set, and has an argument, else null.
     */
    public String getOptionValue(String opt, String defaultValue) {
        String answer = getOptionValue(opt);
        return (answer != null) ? answer : defaultValue;
    }
    
    /** <p>Retrieve any left-over non-recognized options and arguments</p>
     *
     * @return an array of remaining items passed in but not parsed
     */
    public String[] getArgs() {
        String[] answer = new String[ args.size() ];
        args.toArray( answer );
        return answer;
    }
    
    /** <p>Retrieve any left-over non-recognized options and arguments</p>
     *
     * @return List of remaining items passed in but not parsed
     */
    public List getArgList() {
        return args;
    }
    
    /** <p>Dump state, suitable for debugging.</p>
     *
     * @return Stringified form of this object
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        
        buf.append( "[ CommandLine: [ options: " );
        buf.append( options.toString() );
        buf.append( " ] [ args: ");
        buf.append( args.toString() );
        buf.append( " ] ]" );
        
        return buf.toString();
    }
    
    /**
     * <p>Add left-over unrecognized option/argument.</p>
     *
     * @param arg the unrecognised option/argument.
     */
    void addArg(String arg) {
        args.add( arg );
    }
    
    /**
     * <p>Add an option that does not have any value to the 
     * command line.</p>
     *
     * @param opt the processed option
     */
    void setOpt(String opt) {
        options.put( opt, null );
    }
    
    /**
     * <p>Add an option with the specified value to the 
     * command line.</p>
     *
     * @param opt the processed option
     * @param value the value of the option
     */
    void setOpt(String opt, String value) {
        options.put( opt, value );
    }
    
    /**
     * <p>Add an option to the command line.  The values of 
     * the option are stored.</p>
     *
     * @param opt the processed option
     */
    void setOpt(Option opt) {
        options.put( opt.getOpt(), opt.getValues() );
        types.put( opt.getOpt(), opt.getType() );
    }
}
