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
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github content.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/contents/">Contents API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Content extends Comparable<Content>,
    JsonReadable, JsonPatchable {

    /**
     * Repository we're in.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Get its path name.
     * @return The path name
     */
    @NotNull(message = "path is never NULL")
    String path();

    /**
     * Get the raw contents.
     * @throws IOException If an IO error occurs
     * @return Input stream of the raw content
     */
    @NotNull(message = "raw is never NULL")
    InputStream raw() throws IOException;

    /**
     * Smart Content with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "content", "jsn" })
    final class Smart implements Content {
        /**
         * Encapsulated content.
         */
        private final transient Content content;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param cont Content
         */
        public Smart(
            @NotNull(message = "content is never NULL")
            final Content cont) {
            this.content = cont;
            this.jsn = new SmartJson(cont);
        }
        /**
         * Get its name.
         * @return Name of content
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "name is never NULL")
        public String name() throws IOException {
            return this.jsn.text("name");
        }
        /**
         * Get its type.
         * @return Type of content
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "type is never NULL")
        public String type() throws IOException {
            return this.jsn.text("type");
        }
        /**
         * Get its size.
         * @return Size content
         * @throws IOException If it fails
         */
        public int size() throws IOException {
            return this.jsn.number("size");
        }
        /**
         * Get its sha hash.
         * @return Sha hash of content
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "sha is never NULL")
        public String sha() throws IOException {
            return this.jsn.text("sha");
        }
        /**
         * Get its URL.
         * @return URL of content
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "url is never NULL")
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }
        /**
         * Get its HTML URL.
         * @return URL of content
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "html url is never NULL")
        public URL htmlUrl() throws IOException {
            return new URL(this.jsn.text("html_url"));
        }
        /**
         * Get its GIT URL.
         * @return URL of content
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "git url is never NULL")
        public URL gitUrl() throws IOException {
            return new URL(this.jsn.text("git_url"));
        }
        /**
         * Get its encoded content.
         * @return Base64 encoded content
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "content is never NULL")
        public String content() throws IOException {
            return this.jsn.text("content");
        }
        @Override
        public int compareTo(
            @NotNull(message = "cont can't be NULL") final Content cont
        ) {
            return this.content.compareTo(cont);
        }
        @Override
        public void patch(
            @NotNull(message = "JSON is never NULL")final JsonObject json)
            throws IOException {
            this.content.patch(json);
        }
        @Override
        @NotNull(message = "JSON is never NULL")
        public JsonObject json() throws IOException {
            return this.content.json();
        }
        @Override
        @NotNull(message = "Repository is never NULL")
        public Repo repo() {
            return this.content.repo();
        }
        @Override
        @NotNull(message = "path is never NULL")
        public String path() {
            return this.content.path();
        }
        @Override
        @NotNull(message = "input stream is never NULL")
        public InputStream raw() throws IOException {
            return this.content.raw();
        }
    }
}
