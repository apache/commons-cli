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
package org.apache.commons.cli2.application;

import junit.framework.TestCase;

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.CommandBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.option.ArgumentTest;

//TODO Build up CvsTest like CpTest
public class CvsTest extends TestCase {
    public void testCVS() {
        final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        final ArgumentBuilder abuilder = new ArgumentBuilder();
        final CommandBuilder cbuilder = new CommandBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();

        final Group commands =
            gbuilder
                .withName("commands")
                .withOption(
                    cbuilder
                        .withName("add")
                        .withName("ad")
                        .withName("new")
                        .withDescription("Add a new file/directory to the repository")
                        .create())
                .withOption(
                    cbuilder
                        .withName("admin")
                        .withName("adm")
                        .withName("rcs")
                        .withDescription("Administration front end for rcs")
                        .create())
                .withOption(
                    cbuilder
                        .withName("annotate")
                        .withName("ann")
                        .withDescription("Show last revision where each line was modified")
                        .create())
                .withOption(
                    cbuilder
                        .withName("checkout")
                        .withName("co")
                        .withName("get")
                        .withDescription("Checkout sources for editing")
                        .create())
                .withOption(
                    cbuilder
                        .withName("commit")
                        .withName("ci")
                        .withName("com")
                        .withDescription("Check files into the repository")
                        .create())
                .withOption(
                    cbuilder
                        .withName("diff")
                        .withName("di")
                        .withName("dif")
                        .withDescription("Show differences between revisions")
                        .create())
                .withOption(
                    cbuilder
                        .withName("edit")
                        .withDescription("Get ready to edit a watched file")
                        .create())
                .withOption(
                    cbuilder
                        .withName("editors")
                        .withDescription("See who is editing a watched file")
                        .create())
                .withOption(
                    cbuilder
                        .withName("export")
                        .withName("exp")
                        .withName("ex")
                        .withDescription("Export sources from CVS, similar to checkout")
                        .create())
                .withOption(
                    cbuilder
                        .withName("history")
                        .withName("hi")
                        .withName("his")
                        .withDescription("Show repository access history")
                        .create())
                .withOption(
                    cbuilder
                        .withName("import")
                        .withName("im")
                        .withName("imp")
                        .withDescription("Import sources into CVS, using vendor branches")
                        .create())
                .withOption(
                    cbuilder
                        .withName("init")
                        .withDescription("Create a CVS repository if it doesn't exist")
                        .create())
                .withOption(
                    cbuilder
                        .withName("log")
                        .withName("lo")
                        .withName("rlog")
                        .withDescription("Print out history information for files")
                        .create())
                .withOption(
                    cbuilder
                        .withName("login")
                        .withName("logon")
                        .withName("lgn")
                        .withDescription("Prompt for password for authenticating server")
                        .create())
                .withOption(
                    cbuilder
                        .withName("logout")
                        .withDescription("Removes entry in .cvspass for remote repository")
                        .create())
                .withOption(
                    cbuilder
                        .withName("rdiff")
                        .withName("patch")
                        .withName("pa")
                        .withDescription("Create 'patch' format diffs between releases")
                        .create())
                .withOption(
                    cbuilder
                        .withName("release")
                        .withName("re")
                        .withName("rel")
                        .withDescription("Indicate that a Module is no longer in use")
                        .create())
                .withOption(
                    cbuilder
                        .withName("remove")
                        .withName("rm")
                        .withName("delete")
                        .withDescription("Remove an entry from the repository")
                        .create())
                .withOption(
                    cbuilder
                        .withName("rtag")
                        .withName("rt")
                        .withName("rfreeze")
                        .withDescription("Add a symbolic tag to a module")
                        .create())
                .withOption(
                    cbuilder
                        .withName("status")
                        .withName("st")
                        .withName("stat")
                        .withDescription("Display status information on checked out files")
                        .create())
                .withOption(
                    cbuilder
                        .withName("tag")
                        .withName("ta")
                        .withName("freeze")
                        .withDescription("Add a symbolic tag to checked out version of files")
                        .create())
                .withOption(
                    cbuilder
                        .withName("unedit")
                        .withDescription("Undo an edit command")
                        .create())
                .withOption(
                    cbuilder
                        .withName("update")
                        .withName("up")
                        .withName("upd")
                        .withDescription("Bring work tree in sync with repository")
                        .create())
                .withOption(
                    cbuilder
                        .withName("watch")
                        .withDescription("Set watches")
                        .create())
                .withOption(
                    cbuilder
                        .withName("watchers")
                        .withDescription("See who is watching a file")
                        .create())
                .withOption(
                    cbuilder
                        .withName("version")
                        .withName("ve")
                        .withName("ver")
                        .withDescription("????")
                        .create())
                .withOption(ArgumentTest.buildTargetsArgument())
                .create();

        final Group cvsOptions =
            new GroupBuilder()
                .withName("cvs-options")
                .withOption(
                    obuilder
                        .withShortName("H")
                        .withDescription("Displays usage information for command.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("Q")
                        .withDescription("Cause CVS to be really quiet.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("q")
                        .withDescription("Cause CVS to be somewhat quiet.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("r")
                        .withDescription("Make checked-out files read-only.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("w")
                        .withDescription("Make checked-out files read-write (default).")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("l")
                        .withDescription("Turn history logging off.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("n")
                        .withDescription("Do not execute anything that will change the disk.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("t")
                        .withDescription("Show trace of program execution -- try with -n.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("v")
                        .withDescription("CVS version and copyright.")
                        .create())
                .withOption(
                    obuilder
                        .withLongName("crlf")
                        .withDescription("Use the Dos line feed for text files (default).")
                        .create())
                .withOption(
                    obuilder
                        .withLongName("lf")
                        .withDescription("Use the Unix line feed for text files.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("T")
                        .withDescription("Use 'tmpdir' for temporary files.")
                        .withArgument(abuilder.withName("tmpdir").create())
                        .create())
                .withOption(
                    obuilder
                        .withShortName("e")
                        .withDescription("Use 'editor' for editing log information.")
                        .withArgument(abuilder.withName("editor").create())
                        .create())
                .withOption(
                    obuilder
                        .withShortName("d")
                        .withDescription("Overrides $CVSROOT as the root of the CVS tree.")
                        .withArgument(abuilder.withName("CVS_root").create())
                        .create())
                .withOption(
                    obuilder
                        .withShortName("f")
                        .withDescription("Do not use the ~/.cvsrc file.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("z")
                        .withDescription("Use compression level '#' for net traffic.")
                        .withArgument(abuilder.withName("#").create())
                        .create())
                .withOption(
                    obuilder
                        .withShortName("a")
                        .withDescription("Authenticate all net traffic.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("s")
                        .withDescription("Set CVS user variable.")
                        .withArgument(abuilder.withName("VAR=VAL").create())
                        .create())
                .withOption(commands)
                .create();

        assertNotNull(cvsOptions);
    }
}
