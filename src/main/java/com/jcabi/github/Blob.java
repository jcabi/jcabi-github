/**
 * Copyright (c) 2013-2014, JCabi.com
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
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github Git blob.
 *
 * @author Alexander Lukashevich (sanai56967@gmail.com)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/git/blobs/">Blobs API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
public interface Blob extends JsonReadable, JsonPatchable {
    /**
     * SHA of it.
     * @return SHA
     */
    @NotNull(message = "commit SHA is never NULL")
    String sha();
    /**
     * Smart Blob with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "blob", "jsn" })
    final class Smart implements Blob {
        /**
         * Encapsulated blob.
         */
        private final transient Blob blob;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param blb Blob
         */
        public Smart(final Blob blb) {
            this.blob = blb;
            this.jsn = new SmartJson(blb);
        }

        /**
         * Get its state.
         * @return State of blob request
         * @throws IOException If there is any I/O problem
         */
        public String state() throws IOException {
            return this.jsn.text("state");
        }
        /**
         * Change its state.
         * @param state State of blob request
         * @throws IOException If there is any I/O problem
         */
        public void state(final String state) throws IOException {
            this.blob.patch(
                Json.createObjectBuilder().add("state", state).build()
            );
        }
        /**
         * Get its body.
         * @return Body of blob request
         * @throws IOException If there is any I/O problem
         */
        public String url() throws IOException {
            return this.jsn.text("url");
        }

        /**
         * Get its title.
         * @return Title of blob request
         * @throws IOException If there is any I/O problem
         */
        public String body() throws IOException {
            return this.jsn.text("body");
        }
        /**
         * Change its body.
         * @param text Body of blob request
         * @throws IOException If there is any I/O problem
         */
        public void body(final String text) throws IOException {
            this.blob.patch(
                Json.createObjectBuilder().add("body", text).build()
            );
        }

        @Override
        public JsonObject json() throws IOException {
            return this.blob.json();
        }
        @Override
        public void patch(final JsonObject json) throws IOException {
            this.blob.patch(json);
        }

        @Override
        public String sha() {
            return this.blob.sha();
        }
    }
}
