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
package org.apache.commons.cli2.validation;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

/**
 * The <code>FileValidator</code> validates the string argument
 * values are files.  If the value is a file, the string value in
 * the {@link java.util.List} of values is replaced with the
 * {@link java.io.File} instance.
 *
 * The following attributes can also be specified using the
 * appropriate settors:
 * <ul>
 *  <li>writable</li>
 *  <li>readable</li>
 *  <li>hidden</li>
 *  <li>existing</li>
 *  <li>is a file</li>
 *  <li>is a directory</li>
 * </ul>
 *
 * The following example shows how to limit the valid values
 * for the config attribute to files that are readable, writeable,
 * and that already existing.
 *
 * <pre>
 * ...
 * ArgumentBuilder builder = new ArgumentBuilder();
 * FileValidator validator = FileValidator.getExistingFileInstance();
 * validator.setReadable(true);
 * validator.setWritable(true);
 *
 * Argument age =
 *     builder.withName("config");
 *            .withValidator(validator);
 * </pre>
 *
 * @author Rob Oxspring
 * @author John Keyes
 */
public class FileValidator implements Validator {

    /**
     * Returns a <code>FileValidator</code> for existing files/directories.
     *
     * @return a <code>FileValidator</code> for existing files/directories.
     */
    public static FileValidator getExistingInstance() {
        final FileValidator validator = new FileValidator();
        validator.setExisting(true);
        return validator;
    }

    /**
     * Returns a <code>FileValidator</code> for existing files.
     *
     * @return a <code>FileValidator</code> for existing files.
     */
    public static FileValidator getExistingFileInstance() {
        final FileValidator validator = new FileValidator();
        validator.setExisting(true);
        validator.setFile(true);
        return validator;
    }

    /**
     * Returns a <code>FileValidator</code> for existing directories.
     *
     * @return a <code>FileValidator</code> for existing directories.
     */
    public static FileValidator getExistingDirectoryInstance() {
        final FileValidator validator = new FileValidator();
        validator.setExisting(true);
        validator.setDirectory(true);
        return validator;
    }

    /** whether the argument value is readable */
    private boolean readable = false;

    /** whether the argument value is writable */
    private boolean writable = false;

    /** whether the argument value exists */
    private boolean existing = false;

    /** whether the argument value is a directory */
    private boolean directory = false;

    /** whether the argument value is a file */
    private boolean file = false;

    /** whether the argument value is a hidden file or directory */
    private boolean hidden = false;

    /**
     * Validate the list of values against the list of permitted values.
     * If a value is valid, replace the string in the <code>values</code>
     * {@link java.util.List} with the {@link java.io.File} instance.
     *
     * @see org.apache.commons.cli2.validation.Validator#validate(java.util.List)
     */
    public void validate(final List values) throws InvalidArgumentException {
        for (final ListIterator i = values.listIterator(); i.hasNext();) {
            final String name = (String)i.next();
            final File f = new File(name);

            if ((existing && !f.exists())
                || (file && !f.isFile())
                || (directory && !f.isDirectory())
                || (hidden && !f.isHidden())
                || (readable && !f.canRead())
                || (writable && !f.canWrite())) {

                throw new InvalidArgumentException(name);
            }

            i.set(f);
        }
    }

    /**
     * Returns whether the argument values must represent directories.
     *
     * @return whether the argument values must represent directories.
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * Specifies whether the argument values must represent directories.
     *
     * @param directory specifies whether the argument values must
     * represent directories.
     */
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    /**
     * Returns whether the argument values must represent existing
     * files/directories.
     *
     * @return whether the argument values must represent existing
     * files/directories.
     */
    public boolean isExisting() {
        return existing;
    }

    /**
     * Specifies whether the argument values must represent existing
     * files/directories.
     *
     * @param existing specifies whether the argument values must
     * represent existing files/directories.
     */
    public void setExisting(boolean existing) {
        this.existing = existing;
    }

    /**
     * Returns whether the argument values must represent directories.
     *
     * @return whether the argument values must represent directories.
     */
    public boolean isFile() {
        return file;
    }

    /**
     * Specifies whether the argument values must represent files.
     *
     * @param file specifies whether the argument values must
     * represent files.
     */
    public void setFile(boolean file) {
        this.file = file;
    }

    /**
     * Returns whether the argument values must represent hidden
     * files/directories.
     *
     * @return whether the argument values must represent hidden
     * files/directories.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Specifies whether the argument values must represent hidden
     * files/directories.
     *
     * @param hidden specifies whether the argument values must
     * represent hidden files/directories.
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Returns whether the argument values must represent readable
     * files/directories.
     *
     * @return whether the argument values must represent readable
     * files/directories.
     */
    public boolean isReadable() {
        return readable;
    }

    /**
     * Specifies whether the argument values must represent readable
     * files/directories.
     *
     * @param readable specifies whether the argument values must
     * represent readable files/directories.
     */
    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    /**
     * Returns whether the argument values must represent writable
     * files/directories.
     *
     * @return whether the argument values must represent writable
     * files/directories.
     */
    public boolean isWritable() {
        return writable;
    }

    /**
     * Specifies whether the argument values must represent writable
     * files/directories.
     *
     * @param writable specifies whether the argument values must
     * represent writable files/directories.
     */
    public void setWritable(boolean writable) {
        this.writable = writable;
    }
}
