/**
 * Copyright (c) 2013-2024, jcabi.com
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
import java.nio.charset.StandardCharsets;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.FileUtils;
import org.xembly.Directive;
import org.xembly.Xembler;

/**
 * Storage of Github data.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.5
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface MkStorage {

    /**
     * Get full XML.
     * @return XML
     * @throws IOException If there is any I/O problem, or if the current
     *  storage is locked by another thread.
     */
    XML xml() throws IOException;

    /**
     * Update XML with this directives.
     * @param dirs Directives
     * @throws IOException If there is any I/O problem, or if the current
     *  storage is locked by another thread.
     */
    void apply(
        Iterable<Directive> dirs
    ) throws IOException;

    /**
     * Locks storage to the current thread.
     *
     * <p>If the lock is available, grant it
     * to the calling thread and block all operations from other threads.
     * If not available, wait for the holder of the lock to release it with
     * {@link #unlock()} before any other operations can be performed.
     *
     * <p>Locking behavior is reentrant, which means a thread can invoke
     * {@link #lock()} multiple times, where a hold count is maintained.
     */
    void lock();

    /**
     * Unlock storage.
     *
     * <p>Locking behavior is reentrant, thus if the thread invoked
     * {@link #lock()} multiple times, the hold count is decremented. If the
     * hold count reaches 0, the lock is released.
     *
     * <p>If the current thread does not hold the lock, an
     * {@link IllegalMonitorStateException} will be thrown.
     */
    void unlock();

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
        public InFile(
            final File file
        ) throws IOException {
            FileUtils.write(file, "<github/>", StandardCharsets.UTF_8);
            this.name = file.getAbsolutePath();
        }
        @Override
        public String toString() {
            try {
                return this.xml().toString();
            } catch (final IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
        @Override
        public XML xml() throws IOException {
            synchronized (this.name) {
                return new XMLDocument(
                    FileUtils.readFileToString(
                        new File(this.name), StandardCharsets.UTF_8
                    )
                );
            }
        }
        @Override
        public void apply(
            final Iterable<Directive> dirs
        ) throws IOException {
            synchronized (this.name) {
                FileUtils.write(
                    new File(this.name),
                    new XMLDocument(
                        new Xembler(dirs).applyQuietly(this.xml().node())
                    ).toString(),
                    StandardCharsets.UTF_8
                );
            }
        }
        @Override
        public void lock() {
            // nothing
        }
        @Override
        public void unlock() {
            // nothing
        }
    }

    /**
     * Syncronized.
     */
    @Immutable
    @EqualsAndHashCode(of = { "origin", "lock" })
    @Loggable(Loggable.DEBUG)
    final class Synced implements MkStorage {
        /**
         * Original storage.
         */
        private final transient MkStorage origin;
        /**
         * Lock object.
         */
        private final transient ImmutableReentrantLock lock =
            new ImmutableReentrantLock();
        /**
         * Public ctor.
         * @param storage Original
         */
        public Synced(final MkStorage storage) {
            this.origin = storage;
        }
        @Override
        public String toString() {
            return this.origin.toString();
        }
        @Override
        public XML xml() throws IOException {
            return this.origin.xml();
        }
        @Override
        public void apply(final Iterable<Directive> dirs) throws IOException {
            this.origin.apply(dirs);
        }
        @Override
        public void lock() {
            this.lock.lock();
        }
        @Override
        public void unlock() {
            this.lock.unlock();
        }
    }

}
