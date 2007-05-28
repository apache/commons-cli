/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import junit.framework.TestCase;
import junit.framework.TestSuite;

/** 
 * Test case for the PatternOptionBuilder class 
 *
 * @author Henri Yandell
 **/
public class PatternOptionBuilderTest
extends TestCase
{

   public static TestSuite suite()
   {
      return new TestSuite(PatternOptionBuilderTest.class);
   }

   public void testSimplePattern()
   {
       try {
           Options options = PatternOptionBuilder.parsePattern("a:b@cde>f+n%t/");
           String[] args = new String[] { "-c", "-a", "foo", "-b", "java.util.Vector", "-e", "build.xml", "-f", "java.util.Calendar", "-n", "4.5", "-t", "http://jakarta.apache.org/" };
      
           CommandLineParser parser = new PosixParser();
           CommandLine line = parser.parse(options,args);

           assertEquals("flag a", "foo", line.getOptionValue("a"));
           assertEquals("string flag a", "foo", line.getOptionObject("a"));
           assertEquals("object flag b", new java.util.Vector(), line.getOptionObject("b"));
           assertTrue("boolean true flag c", line.hasOption("c"));
           assertFalse("boolean false flag d", line.hasOption("d"));
           assertEquals("file flag e", new java.io.File("build.xml"), line.getOptionObject("e"));
           assertEquals("class flag f", java.util.Calendar.class, line.getOptionObject("f"));
           assertEquals("number flag n", new Double(4.5), line.getOptionObject("n"));
           assertEquals("url flag t", new java.net.URL("http://jakarta.apache.org/"), line.getOptionObject("t"));

           // tests the char methods of CommandLine that delegate to the String methods
           assertEquals("flag a", "foo", line.getOptionValue('a'));
           assertEquals("string flag a", "foo", line.getOptionObject('a'));
           assertEquals("object flag b", new java.util.Vector(), line.getOptionObject('b'));
           assertTrue("boolean true flag c", line.hasOption('c'));
           assertFalse("boolean false flag d", line.hasOption('d'));
           assertEquals("file flag e", new java.io.File("build.xml"), line.getOptionObject('e'));
           assertEquals("class flag f", java.util.Calendar.class, line.getOptionObject('f'));
           assertEquals("number flag n", new Double(4.5), line.getOptionObject('n'));
           assertEquals("url flag t", new java.net.URL("http://jakarta.apache.org/"), line.getOptionObject('t'));

           /// DATES NOT SUPPORTED YET.
           //      assertEquals("number flag t", new java.util.Date(1023400137276L), line.getOptionObject('z'));
           //     input is:  "Thu Jun 06 17:48:57 EDT 2002"
       }
       catch( ParseException exp ) {
           fail( exp.getMessage() );
       }
       catch( java.net.MalformedURLException exp ) {
           fail( exp.getMessage() );
       }
   }

}
