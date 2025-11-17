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
 * GitHub public key.
 *
 * <p>PublicKey implements {@link JsonReadable}, that's how you can get its full
 * details in JSON format. For example, to get its title,
 * you get the entire JSON and then gets its element:
 *
 * <pre>String title = key.json().getString("title");</pre>
 *
 * <p>However, it's better to use a supplementary "smart" decorator, which
 * automates most of these operations:
 *
 * <pre>String title = new PublicKey.Smart(comment).title();</pre>
 *
 * @see <a href="https://developer.github.com/v3/users/keys/">Public Keys API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface PublicKey extends JsonReadable, JsonPatchable {

    /**
     * User we're in.
     *
     * @return User
     */
    User user();

    /**
     * ID Number of this public key.
     * @return Public key ID number
     */
    int number();

    /**
     * Smart PublicKey with extra features.
     * @checkstyle MultipleStringLiterals (500 lines)
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "key", "jsn" })
    final class Smart implements PublicKey {

        /**
         * Encapsulated public key.
         */
        private final transient PublicKey key;

        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param pkey Public key
         */
        public Smart(
            final PublicKey pkey
        ) {
            this.key = pkey;
            this.jsn = new SmartJson(pkey);
        }

        /**
         * Get its key value.
         * @return Value of public key
         * @throws IOException If there is any I/O problem
         */
        public String key() throws IOException {
            return this.jsn.text("key");
        }

        /**
         * Change its value.
         * @param value Title of public key
         * @throws IOException If there is any I/O problem
         */
        public void key(
            final String value
        ) throws IOException {
            this.key.patch(
                Json.createObjectBuilder().add("key", value).build()
            );
        }

        /**
         * Get its URL.
         * @return URL of public key
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
         * @return Title of public key
         * @throws IOException If there is any I/O problem
         */
        public String title() throws IOException {
            return this.jsn.text("title");
        }

        /**
         * Change its title.
         * @param text Title of public key
         * @throws IOException If there is any I/O problem
         */
        public void title(
            final String text
        ) throws IOException {
            this.key.patch(
                Json.createObjectBuilder().add("title", text).build()
            );
        }

        @Override
        public JsonObject json() throws IOException {
            return this.key.json();
        }

        @Override
        public void patch(
            final JsonObject json
        ) throws IOException {
            this.key.patch(json);
        }

        @Override
        public User user() {
            return this.key.user();
        }

        @Override
        public int number() {
            return this.key.number();
        }
    }
}
