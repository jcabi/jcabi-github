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
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github label.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/labels/">Labels API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Label extends Comparable<Label>, JsonReadable, JsonPatchable {

    /**
     * The repo we're in.
     * @return Issue
     * @since 0.6
     */
    @NotNull(message = "repo is never NULL")
    Repo repo();

    /**
     * Name of it.
     * @return Name
     */
    @NotNull(message = "label name is never NULL")
    String name();

    /**
     * Smart Label with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "label")
    final class Smart implements Label {
        /**
         * Encapsulated label.
         */
        private final transient Label label;
        /**
         * Public ctor.
         * @param lbl Label
         */
        public Smart(final Label lbl) {
            this.label = lbl;
        }
        /**
         * Get its color.
         * @return Color of it
         * @throws IOException If there is any I/O problem
         */
        public String color() throws IOException {
            return new SmartJson(this).text("color");
        }
        /**
         * Set its color.
         * @param color Color to set
         * @throws IOException If there is any I/O problem
         */
        public void color(final String color) throws IOException {
            this.label.patch(
                Json.createObjectBuilder().add("color", color).build()
            );
        }
        @Override
        public Repo repo() {
            return this.label.repo();
        }
        @Override
        public String name() {
            return this.label.name();
        }
        @Override
        public int compareTo(final Label lbl) {
            return this.label.compareTo(lbl);
        }
        @Override
        public void patch(final JsonObject json) throws IOException {
            this.label.patch(json);
        }
        @Override
        public JsonObject json() throws IOException {
            return this.label.json();
        }
    }
    /**
     * Unmodified Label with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "repo", "obj" })
    final class Unmodified implements Label {
        /**
         * Encapsulated Repo.
         */
        private final transient Repo repo;
        /**
         * Encapsulated String.
         */
        private final transient String obj;
        /**
         * Public ctor.
         * @param rep Repo
         * @param object String
         */
        public Unmodified(final Repo rep, final String object) {
            this.repo = rep;
            this.obj = object;
        }
        @Override
        public Repo repo() {
            return this.repo;
        }
        @Override
        public String name() {
            return Json.createReader(new StringReader(this.obj))
                .readObject()
                .getString("name");
        }
        @Override
        public int compareTo(final Label label) {
            return this.name().compareTo(label.name());
        }
        @Override
        public void patch(final JsonObject json) throws IOException {
            throw new UnsupportedOperationException("#patch()");
        }
        @Override
        public JsonObject json() throws IOException {
            return Json.createReader(new StringReader(this.obj)).readObject();
        }
    }
}
