/**
 * Copyright (c) 2013-2015, jcabi.com
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
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github deploy key.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/keys/">Deploy Keys API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface DeployKey extends JsonReadable, JsonPatchable {

    /**
     * Get id of a deploy key.
     * @return Id
     */
    int number();

    /**
     * Delete a deploy key.
     * @throws java.io.IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/keys/#delete">Remove a deploy key</a>
     */
    void remove() throws IOException;

    /**
     * Smart DeployKey with extra features.
     * @checkstyle MultipleStringLiterals (500 lines)
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "key", "jsn" })
    final class Smart implements DeployKey {

        /**
         * Encapsulated deploy key.
         */
        private final transient DeployKey key;

        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param dkey Deploy key
         */
        public Smart(
            @NotNull(message = "deploy key is never NULL") final DeployKey dkey
        ) {
            this.key = dkey;
            this.jsn = new SmartJson(dkey);
        }

        /**
         * Get its key value.
         * @return Value of deploy key
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "key is never NULL")
        public String key() throws IOException {
            return this.jsn.text("key");
        }

        /**
         * Change its value.
         * @param value Title of deploy key
         * @throws IOException If there is any I/O problem
         */
        public void key(
            @NotNull(message = "value can't be NULL") final String value
        ) throws IOException {
            this.key.patch(
                Json.createObjectBuilder().add("key", value).build()
            );
        }

        /**
         * Get its URL.
         * @return URL of deploy key
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "URL can't be NULL")
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }

        /**
         * Get its title.
         * @return Title of deploy key
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "title is never NULL")
        public String title() throws IOException {
            return this.jsn.text("title");
        }

        /**
         * Change its title.
         * @param text Title of deploy key
         * @throws IOException If there is any I/O problem
         */
        public void title(
            @NotNull(message = "text can't be NULL") final String text
        ) throws IOException {
            this.key.patch(
                Json.createObjectBuilder().add("title", text).build()
            );
        }

        @Override
        @NotNull(message = "JSON is never NULL")
        public JsonObject json() throws IOException {
            return this.key.json();
        }

        @Override
        public void patch(
            @NotNull(message = "JSON is never NULL") final JsonObject json
        ) throws IOException {
            this.key.patch(json);
        }

        @Override
        public int number() {
            return this.key.number();
        }

        @Override
        public void remove() throws IOException {
            this.key.remove();
        }
    }

}
