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
 */
@Immutable
public interface Gist {

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
     * @throws IOException If fails
     * @see <a href="http://developer.github.com/v3/gists/#get-a-single-gist">Get a Single Gist</a>
     */
    @NotNull(message = "file content is never NULL")
    String read(@NotNull(message = "file name can't be NULL") String name)
        throws IOException;

    /**
     * Write file content.
     * @param name Name of it
     * @param content Content to write
     * @throws IOException If fails
     * @see <a href="http://developer.github.com/v3/gists/#edit-a-gist">Edit a Gist</a>
     */
    void write(
        @NotNull(message = "file name can't be NULL") String name,
        @NotNull(message = "file content can't be NULL") String content)
        throws IOException;

    /**
     * Describe it in a JSON object.
     * @return JSON object
     * @throws IOException If fails
     * @see <a href="http://developer.github.com/v3/gists/#get-a-single-gist">Get a Single Gist</a>
     */
    @NotNull(message = "JSON is never NULL")
    JsonObject json() throws IOException;

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
         * @throws IOException If fails
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
        public JsonObject json() throws IOException {
            return this.gist.json();
        }
    }

}
