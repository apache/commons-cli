/*
 * Copyright 2004 The Apache Software Foundation
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
 
var Option = new Interface("Option");
Option.addMethod("process(...)");
Option.addMethod("canProcess(...)");
Option.addMethod("getTriggers()");
Option.addMethod("getPrefixes()");
Option.addMethod("validate(...)");
Option.addMethod("helpLines(...)");
Option.addMethod("appendUsage(...)");
Option.addMethod("getPreferredName()");
Option.addMethod("getDescription()");
Option.addMethod("getId()");
Option.addMethod("findOption(...)");

var Group = new Interface("Group");
Group.addMethod("appendUsage(...)");
Group.addNote("-a | -b | -c | -d | -e");

var Parent = new Interface("Parent");
Parent.addMethod("processParent(...)");
Parent.addNote("-f <arg1>");

var Argument = new Interface("Argument");
Argument.addMethod("getInitialSeparator()");
Argument.addMethod("processValues(...)");
Argument.addMethod("validate(...)");
Argument.addNote("<arg1> [<arg2> ...]");

var CommandLine = new Interface("CommandLine");
CommandLine.addMethod("hasOption(...)");
CommandLine.addMethod("getOption(...)");
CommandLine.addMethod("getValue(...)");
CommandLine.addMethod("getValues(...)");
CommandLine.addMethod("getSwitch(...)");
CommandLine.addMethod("getProperty(...)");
CommandLine.addMethod("getProperties()");
CommandLine.addMethod("getOptionCount(...)");
CommandLine.addMethod("getOptions()");
CommandLine.addMethod("getOptionTriggers()");

var WriteableCommandLine = new Interface("WriteableCommandLine");
WriteableCommandLine.addMethod("addOption(...)");
WriteableCommandLine.addMethod("addValue(...)");
WriteableCommandLine.addMethod("addSwitch(...)");
WriteableCommandLine.addMethod("addProperty(...)");
WriteableCommandLine.addMethod("looksLikeOption(...)");





var PropertyOption = new Class("PropertyOption");
PropertyOption.addAttribute("optionString");
PropertyOption.addAttribute("description");
PropertyOption.addAttribute("prefixes");
PropertyOption.addNote("-Dproperty=value");

var DefaultOption = new Class("DefaultOption");
DefaultOption.addAttribute("optionString");
DefaultOption.addAttribute("description");
DefaultOption.addAttribute("prefixes");
DefaultOption.addNote("-f (--file, --filelist)");

var Command = new Class("Command");
Command.addAttribute("preferredName");
Command.addAttribute("aliases");
Command.addAttribute("required");
Command.addAttribute("triggers");
Command.addNote("update (up, upd)");

var Switch = new Class("Switch");
Switch.addAttribute("enabledPrefix");
Switch.addAttribute("disabledPrefix");
Switch.addAttribute("preferredName");
Switch.addAttribute("aliases");
Switch.addAttribute("required");
Switch.addAttribute("triggers");
Switch.addAttribute("prefixes");
Switch.addNote("+d|-d (+display|-display)");

var SourceDestArgument = new Class("SourceDestArgument");
SourceDestArgument.addAttribute("preferredName");
SourceDestArgument.addAttribute("aliases");
SourceDestArgument.addAttribute("required");
SourceDestArgument.addAttribute("triggers");
SourceDestArgument.addNote("<src1> <src2> ... <dst>");




var Parser = new Class("Parser");
Parser.addMethod("parse(...)");
Parser.addMethod("parseAndHelp(...)");
Parser.addMethod("setGroup(...)");
Parser.addMethod("setHelpFormatter(...)");
Parser.addMethod("setHelpOption(...)");
Parser.addMethod("setHelpTrigger(...)");



var DefaultingCommandLine = new Class("DefaultingCommandLine");
DefaultingCommandLine.addMethod("appendCommandLine(...)");
DefaultingCommandLine.addMethod("insertCommandLine(...)");
DefaultingCommandLine.addMethod("commandLines()");

var PropertiesCommandLine = new Class("PropertiesCommandLine");
PropertiesCommandLine.addAttribute("properties");
PropertiesCommandLine.addNote("java.util.Properties");

var PreferencesCommandLine = new Class("PreferencesCommandLine");
PreferencesCommandLine.addAttribute("preferences");
PreferencesCommandLine.addNote("java.util.prefs.Preferences");

