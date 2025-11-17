/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub pull request ref.
 *
 * @since 0.24
 * @see <a href="https://developer.github.com/v3/pulls/#get-a-single-pull-request">Get a single pull request API</a>
 *
 */
@Immutable
public interface PullRef extends JsonReadable {
    /**
     * Get the repository which its commit is in.
     * @return Repo
     */
    Repo repo();

    /**
     * Get its ref.
     * @return Ref
     * @throws IOException If there is any I/O problem
     */
    String ref() throws IOException;

    /**
     * Get its commit SHA.
     * @return Commit SHA
     * @throws IOException If there is any I/O problem
     */
    String sha() throws IOException;

    /**
     * Smart pull request ref with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "pullref", "jsn" })
    final class Smart implements PullRef {
        /**
         * Encapsulated pull request ref.
         */
        private final transient PullRef pullref;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param pref Pull request ref
         */
        public Smart(
            final PullRef pref
        ) {
            this.pullref = pref;
            this.jsn = new SmartJson(pref);
        }

        @Override
        public Repo repo() {
            return this.pullref.repo();
        }

        @Override
        public String ref() throws IOException {
            return this.pullref.ref();
        }

        @Override
        public String sha() throws IOException {
            return this.pullref.sha();
        }

        /**
         * Gets the user who owns the repository which its commit is in.
         * @return User
         * @throws IOException If there is any I/O problem
         */
        public User user() throws IOException {
            return this.pullref.repo().github().users().get(
                this.jsn.value("user", JsonObject.class).getString("login")
            );
        }

        /**
         * Get its label. Normally of the form "user:branch".
         * @return Label string
         * @throws IOException If there is any I/O problem
         */
        public String label() throws IOException {
            return this.jsn.text("label");
        }

        /**
         * Get its commit.
         * @return Commit
         * @throws IOException If there is any I/O problem
         */
        public Commit commit() throws IOException {
            return this.repo().git().commits().get(this.sha());
        }

        @Override
        public JsonObject json() throws IOException {
            return this.pullref.json();
        }
    }
}
