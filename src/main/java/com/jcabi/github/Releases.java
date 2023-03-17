/**
 * Copyright (c) 2013-2023, jcabi.com
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
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github Releases.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 */
@Immutable
public interface Releases {
    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Iterate them all.
     * @return Iterator of releases
     * @see <a href="http://developer.github.com/v3/repos/releases/#list">List</a>
     */
    Iterable<Release> iterate();

    /**
     * Get a single release.
     * @param number Release id
     * @return Release
     * @see <a href="http://developer.github.com/v3/repos/releases/#get-a-single-release">Get a single release</a>
     */
    Release get(int number);

    /**
     * Create new release.
     * @param tag The name of the tag
     * @return Release just created
     * @throws java.io.IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/releases/#create-a-release">Create an Release</a>
     */
    Release create(
        String tag)
        throws IOException;

    /**
     * Remove a release.
     *
     * @param number ID of the release to remove.
     * @throws IOException If an IO problem occurs.
     * @see <a href="http://developer.github.com/v3/repos/releases/#delete-a-release">Delete a release.</a>
     */
    void remove(int number) throws IOException;

    /**
     * Smart releases.
     * @since 0.17
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "releases")
    final class Smart implements Releases {
        /**
         * Encapsulated releases.
         */
        private final transient Releases releases;

        /**
         * Public CTOR.
         * @param original Original releases
         */
        public Smart(
            final Releases original
        ) {
            this.releases = original;
        }
        @Override
        public Repo repo() {
            return this.releases.repo();
        }
        @Override
        public Iterable<Release> iterate() {
            return this.releases.iterate();
        }
        @Override
        public Release get(final int number) {
            return this.releases.get(number);
        }
        @Override
        public Release create(final String tag) throws IOException {
            return this.releases.create(tag);
        }
        @Override
        public void remove(final int number) throws IOException {
            this.releases.remove(number);
        }
        /**
         * This release exists by the tag.
         * @param tag The tag
         * @return TRUE if it already exists
         * @throws IOException If fails
         */
        public boolean exists(final String tag) throws IOException {
            boolean exists = false;
            final Iterable<Release.Smart> rels = new Smarts<>(
                this.iterate()
            );
            for (final Release.Smart rel : rels) {
                if (rel.tag().equals(tag)) {
                    exists = true;
                    break;
                }
            }
            return exists;
        }
        /**
         * Find release by the tag (runtime exception if not found).
         * @param tag The tag
         * @return Release found
         * @throws IOException If fails
         */
        public Release find(final String tag) throws IOException {
            Release found = null;
            final Iterable<Release.Smart> rels = new Smarts<>(
                this.iterate()
            );
            for (final Release.Smart rel : rels) {
                if (rel.tag().equals(tag)) {
                    found = rel;
                    break;
                }
            }
            if (found == null) {
                throw new IllegalArgumentException(
                    String.format("release not found by tag '%s'", tag)
                );
            }
            return found;
        }
    }
}
