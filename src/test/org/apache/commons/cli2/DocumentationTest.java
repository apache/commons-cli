/**
 * Copyright 2003-2004 The Apache Software Foundation
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
package org.apache.commons.cli2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.commandline.WriteableCommandLineImpl;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.option.PropertyOption;
import org.apache.commons.cli2.util.HelpFormatter;

/**
 * @author Rob
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class DocumentationTest extends TestCase {

    public void testBasicUsage() throws IOException, OptionException {
        HelpFormatter helpFormatter = new HelpFormatter();
        //ignore all printed
        helpFormatter.setPrintWriter(new PrintWriter(new StringWriter()));

        /*
         * --version -? -h --help -log file -s|-q|-v|-d Bursting File/Num/Date
         * validation Switches Commands Auto help Auto exception help
         *  
         */
        DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        Option version =
            obuilder
                .withLongName("version")
                .withDescription("Displays version information and then exits")
                .create();

        Option help =
            obuilder
                .withShortName("h")
                .withShortName("?")
                .withLongName("help")
                .withDescription("Displays help on usage and then exits")
                .create();

        ArgumentBuilder abuilder = new ArgumentBuilder();
        Argument logFile =
            abuilder
                .withDescription("The log file to write to")
                .withName("file")
                .withMinimum(1)
                .withMaximum(1)
                .create();
        Option log =
            obuilder
                .withArgument(logFile)
                .withShortName("log")
                .withDescription("Log progress information to a file")
                .create();

        GroupBuilder gbuilder = new GroupBuilder();
        Group outputQuality =
            gbuilder
                .withName("quality")
                .withDescription("Controls the quality of console output")
                .withMaximum(1)
                .withOption(
                    obuilder
                        .withShortName("s")
                        .withDescription("Silent")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("q")
                        .withDescription("Quiet")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("n")
                        .withDescription("Normal")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("v")
                        .withDescription("Verbose")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("d")
                        .withDescription("Debug")
                        .create())
                .create();

        Group options =
            new GroupBuilder()
                .withName("options")
                .withOption(version)
                .withOption(help)
                .withOption(log)
                .withOption(outputQuality)
                .create();

        final String[] args = new String[] { "--bad-option" };

        Parser parser = new Parser();
        parser.setHelpFormatter(helpFormatter);
        parser.setGroup(options);
        parser.setHelpOption(help);
        CommandLine commandLine = parser.parseAndHelp(args);
        if (commandLine != null) {
            if (commandLine.hasOption(version)) {
                System.out.println("MyApp ver 1.0");
                return;
            }
            if (commandLine.hasOption("-log")) {
                String filename = (String)commandLine.getValue("-log");
                //...
            }
        }

        try {
            commandLine = parser.parse(args);
            fail("Unexpected Option!");
        }
        catch (OptionException uoe) {
            assertEquals(
                "Unexpected --bad-option while processing options",
                uoe.getMessage());
        }
    }
    
    public void testManualIntroduction() {
        
        DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        ArgumentBuilder aBuilder = new ArgumentBuilder();
        GroupBuilder gBuilder = new GroupBuilder();
        
        DefaultOption xmlOption = 
            oBuilder
                .withLongName("xml")
                .withDescription("Output using xml format")
                .create();
        
        Argument pathArgument = 
            aBuilder
                .withName("path")
                .withMinimum(1)
                .withMaximum(1)
                .create();
        
        Group outputChildren = 
            gBuilder
                .withOption(xmlOption)
                .create();
        
        Option outputOption = 
            oBuilder
                .withLongName("output")
                .withDescription("Outputs to a file")
                .withArgument(pathArgument)
                .withChildren(outputChildren)
                .create();
        
        ///////////////////////////////////////////////////
        
        try {
            Group options = outputChildren;
            HelpFormatter hf = new HelpFormatter();
            
            Parser p = new Parser();
            p.setGroup(options);
            p.setHelpFormatter(hf);
            p.setHelpTrigger("--help");
            CommandLine cl = p.parseAndHelp(new String[]{});
            if(cl==null) {
                System.exit(-1);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //////////////////////////////////////////////////
        
        CommandLine cl = new WriteableCommandLineImpl(outputChildren,new ArrayList());
        
        // if we have --output option
        if(cl.hasOption("--output")) {
            // grab the path
            String path = (String)cl.getValue("--output");
            // grab the format
            boolean xml = cl.hasOption("--xml");
            // configure the application's output
            configureOutput(path,xml);
        }
        
        
                
        
    }

    private void configureOutput(String path, boolean xml) {
        // TODO Auto-generated method stub
        
    }

    public void testExampleAnt() throws IOException, OptionException {
        // Apache Ant version 1.6.1 compiled on February 12 2004

        final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        final ArgumentBuilder abuilder = new ArgumentBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();

        Option help =
            obuilder
                .withShortName("help")
                .withShortName("h")
                .withDescription("print this message")
                .create();
        Option projecthelp =
            obuilder
                .withShortName("projecthelp")
                .withShortName("p")
                .withDescription("print project help information")
                .create();
        Option version =
            obuilder
                .withShortName("version")
                .withDescription("print the version information and exit")
                .create();
        Option diagnostics =
            obuilder
                .withShortName("diagnostics")
                .withDescription("print information that might be helpful to diagnose or report problems.")
                .create();
        Option quiet =
            obuilder
                .withShortName("quiet")
                .withShortName("q")
                .withDescription("be extra quiet")
                .create();
        Option verbose =
            obuilder
                .withShortName("verbose")
                .withShortName("v")
                .withDescription("be extra verbose")
                .create();
        Option debug =
            obuilder
                .withShortName("debug")
                .withShortName("d")
                .withDescription("print debugging information")
                .create();
        Option emacs =
            obuilder
                .withShortName("emacs")
                .withShortName("e")
                .withDescription("produce logging information without adornments")
                .create();
        Option lib =
            obuilder
                .withShortName("lib")
                .withDescription("specifies a path to search for jars and classes")
                .withArgument(
                    abuilder
                        .withName("path")
                        .withMinimum(1)
                        .withMaximum(1)
                        .create())
                .create();
        Option logfile =
            obuilder
                .withShortName("logfile")
                .withShortName("l")
                .withDescription("use given file for log")
                .withArgument(
                    abuilder
                        .withName("file")
                        .withMinimum(1)
                        .withMaximum(1)
                        .create())
                .create();
        Option logger =
            obuilder
                .withShortName("logger")
                .withDescription("the class which is to perform logging")
                .withArgument(
                    abuilder
                        .withName("classname")
                        .withMinimum(1)
                        .withMaximum(1)
                        .create())
                .create();
        Option listener =
            obuilder
                .withShortName("listener")
                .withDescription("add an instance of class as a project listener")
                .withArgument(
                    abuilder
                        .withName("classname")
                        .withMinimum(1)
                        .withMaximum(1)
                        .create())
                .create();
        Option noinput =
            obuilder
                .withShortName("noinput")
                .withDescription("do not allow interactive input")
                .create();
        Option buildfile =
            obuilder
                .withShortName("buildfile")
                .withShortName("file")
                .withShortName("f")
                .withDescription("use given buildfile")
                .withArgument(
                    abuilder
                        .withName("file")
                        .withMinimum(1)
                        .withMaximum(1)
                        .create())
                .create();
        Option property = new PropertyOption();
        Option propertyfile =
            obuilder
                .withShortName("propertyfile")
                .withDescription("load all properties from file with -D properties taking precedence")
                .withArgument(
                    abuilder
                        .withName("name")
                        .withMinimum(1)
                        .withMaximum(1)
                        .create())
                .create();
        Option inputhandler =
            obuilder
                .withShortName("inputhandler")
                .withDescription("the class which will handle input requests")
                .withArgument(
                    abuilder
                        .withName("class")
                        .withMinimum(1)
                        .withMaximum(1)
                        .create())
                .create();
        Option find =
            obuilder
                .withShortName("find")
                .withShortName("s")
                .withDescription("search for buildfile towards the root of the filesystem and use it")
                .withArgument(
                    abuilder
                        .withName("file")
                        .withMinimum(1)
                        .withMaximum(1)
                        .create())
                .create();
        Option targets = abuilder.withName("target").create();

        Group options =
            gbuilder
                .withName("options")
                .withOption(help)
                .withOption(projecthelp)
                .withOption(version)
                .withOption(diagnostics)
                .withOption(quiet)
                .withOption(verbose)
                .withOption(debug)
                .withOption(emacs)
                .withOption(lib)
                .withOption(logfile)
                .withOption(logger)
                .withOption(listener)
                .withOption(noinput)
                .withOption(buildfile)
                .withOption(property)
                .withOption(propertyfile)
                .withOption(inputhandler)
                .withOption(find)
                .withOption(targets)
                .create();
        
        /////////////////////////////////////
        String[] args = new String[]{};
        
        Parser parser = new Parser();
        parser.setGroup(options);
        CommandLine cl = parser.parse(args);
        
        if(cl.hasOption(help)) {
            //displayHelp();
            return;
        }
        if(cl.hasOption("-version")) {
            //displayVersion();
            return;
        }
        if(cl.hasOption(logfile)) {
            String file = (String)cl.getValue(logfile);
            //setLogFile();
        }
        List targetList = cl.getValues(targets);
        for (Iterator i = targetList.iterator(); i.hasNext();) {
            String target = (String) i.next();
            //doTarget(target);
        }
        
        /////////////////////////////////////

        HelpFormatter hf = new HelpFormatter();
        hf.setShellCommand("ant");
        hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_NAME);
        hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_ARGUMENT);
        hf.getFullUsageSettings().remove(DisplaySetting.DISPLAY_GROUP_EXPANDED);

        hf.getLineUsageSettings().add(DisplaySetting.DISPLAY_PROPERTY_OPTION);
        hf.getLineUsageSettings().add(DisplaySetting.DISPLAY_PARENT_ARGUMENT);
        hf.getLineUsageSettings().add(DisplaySetting.DISPLAY_ARGUMENT_BRACKETED);

        hf.getDisplaySettings().remove(DisplaySetting.DISPLAY_GROUP_ARGUMENT);

        hf.setGroup(options);
        // redirect printed stuff to a string
        hf.setPrintWriter(new PrintWriter(new StringWriter()));
        hf.print();

    }
}
