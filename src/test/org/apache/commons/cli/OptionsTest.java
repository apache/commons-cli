/**
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.cli;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Rob Oxspring roxspring@apache.org
 * @version $Revision$
 */
public class OptionsTest extends TestCase
{

    public static Test suite() 
    { 
        return new TestSuite ( OptionsTest.class ); 
    }

    public OptionsTest( String name )
    {
        super( name );
    }

    public void setUp()
    {
    }

    public void tearDown()
    {
    }
    
    public void testHelpOptions(){
        
        Option longOnly1 = OptionBuilder
            .withLongOpt("long-only1")
            .create();
        
        Option longOnly2 = OptionBuilder
            .withLongOpt("long-only2")
            .create();
                
        Option shortOnly1 = OptionBuilder
            .create("1");
                
        Option shortOnly2 = OptionBuilder
            .create("2");
                
        Option bothA = OptionBuilder
            .withLongOpt("bothA")
            .create("a");
                
        Option bothB = OptionBuilder
            .withLongOpt("bothB")
            .create("b");
        
        Options options = new Options();
        options.addOption(longOnly1);
        options.addOption(longOnly2);
        options.addOption(shortOnly1);
        options.addOption(shortOnly2);
        options.addOption(bothA);
        options.addOption(bothB);
        
        Collection allOptions = new ArrayList();
        allOptions.add(longOnly1);
        allOptions.add(longOnly2);
        allOptions.add(shortOnly1);
        allOptions.add(shortOnly2);
        allOptions.add(bothA);
        allOptions.add(bothB);
        
        Collection helpOptions = options.helpOptions();
        
        assertTrue("Everything in all should be in help",helpOptions.containsAll(allOptions));
        assertTrue("Everything in help should be in all",allOptions.containsAll(helpOptions));        
    }



}

