/*
 * $Header: /home/cvs/jakarta-commons-sandbox/cli/src/java/org/apache/commons/cli/Options.java,v 1.5 2002/06/06 22:32:37 bayard Exp $
 * $Revision: 1.5 $
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

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

/** <p>Main entry-point into the library.</p>
 *
 * <p>Options represents a collection of {@link Option} objects, which
 * describe the possible options for a command-line.<p>
 *
 * <p>It may flexibly parse long and short options, with or without
 * values.  Additionally, it may parse only a portion of a commandline,
 * allowing for flexible multi-stage parsing.<p>
 *
 * @see org.apache.commons.cli.CommandLine
 *
 * @author bob mcwhirter (bob @ werken.com)
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.5 $
 */
public class Options {

    private String defaultParserImpl = "org.apache.commons.cli.PosixParser";
    private String parserImpl = defaultParserImpl;

    private CommandLineParser parser;

    /** the list of options */
    private List options      = new ArrayList();

    /** a map of the options with the character key */
    private Map  shortOpts    = new HashMap();

    /** a map of the options with the long key */
    private Map  longOpts     = new HashMap();

    /** a map of the required options */
    private Map  requiredOpts = new HashMap();
    
    /** a map of the option groups */
    private Map optionGroups  = new HashMap();

    /** <p>Construct a new Options descriptor</p>
     */
    public Options() {        
        parserImpl = System.getProperty( "org.apache.commons.cli.parser" );
        try {
            parser = (CommandLineParser)Class.forName( parserImpl ).newInstance();
        }
        catch( Exception exp ) {
            // could not create according to parserImpl so default to
            // PosixParser
            try {
                parser = (CommandLineParser)Class.forName( defaultParserImpl ).newInstance();
            }
            catch( Exception exp2 ) {
                // this will not happen ?
            }
        }
    }

    /**
     * <p>Add the specified option group.</p>
     *
     * @param group the OptionGroup that is to be added
     * @return the resulting Options instance
     */
    public Options addOptionGroup( OptionGroup group ) {
        Iterator options = group.getOptions().iterator();

        while( options.hasNext() ) {
            Option option = (Option)options.next();
            addOption( option );
            optionGroups.put( option, group );
        }

        return this;
    }

    /** <p>Add an option that only contains a short-name</p>
     * <p>It may be specified as requiring an argument.</p>
     *
     * @param opt Short single-character name of the option.
     * @param hasArg flag signally if an argument is required after this option
     * @param description Self-documenting description
     * @return the resulting Options instance
     */
    public Options addOption(String opt, boolean hasArg, String description) {
        addOption( opt, null, hasArg, description, false );
        return this;
    }
    
    /** <p>Add an option that contains a short-name and a long-name</p>
     * <p>It may be specified as requiring an argument.</p>
     *
     * @param opt Short single-character name of the option.
     * @param longOpt Long multi-character name of the option.
     * @param hasArg flag signally if an argument is required after this option
     * @param description Self-documenting description
     * @return the resulting Options instance
     */
    public Options addOption(String opt, String longOpt, boolean hasArg, String description) {
        addOption( opt, longOpt, hasArg, description, false );        
        return this;
    }

    /** <p>Add an option that contains a short-name and a long-name</p>
     * <p>It may be specified as requiring an argument.</p>
     *
     * @param opt Short single-character name of the option.
     * @param longOpt Long multi-character name of the option.
     * @param hasArg flag signally if an argument is required after this option
     * @param description Self-documenting description
     * @param required specifies if this option is required
     * @return the resulting Options instance
     */
    public Options addOption(String opt, String longOpt, boolean hasArg, String description,
                             boolean required) {
        addOption( new Option(opt, longOpt, hasArg, description, required) );        
        return this;
    }

    /** <p>Add an option that contains a short-name and a long-name</p>
     * <p>It may be specified as requiring an argument.</p>
     *
     * @param opt Short single-character name of the option.
     * @param longOpt Long multi-character name of the option.
     * @param hasArg flag signally if an argument is required after this option
     * @param description Self-documenting description
     * @param required specifies if this option is required
     * @param multipleArgs specifies if this option can accept multiple argument values
     * @return the resulting Options instance
     */
    public Options addOption(String opt, String longOpt, boolean hasArg, String description,
                             boolean required, boolean multipleArgs) {
        addOption( new Option(opt, longOpt, hasArg, description, required, multipleArgs) );        
        return this;
    }

