/**
 * Copyright (c) 2013-2014, jcabi.com
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
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
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
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.3
 * @see <a href="http://developer.github.com/v3/pulls/">Pull Request API</a>
 * @see <a href="http://developer.github.com/v3/git/commits/">Commits API</a>
 */
@Immutable
public interface Commit extends Comparable<Commit>, JsonReadable {

    /**
     * The repo we're in.
     * @return Repo
     */
    @NotNull(message = "repo is never NULL")
    Repo repo();

    /**
     * SHA of it.
     * @return SHA
     */
    @NotNull(message = "commit SHA is never NULL")
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
        public Smart(
            @NotNull(message = "cmt can't be NULL") final Commit cmt
        ) {
            this.commit = cmt;
            this.jsn = new SmartJson(cmt);
        }
        /**
         * Get its message.
         * @return Message of commit
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "message is never NULL")
        public String message() throws IOException {
            return this.jsn.json().getJsonObject("commit").getString("message");
        }
        /**
         * Get its URL.
         * @return URL of comment
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "URL is never NULL")
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }
        @Override
        @NotNull(message = "repository is never NULL")
        public Repo repo() {
            return this.commit.repo();
        }
        @Override
        @NotNull(message = "sha is never NULL")
        public String sha() {
            return this.commit.sha();
        }
        @Override
        @NotNull(message = "JSON is never NULL")
        public JsonObject json() throws IOException {
            return this.commit.json();
        }
        @Override
        public int compareTo(
            @NotNull(message = "obj can't be NULL") final Commit obj
        ) {
            return this.commit.compareTo(obj);
        }
    }

}
