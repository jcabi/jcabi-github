/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
 * Storage of GitHub data.
 * @since 0.5
 */
@Immutable
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
    void apply(Iterable<Directive> dirs) throws IOException;

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
     * @since 0.5
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
        private final transient ImmutableReentrantLock lock;

        /**
         * Public ctor.
         * @throws IOException If there is any I/O problem
         */
        public InFile() throws IOException {
            this(MkStorage.InFile.temp());
        }

        /**
         * Public ctor.
         * @param file File to use
         * @throws IOException If there is any I/O problem
         */
        public InFile(final File file) throws IOException {
            this(MkStorage.InFile.blank(file));
        }

        private InFile(final String path) {
            this.name = path;
            this.lock = new ImmutableReentrantLock();
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
            this.lock.lock();
            try {
                return new XMLDocument(
                    FileUtils.readFileToString(
                        new File(this.name), StandardCharsets.UTF_8
                    )
                );
            } finally {
                this.lock.unlock();
            }
        }

        @Override
        public void apply(final Iterable<Directive> dirs) throws IOException {
            this.lock.lock();
            try {
                FileUtils.write(
                    new File(this.name),
                    new XMLDocument(
                        new Xembler(dirs).applyQuietly(this.xml().inner())
                    ).toString(),
                    StandardCharsets.UTF_8
                );
            } finally {
                this.lock.unlock();
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

        private static File temp() throws IOException {
            final File file = File.createTempFile("jcabi-github", ".xml");
            file.deleteOnExit();
            return file;
        }

        private static String blank(final File file) throws IOException {
            FileUtils.write(file, "<github/>", StandardCharsets.UTF_8);
            return file.getAbsolutePath();
        }
    }

    /**
     * Synchronized.
     * @since 0.5
     */
    @Immutable
    @EqualsAndHashCode(of = { "origin", "lock" })
    @Loggable(Loggable.DEBUG)
    @SuppressWarnings("PMD.ConstructorShouldDoInitialization")
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