    /** <p>Add an option that contains a short-name and a long-name</p>
     * <p>It may be specified as requiring an argument.</p>
     *
     * @param opt Short single-character name of the option.
     * @param longOpt Long multi-character name of the option.
     * @param hasArg flag signally if an argument is required after this option
     * @param description Self-documenting description
     * @param required specifies if this option is required
     * @param multipleArgs specifies if this option can accept multiple argument values
     * @param type specifies the type for the value of the option
     * @return the resulting Options instance
     */
    public Options addOption(String opt, String longOpt, boolean hasArg, String description,
                             boolean required, boolean multipleArgs, Object type) {
        addOption( new Option(opt, longOpt, hasArg, description, required, multipleArgs, type) );        
        return this;
    }

    /** <p>Parse the given list of arguments against this descriptor<p>
     *
     * @param args Args to parse
     *
     * @return {@link CommandLine} containing information related to parse state
     *
     * @throws MissingArgumentException if an argument value for an option is not present
     * @throws UnrecognizedOptionException if an unrecognised option is present
     * @throws MissingOptionException if a required option is not present
     * @throws AlreadySelectedException if the same option appears more than once
     */
    public CommandLine parse(String[] args) 
    throws MissingArgumentException, UnrecognizedOptionException, 
        MissingOptionException, AlreadySelectedException {
        return parse( args, 0, args.length, false);
    }
    
    /** <p>Parse the given list of arguments against this descriptor</p>
     *
     * <p>This method will cease parsing upon the first non-option token,
     * storing the rest of the tokens for access through {@link CommandLine#getArgs()}.</p>
     *
     * <p>This is useful for parsing a command-line in pieces, such as:</p>
     *
     * <p><code>
     * <pre>
     * myApp -s &lt;server&gt; -p &lt;port&gt; command -p &lt;printer&gt; -s &lt;style&gt;
     * </pre>
     * </code></p>
     *
     * <p>Here, it'll parse up-to, but not including <code>command</code>. The
     * tokens <code>command -p &lt;printer&gt; -s &lt;style&gt;</code> are available
     * through {@link CommandLine#getArgs()}, which may subsequently be parsed by
     * another different <code>Options</code> instance.<p>
     *
     * @param args Args to parse
     * @param stopAtNonOption stop parsing at the first non-option token
     *
     * @return {@link CommandLine} containing information related to parse state
     *
     * @throws MissingArgumentException if an argument value for an option is not present
     * @throws UnrecognizedOptionException if an unrecognised option is present
     * @throws MissingOptionException if a required option is not present
     * @throws AlreadySelectedException if the same option appears more than once
     */
    public CommandLine parse(String[] args, boolean stopAtNonOption) 
    throws MissingArgumentException, UnrecognizedOptionException, 
        MissingOptionException, AlreadySelectedException {
        return parse( args, 0, args.length, stopAtNonOption);
    }
    
    /** <p>Parse the given list of arguments against this descriptor</p>
     *
     * <p>This method allows parsing from <code>formIndex</code> inclusive
     * to <code>toIndex</code> exclusive, of the <code>args</code> parameter,
     * to allow parsing a specific portion of a command-line.<p>
     *
     * @param args Args to parse
     * @param fromIndex index of args to start parsing
     * @param toIndex index of args to stop parsing
     *
     * @return {@link CommandLine} containing information related to parse state
     *
     * @throws MissingArgumentException if an argument value for an option is not present
     * @throws UnrecognizedOptionException if an unrecognised option is present
     * @throws MissingOptionException if a required option is not present
     * @throws AlreadySelectedException if the same option appears more than once
     */
    public CommandLine parse(String[] args, int fromIndex, int toIndex) 
    throws MissingArgumentException, UnrecognizedOptionException, 
        MissingOptionException, AlreadySelectedException {
        return parse( args, fromIndex, toIndex, false );
    }
    
