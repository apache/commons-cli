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

/** 
 * A sample program shpwing the use of Options and the HelpFormatter class 
 *
 * @author Slawek Zachcial
 **/
public class HelpFormatterExamples
{
   // --------------------------------------------------------------- Constants

   // ------------------------------------------------------------------ Static

   public static void main( String[] args )
   {
      System.out.println("\n#\n# 'man' example\n#");
      manExample();
/*
      System.out.println("\n#\n# 'bzip2' example\n#");
      bzip2Example();
      System.out.println("\n#\n# 'ls' example\n#");
      lsExample();
*/
   }

   static void manExample()
   {
      String cmdLine =
         "man [-c|-f|-k|-w|-tZT device] [-adlhu7V] [-Mpath] [-Ppager] [-Slist] " +
         "[-msystem] [-pstring] [-Llocale] [-eextension] [section] page ...";
      Options opts =
         new Options().
         addOption("a", "all",            false, "find all matching manual pages.").
         addOption("d", "debug",          false, "emit debugging messages.").
         addOption("e", "extension",      false, "limit search to extension type 'extension'.").
         addOption("f", "whatis",         false, "equivalent to whatis.").
         addOption("k", "apropos",        false, "equivalent to apropos.").
         addOption("w", "location",       false, "print physical location of man page(s).").
         addOption("l", "local-file",     false, "interpret 'page' argument(s) as local filename(s)").
         addOption("u", "update",         false, "force a cache consistency check.").
         //FIXME - should generate -r,--prompt string
         addOption("r", "prompt",         true,  "provide 'less' pager with prompt.").
         addOption("c", "catman",         false, "used by catman to reformat out of date cat pages.").
         addOption("7", "ascii",          false, "display ASCII translation or certain latin1 chars.").
         addOption("t", "troff",          false, "use troff format pages.").
         //FIXME - should generate -T,--troff-device device
         addOption("T", "troff-device",   true,  "use groff with selected device.").
         addOption("Z", "ditroff",        false, "use groff with selected device.").
         addOption("D", "default",        false, "reset all options to their default values.").
         //FIXME - should generate -M,--manpath path
         addOption("M", "manpath",        true,  "set search path for manual pages to 'path'.").
         //FIXME - should generate -P,--pager pager
         addOption("P", "pager",          true,  "use program 'pager' to display output.").
         //FIXME - should generate -S,--sections list
         addOption("S", "sections",       true,  "use colon separated section list.").
         //FIXME - should generate -m,--systems system
         addOption("m", "systems",        true,  "search for man pages from other unix system(s).").
         //FIXME - should generate -L,--locale locale
         addOption("L", "locale",         true,  "defaine the locale for this particular man search.").
         //FIXME - should generate -p,--preprocessor string
         addOption("p", "preprocessor",   true,  "string indicates which preprocessor to run.\n" +
                                                 " e - [n]eqn  p - pic     t - tbl\n" +
                                                 " g - grap    r - refer   v - vgrind").
         addOption("V", "version",        false, "show version.").
         addOption("h", "help",           false, "show this usage message.");

      HelpFormatter hf = new HelpFormatter();
      //hf.printHelp(cmdLine, opts);
      hf.printHelp(60, cmdLine, null, opts, null);
   }

   static void bzip2Example()
   {
      System.out.println( "Coming soon" );
   }

   static void lsExample()
   {
      System.out.println( "Coming soon" );
   }


   // -------------------------------------------------------------- Attributes

   // ------------------------------------------------------------ Constructors
   
   // ------------------------------------------------------------------ Public

   // --------------------------------------------------------------- Protected

   // ------------------------------------------------------- Package protected   
   
   // ----------------------------------------------------------------- Private
   
   // ----------------------------------------------------------- Inner classes

}
