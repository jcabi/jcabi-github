/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github commit.
 *
 * <p>The commit exposes all available properties through its
 * {@code json()} method. However, it is recommended to use its
 * "smart" decorator, which helps you to get access to all JSON properties,
 * for example:
 *
 * <pre> URL url = new Commit.Smart(commit).url();</pre>
 *
 * @since 0.3
 * @see <a href="https://developer.github.com/v3/pulls/">Pull Request API</a>
 * @see <a href="https://developer.github.com/v3/git/commits/">Commits API</a>
 */
@Immutable
public interface Commit extends Comparable<Commit>, JsonReadable {

    /**
     * The repo we're in.
     * @return Repo
     */
    Repo repo();

    /**
     * SHA of it.
     * @return SHA
     */
    String sha();

    /**
     * Smart commit.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "commit", "jsn" })
    final class Smart implements Commit {
        /**
         * Encapsulated commit.
         */
        private final transient Commit commit;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param cmt Commit
         */
        public Smart(final Commit cmt) {
            this.commit = cmt;
            this.jsn = new SmartJson(cmt);
        }
        /**
         * Get its message.
         * @return Message of commit
         * @throws IOException If there is any I/O problem
         */
        public String message() throws IOException {
            return this.jsn.json().getJsonObject("commit").getString("message");
        }
        /**
         * Get its URL.
         * @return URL of comment
         * @throws IOException If there is any I/O problem
         */
        public URL url() throws IOException {
            try {
                return new URI(this.jsn.text("url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        @Override
        public Repo repo() {
            return this.commit.repo();
        }
        @Override
        public String sha() {
            return this.commit.sha();
        }
        @Override
        public JsonObject json() throws IOException {
            return this.commit.json();
        }
        @Override
        public int compareTo(final Commit obj) {
            return this.commit.compareTo(obj);
        }
    }

}