    /** <p>Parse the given list of arguments against this descriptor</p>
     *
     * <p>This method will cease parsing upon the first non-option token,
     * storing the rest of the tokens for access through {@link CommandLine#getArgs()}.</p>
     *
     * <p>This is useful for parsing a command-line in pieces, such as:</p>
     *
     * <p><code>
     * <pre>
     * myApp -s &lt;server&gt; -p &lt;port&gt; command -p &lt;printer&gt; -s &lt;style&gt;
     * </pre>
     * </code></p>
     *
     * <p>Here, it'll parse up-to, but not including <code>command</code>. The
     * tokens <code>command -p &lt;printer&gt; -s &lt;style&gt;</code> are available
     * through {@link CommandLine#getArgs()}, which may subsequently be parsed by
     * another different <code>Options</code> instance.<p>
     *
     * <p>This method also allows parsing from <code>formIndex</code> inclusive
     * to <code>toIndex</code> exclusive, of the <code>args</code> parameter,
     * to allow parsing a specific portion of a command-line.<p>
     *
     * @param args Args to parse
     * @param fromIndex index of args to start parsing
     * @param toIndex index of args to stop parsing
     * @param stopAtNonOption stop parsing at the first non-option token
     *
     * @return {@link CommandLine} containing information related to parse state
     *
     * @throws MissingArgumentException if an argument value for an option is not present
     * @throws UnrecognizedOptionException if an unrecognised option is present
     * @throws MissingOptionException if a required option is not present
     * @throws AlreadySelectedException if the same option appears more than once
     */
    public CommandLine parse(String[] args, int fromIndex, int toIndex, boolean stopAtNonOption)
    throws MissingArgumentException, UnrecognizedOptionException, 
        MissingOptionException, AlreadySelectedException {
        List argList = java.util.Arrays.asList( args );
        
        return parse( argList, stopAtNonOption);
    }
    
    /** <p>Parse the given list of arguments against this descriptor</p>
     *
     * @param args Args to parse
     *
     * @return {@link CommandLine} containing information related to parse state
     *
     * @throws MissingArgumentException if an argument value for an option is not present
     * @throws UnrecognizedOptionException if an unrecognised option is present
     * @throws MissingOptionException if a required option is not present
     * @throws AlreadySelectedException if the same option appears more than once
     */
    public CommandLine parse(List args)
    throws MissingArgumentException, UnrecognizedOptionException, 
        MissingOptionException, AlreadySelectedException {
        return parse( args, false );
    }
    
    /** <p>Parse the given list of arguments against this descriptor</p>
     *
     * <p>This method will cease parsing upon the first non-option token,
     * storing the rest of the tokens for access through {@link CommandLine#getArgs()}.</p>
     *
     * <p>This is useful for parsing a command-line in pieces, such as:</p>
     *
     * <p><code>
     * <pre>
     * myApp -s &lt;server&gt; -p &lt;port&gt; command -p &lt;printer&gt; -s &lt;style&gt;
     * </pre>
     * </code></p>
     *
     * <p>Here, it'll parse up-to, but not including <code>command</code>. The
     * tokens <code>command -p &lt;printer&gt; -s &lt;style&gt;</code> are available
     * through {@link CommandLine#getArgs()}, which may subsequently be parsed by
     * another different <code>Options</code> instance.<p>
     *
     * <p>This method also allows parsing from <code>formIndex</code> inclusive
     * to <code>toIndex</code> exclusive, of the <code>args</code> parameter,
     * to allow parsing a specific portion of a command-line.<p>
     *
     * @param inArgs Arguments to parse
     * @param stopAtNonOption stop parsing at the first non-option token
     *
     * @return {@link CommandLine} containing information related to parse state
     *
     * @throws MissingArgumentException if an argument value for an option is not present
     * @throws UnrecognizedOptionException if an unrecognised option is present
     * @throws MissingOptionException if a required option is not present
     * @throws AlreadySelectedException if the same option appears more than once
     */
    public CommandLine parse(List inArgs, boolean stopAtNonOption) 
    throws MissingArgumentException, UnrecognizedOptionException, 
        MissingOptionException, AlreadySelectedException {
        CommandLine cl = new CommandLine();
        
        List args = parser.parse( this, inArgs, stopAtNonOption );

        ListIterator argIter = args.listIterator();
        String   eachArg = null;
        Option   eachOpt = null;
        boolean  eatTheRest = false;

        while ( argIter.hasNext() ) {

            eachArg = (String) argIter.next();

            if ( eachArg.equals("--") ) {
                // signalled end-of-opts.  Eat the rest
                
                eatTheRest = true;
            }
            else if ( eachArg.startsWith("--") ) {
                eachOpt = (Option) longOpts.get( eachArg );
                processOption( eachArg, eachOpt, argIter, cl );
            }
            else if ( eachArg.equals("-") ) {
                // Just-another-argument
                
                if ( stopAtNonOption ) {
                    eatTheRest = true;
                }
                else {
                    cl.addArg( eachArg );
                }
            }
            else if ( eachArg.startsWith("-") ) {
                eachOpt = (Option) shortOpts.get( eachArg );
                processOption( eachArg, eachOpt, argIter, cl );
            }                
            else {
                cl.addArg( eachArg );
                if ( stopAtNonOption ) {
                    eatTheRest = true;
                }
            }
            
            if ( eatTheRest ) {
                while ( argIter.hasNext() ) {
                    eachArg = (String) argIter.next();
                    cl.addArg( eachArg );
                }
            }
        }

        // this will throw a MissingOptionException
        checkRequiredOptions();

        return cl;
    }

