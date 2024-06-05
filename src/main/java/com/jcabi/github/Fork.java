/**
 * Copyright (c) 2013-2023, jcabi.com
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
 * Github fork.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/repos/forks/">Forks API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Fork extends JsonReadable, JsonPatchable {
    /**
     * Fork id.
     * @return Id
     */
    int number();

    /**
     * Smart Fork with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "fork", "jsn" })
    final class Smart implements Fork {
        /**
         * Encapsulated Fork.
         */
        private final transient Fork fork;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param frk Fork
         */
        public Smart(final Fork frk) {
            this.fork = frk;
            this.jsn = new SmartJson(frk);
        }

        /**
         * Get its name.
         * @return Name of fork
         * @throws java.io.IOException If there is any I/O problem
         */
        public String name() throws IOException {
            return this.jsn.text("name");
        }

        /**
         * Get its organization.
         * @return Organization
         * @throws java.io.IOException If there is any I/O problem
         */
        public String organization() throws IOException {
            return this.jsn.text("organization");
        }

        /**
         * Get its URL.
         * @return URL of fork
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
         * Get its full name.
         * @return Full name of fork
         * @throws java.io.IOException If there is any I/O problem
         */
        public String fullName() throws IOException {
            return this.jsn.text("full_name");
        }

        /**
         * Get its description.
         * @return Description of fork
         * @throws java.io.IOException If there is any I/O problem
         */
        public String description() throws IOException {
            return this.jsn.text("description");
        }

        /**
         * Get its html url.
         * @return Html url of fork
         * @throws IOException If there is any I/O problem
         */
        public URL htmlUrl() throws IOException {
            try {
                return new URI(this.jsn.text("html_url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get its clone url.
         * @return Clone url of fork
         * @throws IOException If there is any I/O problem
         */
        public URL cloneUrl() throws IOException {
            try {
                return new URI(this.jsn.text("clone_url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get its git url.
         * @return Git url of fork
         * @throws IOException If there is any I/O problem
         */
        public String gitUrl() throws IOException {
            return this.jsn.text("git_url");
        }

        /**
         * Get its ssh url.
         * @return Ssh url of fork
         * @throws IOException If there is any I/O problem
         */
        public String sshUrl() throws IOException {
            return this.jsn.text("ssh_url");
        }

        /**
         * Get its svn url.
         * @return Svn url of fork
         * @throws IOException If there is any I/O problem
         */
        public URL svnUrl() throws IOException {
            try {
                return new URI(this.jsn.text("svn_url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get its mirror url.
         * @return Mirror url of fork
         * @throws IOException If there is any I/O problem
         */
        public String mirrorUrl() throws IOException {
            return this.jsn.text("mirror_url");
        }

        /**
         * Get its home page.
         * @return Url of home page of fork
         * @throws IOException If there is any I/O problem
         */
        public URL homeUrl() throws IOException {
            try {
                return new URI(this.jsn.text("homepage")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get its forks count.
         * @return Forks count of fork
         * @throws IOException If there is any I/O problem
         */
        public int forks() throws IOException {
            return this.jsn.number("forks_count");
        }

        /**
         * Get its stargazers count.
         * @return Stargazers count of fork
         * @throws IOException If there is any I/O problem
         */
        public int stargazers() throws IOException {
            return this.jsn.number("stargazers_count");
        }

        /**
         * Get its watchers count.
         * @return Watchers count of fork
         * @throws IOException If there is any I/O problem
         */
        public int watchers() throws IOException {
            return this.jsn.number("watchers_count");
        }

        /**
         * Get its size.
         * @return Size of fork
         * @throws IOException If there is any I/O problem
         */
        public int size() throws IOException {
            return this.jsn.number("size");
        }

        /**
         * Get its default branch.
         * @return Default branch
         * @throws java.io.IOException If there is any I/O problem
         */
        public String defaultBranch() throws IOException {
            return this.jsn.text("default_branch");
        }

        /**
         * Get its open issues count.
         * @return Size of fork
         * @throws IOException If there is any I/O problem
         */
        public int openIssues() throws IOException {
            return this.jsn.number("open_issues_count");
        }

        @Override
        public int number() {
            return this.fork.number();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.fork.json();
        }

        @Override
        public void patch(final JsonObject json) throws IOException {
            this.fork.patch(json);
        }
    }
}
