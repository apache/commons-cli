/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//cli/src/java/org/apache/commons/cli/Util.java,v 1.4 2003/10/09 20:57:02 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/10/09 20:57:02 $
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
 * Contains useful helper methods for classes within this package.
 *
 * @author John Keyes (john at integralsource.com)
 */
class Util {

    /**
     * <p>Remove the hyphens from the begining of <code>str</code> and
     * return the new String.</p>
     *
     * @param str The string from which the hyphens should be removed.
     *
     * @return the new String.
     */
    static String stripLeadingHyphens(String str)
    {
        if (str.startsWith("--"))
        {
            return str.substring(2, str.length());
        }
        else if (str.startsWith("-"))
        {
            return str.substring(1, str.length());
        }

        return str;
    }

    /**
     * <p>Remove the leading and trailing quotes from <code>str</code>,
     * e.g. if str is '"one two"', then 'one two' is returned.</p>
     *
     * @param str The string from which the leading and trailing quotes
     * should be removed.
     *
     * @return The string without the leading and trailing quotes.
     */
    static String stripLeadingAndTrailingQuotes(String str)
    {
        if (str.startsWith("\"")) {
            str = str.substring(1, str.length());
        }
        if (str.endsWith("\"")) {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }
}