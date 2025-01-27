/**
 * Copyright (c) 2013-2025, jcabi.com
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
import javax.json.JsonObject;
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
