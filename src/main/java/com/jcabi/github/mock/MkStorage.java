/**
 * Copyright (c) 2013-2014, jcabi.com
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
import java.util.ConcurrentModificationException;
import java.util.concurrent.locks.ReentrantLock;
import javax.validation.constraints.NotNull;
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
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@Immutable
public interface MkStorage {

    /**
     * Get full XML.
     * @return XML
     * @throws IOException If there is any I/O problem, or if the current
     *  storage is locked by another thread.
     */
    @NotNull(message = "xml is never NULL")
    XML xml() throws IOException;

    /**
     * Update XML with this directives.
     * @param dirs Directives
     * @throws IOException If there is any I/O problem, or if the current
     *  storage is locked by another thread.
     */
    void apply(
        @NotNull(message = "dirs can't be NULL") Iterable<Directive> dirs
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
     * @throws IOException If there is any I/O problem
     */
    void lock() throws IOException;

    /**
     * Unlock storage.
     *
     * <p>Locking behavior is reentrant, thus if the thread invoked
     * {@link #lock()} multiple times, the hold count is decremented. If the
     * hold count reaches 0, the lock is released.
     *
     * <p>If the current thread does not hold the lock, an
     * {@link IllegalMonitorStateException} will be thrown.
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
         * Lock object.
         */
        private final transient ReentrantLock lock = new ReentrantLock();
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
            @NotNull(message = "file can't be NULL") final File file
        ) throws IOException {
            FileUtils.write(file, "<github/>");
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
        @NotNull(message = "XML is never NULL")
        public XML xml() throws IOException {
            if (this.lock.isLocked() && !this.lock.isHeldByCurrentThread()) {
                throw new ConcurrentModificationException(
                    "lock should be taken before method call"
                );
            }
            this.lock.lock();
            try {
                return new XMLDocument(
                    FileUtils.readFileToString(
                        new File(this.name), Charsets.UTF_8
                    )
                );
            } finally {
                this.lock.unlock();
            }
        }
        @Override
        public void apply(
            @NotNull(message = "dirs cannot be NULL")
            final Iterable<Directive> dirs
        ) throws IOException {
            if (this.lock.isLocked() && !this.lock.isHeldByCurrentThread()) {
                throw new ConcurrentModificationException(
                    "lock should be taken before method call"
                );
            }
            this.lock.lock();
            try {
                FileUtils.write(
                    new File(this.name),
                    new XMLDocument(
                        new Xembler(dirs).apply(this.xml().node())
                    ).toString(),
                    Charsets.UTF_8
                );
            } catch (final ImpossibleModificationException ex) {
                throw new IllegalArgumentException(ex);
            } finally {
                this.lock.unlock();
            }
        }
        @Override
        public void lock() throws IOException {
            this.lock.lock();
        }
        @Override
        public void unlock() throws IOException {
            this.lock.unlock();
        }
    }

}
