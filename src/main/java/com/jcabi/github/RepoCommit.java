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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github repo commit.
 *
 * <p>The repo commit exposes all available properties through its
 * {@code json()} method. However, it is recommended to use its
 * "smart" decorator, which helps you to get access to all JSON properties,
 * for example:
 *
 * <pre> URL url = new RepoCommit.Smart(commit).url();</pre>
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/repos/commits/">Commits API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface RepoCommit extends Comparable<RepoCommit>, JsonReadable {

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
    final class Smart implements RepoCommit {
        /**
         * Encapsulated repo commit.
         */
        private final transient RepoCommit commit;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param cmt RepoCommit
         */
        public Smart(
            final RepoCommit cmt
        ) {
            this.commit = cmt;
            this.jsn = new SmartJson(cmt);
        }
        /**
         * Get its message.
         * @return Message of repo commit
         * @throws IOException If there is any I/O problem
         */
        public String message() throws IOException {
            return this.jsn.json()
                .getJsonObject("commit")
                .getString("message");
        }
        /**
         * Get its URL.
         * @return URL of repo commit
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
         * Returns the login of the author.
         * @return The login
         * @throws IOException If there is any I/O problem
         * @since 1.1
         */
        public String author() throws IOException {
            return this.jsn.json()
                .getJsonObject("commit")
                .getJsonObject("author")
                .getString("name");
        }
        /**
         * Returns TRUE if the commit is verified.
         * @return TRUE if verified
         * @throws IOException If there is any I/O problem
         * @since 1.1
         */
        public boolean isVerified() throws IOException {
            return this.jsn.json()
                .getJsonObject("commit")
                .getJsonObject("verification")
                .getBoolean("verified");
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
        public int compareTo(
            final RepoCommit obj
        ) {
            return this.commit.compareTo(obj);
        }
    }

}
