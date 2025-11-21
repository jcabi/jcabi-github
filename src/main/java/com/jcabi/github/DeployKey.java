/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub deploy key.
 * @see <a href="https://developer.github.com/v3/repos/keys/">Deploy Keys API</a>
 * @since 0.8
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
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/keys/#delete">Remove a deploy key</a>
     */
    void remove() throws IOException;

    /**
     * Smart DeployKey with extra features.
     * @since 0.8
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
        public Smart(final DeployKey dkey) {
            this.key = dkey;
            this.jsn = new SmartJson(dkey);
        }

        /**
         * Get its key value.
         * @return Value of deploy key
         * @throws IOException If there is any I/O problem
         */
        public String key() throws IOException {
            return this.jsn.text("key");
        }

        /**
         * Change its value.
         * @param value Title of deploy key
         * @throws IOException If there is any I/O problem
         */
        public void key(final String value) throws IOException {
            this.key.patch(
                Json.createObjectBuilder().add("key", value).build()
            );
        }

        /**
         * Get its URL.
         * @return URL of deploy key
         * @throws IOException If there is any I/O problem
         */
        public URL url() throws IOException {
            try {
                return new URI(this.jsn.text("url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get its title.
         * @return Title of deploy key
         * @throws IOException If there is any I/O problem
         */
        public String title() throws IOException {
            return this.jsn.text("title");
        }

        /**
         * Change its title.
         * @param text Title of deploy key
         * @throws IOException If there is any I/O problem
         */
        public void title(final String text) throws IOException {
            this.key.patch(
                Json.createObjectBuilder().add("title", text).build()
            );
        }

        @Override
        public JsonObject json() throws IOException {
            return this.key.json();
        }

        @Override
        public void patch(final JsonObject json) throws IOException {
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
