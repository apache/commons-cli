/*
 * $Header: /home/cvs/jakarta-commons-sandbox/cli/src/java/org/apache/commons/cli/Option.java,v 1.6 2002/06/06 22:50:14 bayard Exp $
 * $Revision: 1.6 $
 * $Date: 2002/06/06 22:50:14 $
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

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: Option.java,v 1.6 2002/06/06 22:50:14 bayard Exp $
 */

package org.apache.commons.cli;

import java.util.ArrayList;

/** <p>Describes a single command-line option.  It maintains
 * information regarding the short-name of the option, the long-name,
 * if any exists, a flag indicating if an argument is required for
 * this option, and a self-documenting description of the option.</p>
 *
 * <p>An Option is not created independantly, but is create through
 * an instance of {@link Options}.<p>
 *
 * @see org.apache.commons.cli.Options
 * @see org.apache.commons.cli.CommandLine
 *
 * @author bob mcwhirter (bob @ werken.com)
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.6 $
 */

public class Option {
    
    /** opt the single character representation of the option */
    private String opt = null;

    /** longOpt is the long representation of the option */
    private String     longOpt      = null;

    /** hasArg specifies whether this option has an associated argument */
    private boolean    hasArg       = false;

    /** description of the option */
    private String     description  = null;

    /** required specifies whether this option is required to be present */
    private boolean    required     = false;

    /** 
     * numberOfArgs specifies the number of argument values this option 
     * can have 
     */
    private int    numberOfArgs = UNINITIALIZED;   

    /** number of arguments constants */
    public final static int UNINITIALIZED = -1;
    public final static int UNLIMITED_VALUES = -2;

    /** the type of this Option */
    private Object     type         = null;   

    /** ?? **/
    private ArrayList  values       = new ArrayList();
    
    /** option char (only valid for single character options) */
    private char id;

    private void validateOption( String opt ) 
    throws IllegalArgumentException
    {
        if( opt == null ) {
            throw new IllegalArgumentException( "opt is null" );
        }
        else if( opt.length() == 1 ) {
            if ( !isValidOpt( opt.charAt( 0 ) ) ) {
                throw new IllegalArgumentException( "illegal option value '" 
                                                    + opt.charAt( 0 ) + "'" );
            }
            id = opt.charAt( 0 );
        }
        else {
            char[] chars = opt.toCharArray();
            for( int i = 0; i < chars.length; i++ ) {
                if( !isValidChar( chars[i] ) ) {
                    throw new IllegalArgumentException( "opt contains illegal character value '" + chars[i] + "'" );
                }
            }
        }
    }

    private boolean isValidOpt( char c ) 
    {
        if ( ! ( isValidChar( c ) || c == '?' || c == '@') ) {
            return false;
        }
        return true;
    }

    private boolean isValidChar( char c ) 
    {
        if ( ! ( Character.isLetter( c ) ) ) {
            return false;
        }
        return true;
    }

    public int getId( ) {
        return id;
    }

    /**
     * Creates an Option using the specified parameters.
     *
     * @param opt short representation of the option
     * @param hasArg specifies whether the Option takes an argument or not
     * @param description describes the function of the option
     */
    public Option(String opt, String description) 
    throws IllegalArgumentException
    {
        this(opt, null, false, description);
    }

    /**
     * Creates an Option using the specified parameters.
     *
     * @param opt short representation of the option
     * @param hasArg specifies whether the Option takes an argument or not
     * @param description describes the function of the option
     */
    public Option(String opt, boolean hasArg, String description) 
    throws IllegalArgumentException
    {
        this(opt, null, hasArg, description);
    }
    
    /**
     * Creates an Option using the specified parameters.
     *
     * @param opt short representation of the option
     * @param longOpt the long representation of the option
     * @param hasArg specifies whether the Option takes an argument or not
     * @param description describes the function of the option
     */
    public Option(String opt, String longOpt, boolean hasArg, String description) 
    throws IllegalArgumentException
    {
        validateOption( opt );

        this.opt          = opt;
        this.longOpt      = longOpt;

        if( hasArg ) {
            this.numberOfArgs = 1;
        }
        this.hasArg       = hasArg;
        this.description  = description;
    }
    
