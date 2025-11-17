/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub Git blob.
 *
 * @see <a href="https://developer.github.com/v3/git/blobs/">Blobs API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
public interface Blob extends JsonReadable {
    /**
     * SHA of it.
     * @return SHA
     */
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

        @Override
        public JsonObject json() throws IOException {
            return this.blob.json();
        }

        @Override
        public String sha() {
            return this.blob.sha();
        }

        /**
         * Get its url.
         * @return Url of blob request
         * @throws IOException If there is any I/O problem
         */
        public String url() throws IOException {
            return this.jsn.text("url");
        }
    }
}
