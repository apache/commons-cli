/*
 * $Header: /home/cvs/jakarta-commons-sandbox/cli/src/java/org/apache/commons/cli/PatternOptionBuilder.java,v 1.2 2002/06/06 22:49:36 bayard Exp $
 * $Revision: 1.2 $
 * $Date: 2002/06/06 22:49:36 $
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

/** 
 * Allows Options to be created from a single String.
 *
 *
 * @author Henri Yandell (bayard @ generationjava.com)
 * @version $Revision: 1.2 $
 */
public class PatternOptionBuilder {

    /// TODO: These need to break out to OptionType and also to be pluggable.

    static public final Class STRING_VALUE        = java.lang.String.class;
    static public final Class OBJECT_VALUE        = java.lang.Object.class;
    static public final Class NUMBER_VALUE        = java.lang.Number.class;
    static public final Class DATE_VALUE          = java.util.Date.class;
    static public final Class CLASS_VALUE         = java.lang.Class.class;

/// can we do this one?? 
// is meant to check that the file exists, else it errors.
// ie) it's for reading not writing.
    static public final Class EXISTING_FILE_VALUE = java.io.FileInputStream.class;
    static public final Class FILE_VALUE          = java.io.File.class;
    static public final Class FILES_VALUE         = java.io.File[].class;
    static public final Class URL_VALUE           = java.net.URL.class;

    static public Object getValueClass(char ch) {
        if (ch == '@') {
            return PatternOptionBuilder.OBJECT_VALUE;
        } else if (ch == ':') {
            return PatternOptionBuilder.STRING_VALUE;
        } else if (ch == '%') {
            return PatternOptionBuilder.NUMBER_VALUE;
        } else if (ch == '+') {
            return PatternOptionBuilder.CLASS_VALUE;
        } else if (ch == '#') {
            return PatternOptionBuilder.DATE_VALUE;
        } else if (ch == '<') {
            return PatternOptionBuilder.EXISTING_FILE_VALUE;
        } else if (ch == '>') {
            return PatternOptionBuilder.FILE_VALUE;
        } else if (ch == '*') {
            return PatternOptionBuilder.FILES_VALUE;
        } else if (ch == '/') {
            return PatternOptionBuilder.URL_VALUE;
        }
        return null;
    }
 
    static public boolean isValueCode(char ch) {
        if( (ch != '@') &&
            (ch != ':') &&
            (ch != '%') &&
            (ch != '+') &&
            (ch != '#') &&
            (ch != '<') &&
            (ch != '>') &&
            (ch != '*') &&
            (ch != '/')
          )
        {
            return false;
        }
        return true;
    }       
 
    static public Options parsePattern(String pattern) {
        int sz = pattern.length();

        char opt = ' ';
        char ch = ' ';
        boolean required = false;
        Object type = null;

        Options options = new Options();

        for(int i=0; i<sz; i++) {
            ch = pattern.charAt(i);

            // a value code comes after an option and specifies 
            // details about it
            if(!isValueCode(ch)) {
                if(opt != ' ') {
                    // we have a previous one to deal with
                    options.addOption("" + opt, null, (type != null), "", required, false, type);
                    required = false;
                    type = null;
                    opt = ' ';
                }
                opt = ch;
            } else
            if(ch == '!') {
                required = true;
            } else {
                type = getValueClass(ch);
            }
        }

        if(opt != ' ') {
            // we have a final one to deal with
            options.addOption( "" + opt, null, (type != null), "", required, false, type);
        }

        return options;
    }

}