    /** <p>Retrieve the name of this Option</p>
     *
     * <p>It is this character which can be used with
     * {@link CommandLine#hasOption(String opt)} and
     * {@link CommandLine#getOptionValue(String opt)} to check
     * for existence and argument.<p>
     *
     * @return The name of this option
     */
    public String getOpt() {
        return this.opt;
    }

    /**
     * <p>Retrieve the type of this Option</p>
     * 
     * @return The type of this option
     */
    public Object getType() {
        return this.type;
    }

    public void setType( Object type ) {
        this.type = type;
    }
    
    /** <p>Retrieve the long name of this Option</p>
     *
     * @return Long name of this option, or null, if there is no long name
     */
    public String getLongOpt() {
        return this.longOpt;
    }

    public void setLongOpt( String longOpt ) {
        this.longOpt = longOpt;
    }
    
    /** <p>Query to see if this Option has a long name</p>
     *
     * @return boolean flag indicating existence of a long name
     */
    public boolean hasLongOpt() {
        return ( this.longOpt != null );
    }
    
    /** <p>Query to see if this Option requires an argument</p>
     *
     * @return boolean flag indicating if an argument is required
     */
    public boolean hasArg() {
        return this.numberOfArgs > 0 || numberOfArgs == UNLIMITED_VALUES;
    }
    
    /** <p>Retrieve the self-documenting description of this Option</p>
     *
     * @return The string description of this option
     */
    public String getDescription() {
        return this.description;
    }

     /** <p>Query to see if this Option requires an argument</p>
      *
      * @return boolean flag indicating if an argument is required
      */
     public boolean isRequired() {
         return this.required;
     }

     public void setRequired( boolean required ) {
         this.required = required;
     }

     /** <p>Query to see if this Option can take many values</p>
      *
      * @return boolean flag indicating if multiple values are allowed
      */
     public boolean hasArgs() {
         return ( this.numberOfArgs > 1 || this.numberOfArgs == UNLIMITED_VALUES );
     }

     /** <p>Sets the number of argument values this Option can take.</p>
      *
      * @param num the number of argument values
      */
     public void setArgs( int num ) {
         this.numberOfArgs = num;
     }

     /** <p>Returns the number of argument values this Option can take.</p>
      *
      * @return num the number of argument values
      */
     public int getArgs( ) {
         return this.numberOfArgs;
     }

    /** <p>Dump state, suitable for debugging.</p>
     *
     * @return Stringified form of this object
     */
    public String toString() {
        StringBuffer buf = new StringBuffer().append("[ option: ");
        
        buf.append( this.opt );
        
        if ( this.longOpt != null ) {
            buf.append(" ")
            .append(this.longOpt);
        }
        
        buf.append(" ");
        
        if ( hasArg ) {
            buf.append( "+ARG" );
        }
        
        buf.append(" :: ")
        .append( this.description );
        
        if ( this.type != null ) {
            buf.append(" :: ")
            .append( this.type );
        }

        buf.append(" ]");
        return buf.toString();
    }

    /**
     * Adds the specified value to this Option
     * 
     * @param value is a/the value of this Option
     */
    public boolean addValue( String value ) {
        switch( numberOfArgs ) {
            case UNINITIALIZED:
                return false;
            case UNLIMITED_VALUES:
                this.values.add( value );
                return true;
            default:
                if( values.size() > numberOfArgs-1 ) {
                    return false;
                }
                this.values.add( value );
                return true;
        }
    }

    /**
     * @return the value/first value of this Option or 
     * null if there are no values.
     */
    public String getValue() {
        return this.values.size()==0 ? null : (String)this.values.get( 0 );
    }

    /**
     * @return the value/first value of this Option or the 
     * <code>defaultValue</code> if there are no values.
     */
    public String getValue( String defaultValue ) {
        String value = getValue( );
        return ( value != null ) ? value : defaultValue;
    }

    /**
     * @return the values of this Option or null if there are no
     * values
     */
    public String[] getValues() {
        return this.values.size()==0 ? null : (String[])this.values.toArray(new String[]{});
    }
}
