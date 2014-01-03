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
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github gist.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/gists/">Gists API</a>
 * @todo #1:1hr New method star() and starred() for a single gist. Let's
 *  add these methods as explained in
 *  http://developer.github.com/v3/gists/
 *  The method should be tested by integration and unit tests, and implemented
 *  in MkGist as well. When done, remove this comment.
 * @todo #19 Getting "Too many methods" error message. Does this class need
 *  refactoring?
 * @todo #1:1hr New method fork() to fork a gist. Let's introduce
 *  a new method, as explained in
 *  http://developer.github.com/v3/gists/#fork-a-gist. The method should
 *  be tested in a unit and integration tests. When done, remove this comment.
 * @todo #1:1hr Gist comments. Let's add new method comments() to this
 *  interface, returning an instance of interface GistComments. This new
 *  interface should implement methods do iterate, post, delete and read
 *  comments, as explained in
 *  http://developer.github.com/v3/gists/comments/. New interface should
 *  be implemented by GhGistComments class and tested with unit and
 *  integration tests.
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Gist extends JsonReadable {

    /**
     * Github we're in.
     * @return Github
     */
    @NotNull(message = "Github is never NULL")
    Github github();

    /**
     * Read file content.
     * @param name Name of it
     * @return File content
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/gists/#get-a-single-gist">Get a Single Gist</a>
     */
    @NotNull(message = "file content is never NULL")
    String read(@NotNull(message = "file name can't be NULL") String name)
        throws IOException;

    /**
     * Write file content.
     * @param name Name of it
     * @param content Content to write
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/gists/#edit-a-gist">Edit a Gist</a>
     */
    void write(
        @NotNull(message = "file name can't be NULL") String name,
        @NotNull(message = "file content can't be NULL") String content)
        throws IOException;

    /**
     * Star a gist.
     * @throws IOException If there is any I/O problem
     */
    void star() throws IOException;

    /**
     * Checks if Gist is starred.
     * @throws IOException If there is any I/O problem
     * @return True if gist is starred
     */
    boolean starred() throws IOException;

    /**
     * Smart Gist with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "gist")
    final class Smart implements Gist {
        /**
         * Encapsulated gist.
         */
        private final transient Gist gist;
        /**
         * Public ctor.
         * @param gst Gist
         */
        public Smart(final Gist gst) {
            this.gist = gst;
        }
        /**
         * Get a list of all file names in the gist.
         * @return File names
         * @throws IOException If there is any I/O problem
         */
        public Iterable<String> files() throws IOException {
            final JsonObject array = this.gist.json().getJsonObject("files");
            final Collection<String> files =
                new ArrayList<String>(array.size());
            for (final JsonValue value : array.values()) {
                files.add(JsonObject.class.cast(value).getString("filename"));
            }
            return files;
        }
        @Override
        public Github github() {
            return this.gist.github();
        }
        @Override
        public String read(final String name) throws IOException {
            return this.gist.read(name);
        }
        @Override
        public void write(final String name, final String content)
            throws IOException {
            this.gist.write(name, content);
        }

        @Override
        public void star() throws IOException {
            this.gist.star();
        }

        @Override
        public boolean starred() throws IOException {
            return this.gist.starred();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.gist.json();
        }
    }

}