    /**
     * @throws MissingOptionException if all of the required options are
     * not present.
     */
    private void checkRequiredOptions() throws MissingOptionException {
        if( requiredOpts.size() > 0 ) {
            Set optKeys = requiredOpts.keySet();

            Iterator iter = optKeys.iterator();

            StringBuffer buff = new StringBuffer();

            while( iter.hasNext() ) {
                Option missing = (Option)requiredOpts.get( iter.next() );
                buff.append( "-" );
                buff.append( missing.getOpt() );
                buff.append( " " );
                buff.append( missing.getDescription() );
            }

            throw new MissingOptionException( buff.toString() );
        }
    }

    /**
     * <p>processOption rakes the current option and checks if it is
     * an unrecognised option, whether the argument value is missing or
     * whether the option has already been selected.</p>
     *
     * @param eachArg the current option read from command line
     * @param option the current option corresponding to eachArg
     * @param argIter the argument iterator
     * @param cl the current command line
     *
     * @throws MissingArgumentException if an argument value for an option is not present
     * @throws UnrecognizedOptionException if an unrecognised option is present
     * @throws AlreadySelectedException if the same option appears more than once
     */
    private void processOption( String eachArg, Option option, ListIterator argIter, 
                                CommandLine cl)
    throws UnrecognizedOptionException, AlreadySelectedException, 
        MissingArgumentException {

        if ( option == null ) {
            throw new UnrecognizedOptionException("Unrecognized option: " + eachArg);
        }
        else {

            if ( optionGroups.get( option ) != null ) {
                ( (OptionGroup)( optionGroups.get( option ) ) ).setSelected( option );
            }

            // if required remove from list
            if ( option.isRequired() ) {
                requiredOpts.remove( "-" + option.getOpt() );
            }

            if ( option.hasArg() ) {
                if ( argIter.hasNext() ) {
                    eachArg = (String) argIter.next();
                    option.addValue( eachArg );
                    
                    if( option.hasMultipleArgs() ) {
                        while( argIter.hasNext() ) {
                            eachArg = (String)argIter.next();
                            if( eachArg.startsWith("-") ) {
                                argIter.previous();
                                cl.setOpt( option );
                                break;
                            }
                            else {
                                option.addValue( eachArg );
                            }
                        }
                    }
                    else {
                        cl.setOpt( option );
                        return;
                    }
                    if( !argIter.hasNext() ) {
                        cl.setOpt( option );
                    }
                }
                else {
                    throw new MissingArgumentException( eachArg + " requires an argument.");
                }

            }
            else {
                cl.setOpt( option );
            }
        }
    }

    /**
     * <p>Adds the option to the necessary member lists</p>
     *
     * @param opt the option that is to be added 
     */
    private void addOption(Option opt)  {
        String shortOptStr = "-" + opt.getOpt();
        
        if ( opt.hasLongOpt() ) {
            longOpts.put( "--" + opt.getLongOpt(), opt );
        }
        
        if ( opt.isRequired() ) {
            requiredOpts.put( "-" + opt.getOpt(), opt );
        }

        shortOpts.put( "-" + opt.getOpt(), opt );
        
        options.add( opt );
    }
    
    /** <p>Retrieve a read-only list of options in this set</p>
     *
     * @return read-only List of {@link Option} objects in this descriptor
     */
    public List getOptions() {
        return Collections.unmodifiableList(options);
    }
    
    /** <p>Retrieve the named {@link Option}</p>
     *
     * @param opt short single-character name of the {@link Option}
     * @return the option represented by opt
     */
    public Option getOption(String opt) {

        // short option
        if( opt.length() == 1 ) {
            return (Option) shortOpts.get( "-" + opt );
        }
        // long option
        else if( opt.startsWith( "--" ) ) {
            return (Option) longOpts.get( opt );
        }
        // a just-in-case
        else {
            return (Option) shortOpts.get( opt );
        }
    }
    
    /** <p>Dump state, suitable for debugging.</p>
     *
     * @return Stringified form of this object
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        
        buf.append("[ Options: [ short ");
        buf.append( shortOpts.toString() );
        buf.append( " ] [ long " );
        buf.append( longOpts );
        buf.append( " ]");
        
        return buf.toString();
    }
}
