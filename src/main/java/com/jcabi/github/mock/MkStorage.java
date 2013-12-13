/**
 * Copyright (c) 2012-2013, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.File;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.xembly.Directive;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Storage of Github data.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
public interface MkStorage {

    /**
     * Get full XML.
     * @return XML
     * @throws IOException If there is any I/O problem
     */
    XML xml() throws IOException;

    /**
     * Update XML with this directives.
     * @param dirs Directives
     * @throws IOException If there is any I/O problem
     */
    void apply(Iterable<Directive> dirs) throws IOException;

    /**
     * Lock storage.
     * @throws IOException If there is any I/O problem
     */
    void lock() throws IOException;

    /**
     * Unlock storage.
     * @throws IOException If there is any I/O problem
     */
    void unlock() throws IOException;

    /**
     * In file.
     */
    @Immutable
    @EqualsAndHashCode(of = "name")
    @Loggable(Loggable.DEBUG)
    final class InFile implements MkStorage {
        /**
         * File name.
         */
        private final transient String name;
        /**
         * Public ctor.
         * @throws IOException If there is any I/O problem
         */
        public InFile() throws IOException {
            this(File.createTempFile("jcabi-github", ".xml"));
            new File(this.name).deleteOnExit();
        }
        /**
         * Public ctor.
         * @param file File to use
         * @throws IOException If there is any I/O problem
         */
        public InFile(final File file) throws IOException {
            FileUtils.write(file, "<github/>");
            this.name = file.getAbsolutePath();
        }
        @Override
        public String toString() {
            try {
                return this.xml().toString();
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
        @Override
        public XML xml() throws IOException {
            return new XMLDocument(
                FileUtils.readFileToString(new File(this.name), Charsets.UTF_8)
            );
        }
        @Override
        public void apply(final Iterable<Directive> dirs) throws IOException {
            try {
                FileUtils.write(
                    new File(this.name),
                    new XMLDocument(
                        new Xembler(dirs).apply(this.xml().node())
                    ).toString(),
                    Charsets.UTF_8
                );
            } catch (ImpossibleModificationException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        @Override
        public void lock() throws IOException {
            // nothing to do
        }
        @Override
        public void unlock() throws IOException {
            // nothing to do
        }
    }

}
