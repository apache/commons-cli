/*
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





var ArgumentBuilder = new Class("ArgumentBuilder");
ArgumentBuilder.addMethod("withId(...)");
ArgumentBuilder.addMethod("withName(...)");
ArgumentBuilder.addMethod("withDescription(...)");
ArgumentBuilder.addMethod("withConsumeRemaining(...)");
ArgumentBuilder.addMethod("withValidator(...)");
ArgumentBuilder.addMethod("withMinimum(...)");
ArgumentBuilder.addMethod("withMaximum(...)");
ArgumentBuilder.addMethod("withDefault(...)");
ArgumentBuilder.addMethod("withDefaults(...)");
ArgumentBuilder.addMethod("withInitialSeparator(...)");
ArgumentBuilder.addMethod("withSubsequentSeparator(...)");
ArgumentBuilder.addMethod("create()");
ArgumentBuilder.addMethod("reset()");

var CommandBuilder = new Class("CommandBuilder");
CommandBuilder.addMethod("withId(...)");
CommandBuilder.addMethod("withName(...)");
CommandBuilder.addMethod("withDescription(...)");
CommandBuilder.addMethod("withArgument(...)");
CommandBuilder.addMethod("withChildren(...)");
CommandBuilder.addMethod("withRequired(...)");
CommandBuilder.addMethod("create()");
CommandBuilder.addMethod("reset()");

var DefaultOptionBuilder = new Class("DefaultOptionBuilder");
DefaultOptionBuilder.addMethod("withId(...)");
DefaultOptionBuilder.addMethod("withShortName(...)");
DefaultOptionBuilder.addMethod("withLongName(...)");
DefaultOptionBuilder.addMethod("withDescription(...)");
DefaultOptionBuilder.addMethod("withArgument(...)");
DefaultOptionBuilder.addMethod("withChildren(...)");
DefaultOptionBuilder.addMethod("withRequired(...)");
DefaultOptionBuilder.addMethod("create()");
DefaultOptionBuilder.addMethod("reset()");
DefaultOptionBuilder.addAttribute("shortPrefix");
DefaultOptionBuilder.addAttribute("longPrefix");
DefaultOptionBuilder.addAttribute("burstEnabled");

var DefaultOptionBuilder = new Class("DefaultOptionBuilder");
DefaultOptionBuilder.addMethod("withId(...)");
DefaultOptionBuilder.addMethod("withShortName(...)");
DefaultOptionBuilder.addMethod("withLongName(...)");
DefaultOptionBuilder.addMethod("withDescription(...)");
DefaultOptionBuilder.addMethod("withArgument(...)");
DefaultOptionBuilder.addMethod("withChildren(...)");
DefaultOptionBuilder.addMethod("withRequired(...)");
DefaultOptionBuilder.addMethod("create()");
DefaultOptionBuilder.addMethod("reset()");

var GroupBuilder = new Class("GroupBuilder");
GroupBuilder.addMethod("withName(...)");
GroupBuilder.addMethod("withDescription(...)");
GroupBuilder.addMethod("withOption(...)");
GroupBuilder.addMethod("withMinimum(...)");
GroupBuilder.addMethod("withMaximum(...)");
GroupBuilder.addMethod("create()");
GroupBuilder.addMethod("reset()");

var PatternBuilder = new Class("PatternBuilder");
PatternBuilder.addMethod("withPattern(...)");
PatternBuilder.addMethod("create()");
PatternBuilder.addMethod("reset()");
PatternBuilder.addAttribute("groupBuilder");
PatternBuilder.addAttribute("optionBuilder");
PatternBuilder.addAttribute("argumentBuilder");

var SwitchBuilder = new Class("SwitchBuilder");
SwitchBuilder.addMethod("withId(...)");
SwitchBuilder.addMethod("withName(...)");
SwitchBuilder.addMethod("withDescription(...)");
SwitchBuilder.addMethod("withArgument(...)");
SwitchBuilder.addMethod("withChildren(...)");
SwitchBuilder.addMethod("withRequired(...)");
SwitchBuilder.addMethod("create()");
SwitchBuilder.addMethod("reset()");
SwitchBuilder.addAttribute("enabledPrefix");
SwitchBuilder.addAttribute("disabledPrefix");



var Validator = new Class("Validator");
Validator.addMethod("validate(...)");

var ClassValidator = new Class("ClassValidator");
ClassValidator.addAttribute("classLoader");
ClassValidator.addAttribute("instance");
ClassValidator.addAttribute("loadable");

var DateValidator = new Class("DateValidator");
DateValidator.addAttribute("formats");
DateValidator.addAttribute("minimum");
DateValidator.addAttribute("maximum");

var EnumValidator = new Class("EnumValidator");
EnumValidator.addAttribute("validValues");

var FileValidator = new Class("FileValidator");
FileValidator.addAttribute("directory");
FileValidator.addAttribute("existing");
FileValidator.addAttribute("file");
FileValidator.addAttribute("hidden");
FileValidator.addAttribute("readable");
FileValidator.addAttribute("writable");

var FileValidator = new Class("FileValidator");
FileValidator.addAttribute("format");
FileValidator.addAttribute("minimum");
FileValidator.addAttribute("maximum");

var UrlValidator = new Class("UrlValidator");
UrlValidator.addAttribute("format");
UrlValidator.addAttribute("minimum");
UrlValidator.addAttribute("maximum");




var Comparators = new Class("Comparators");
Comparators.addMethod("chain(...)");
Comparators.addMethod("commandFirst(...)");
Comparators.addMethod("commandLast(...)");
Comparators.addMethod("defaultOptionFirst(...)");
Comparators.addMethod("defaultOptionLast(...)");
Comparators.addMethod("groupFirst(...)");
Comparators.addMethod("groupLast(...)");
Comparators.addMethod("namedFirst(...)");
Comparators.addMethod("namedLast(...)");
Comparators.addMethod("preferredNameFirst(...)");
Comparators.addMethod("preferredNameLast(...)");
Comparators.addMethod("requiredFirst(...)");
Comparators.addMethod("requiredLast(...)");
Comparators.addMethod("switchFirst(...)");
Comparators.addMethod("switchLast(...)");

var HelpFormatter = new Class("HelpFormatter");
HelpFormatter.addMethod("print()");
HelpFormatter.addMethod("printDivider()");
HelpFormatter.addMethod("printException()");
HelpFormatter.addMethod("printFooter()");
HelpFormatter.addMethod("printGutterLeft()");
HelpFormatter.addMethod("printGutterRight()");
HelpFormatter.addMethod("printHeader()");
HelpFormatter.addMethod("printHelp()");
HelpFormatter.addMethod("printUsage()");
HelpFormatter.addAttribute("comparator");
HelpFormatter.addAttribute("displaySettings");
HelpFormatter.addAttribute("divider");
HelpFormatter.addAttribute("exception");
HelpFormatter.addAttribute("footer");
HelpFormatter.addAttribute("fullUsageSettings");
HelpFormatter.addAttribute("group");
HelpFormatter.addAttribute("gutterCenter");
HelpFormatter.addAttribute("gutterLeft");
HelpFormatter.addAttribute("gutterRight");
HelpFormatter.addAttribute("header");
HelpFormatter.addAttribute("lineUsageSettings");
HelpFormatter.addAttribute("pageWidth");
HelpFormatter.addAttribute("printWriter");
HelpFormatter.addAttribute("shellCommand");
