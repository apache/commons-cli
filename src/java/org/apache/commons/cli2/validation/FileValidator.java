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
package org.apache.commons.cli2.validation;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

/**
 * A Validator implmentation requiring File values 
 */
public class FileValidator implements Validator {

    /**
     * @return an instance requiring existing entries 
     */
    public static FileValidator getExistingInstance() {
        final FileValidator validator = new FileValidator();
        validator.setExisting(true);
        return validator;
    }

    /**
     * @return an instance requiring existing files
     */
    public static FileValidator getExistingFileInstance() {
        final FileValidator validator = new FileValidator();
        validator.setExisting(true);
        validator.setFile(true);
        return validator;
    }

    /**
     * @return an instance requiring existing directories
     */
    public static FileValidator getExistingDirectoryInstance() {
        final FileValidator validator = new FileValidator();
        validator.setExisting(true);
        validator.setDirectory(true);
        return validator;
    }

    private boolean readable = false;
    private boolean writable = false;
    private boolean existing = false;
    private boolean directory = false;
    private boolean file = false;
    private boolean hidden = false;

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
     * @return true iff the file is a directory
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * @param directory true if the file must be a directory
     */
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    /**
     * @return true iff the file exists
     */
    public boolean isExisting() {
        return existing;
    }

    /**
     * @param existing true if the file must exist
     */
    public void setExisting(boolean existing) {
        this.existing = existing;
    }

    /**
     * @return true iff the file is a file (not directory)
     */
    public boolean isFile() {
        return file;
    }

    /**
     * @param file true if the file must be a file (not directory)
     */
    public void setFile(boolean file) {
        this.file = file;
    }

    /**
     * @return true iff the file must be hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @param hidden true if the file must be hidden
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * @return true iff the file must be readable
     */
    public boolean isReadable() {
        return readable;
    }

    /**
     * @param readable true if the file must be readable
     */
    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    /**
     * @return true iff the file must be writable
     */
    public boolean isWritable() {
        return writable;
    }

    /**
     * @param writable true if the file must be writable
     */
    public void setWritable(boolean writable) {
        this.writable = writable;
    }
}
