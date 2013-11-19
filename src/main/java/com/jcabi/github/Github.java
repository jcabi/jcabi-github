/**
 * Copyright (c) 2012-2013, JCabi.com
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
import com.jcabi.manifests.Manifests;
import com.rexsl.test.ApacheRequest;
import com.rexsl.test.Request;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github client, starting point to the entire library.
 *
 * <p>This is how you start communicating with Github API:
 *
 * <pre> Github github = new Github.Simple(oauthKey);
 * Repo repo = github.repo("jcabi/jcabi-github");
 * Issues issues = repo.issues();
 * Issue issue = issues.post("issue title", "issue body");</pre>
 *
 * <p>It is strongly recommended to use </>
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
public interface Github {

    /**
     * Get repository by name.
     * @param name Repository name in "user/repo" format
     * @return Repository
     */
    @NotNull(message = "repository is never NULL")
    Repo repo(@NotNull String name);

    /**
     * Get Gists API entry point.
     * @return Gists API entry point
     */
    @NotNull(message = "gists is never NULL")
    Gists gists();

    /**
     * Get Users API entry point.
     * @return Users API entry point
     * @since 0.4
     */
    @NotNull(message = "users is never NULL")
    Users users();

    /**
     * Simple and default implementation, to be used in most cases.
     */
    @Immutable
    @Loggable(Loggable.DEBUG)
    @ToString
    @EqualsAndHashCode(of = "request")
    final class Simple implements Github {
        /**
         * Version of us.
         */
        private static final String USER_AGENT = String.format(
            "jcabi-github %s %s %s",
            Manifests.read("JCabi-Version"),
            Manifests.read("JCabi-Revision"),
            Manifests.read("JCabi-Date")
        );
        /**
         * Default request to start with.
         */
        private static final Request REQUEST =
            new ApacheRequest("https://api.github.com")
                .header(HttpHeaders.USER_AGENT, Github.Simple.USER_AGENT)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        /**
         * REST request.
         */
        private final transient Request request;
        /**
         * Public ctor, for anonymous access to Github.
         * @since 0.4
         */
        public Simple() {
            this(Github.Simple.REQUEST);
        }
        /**
         * Public ctor, for HTTP Basic Authentication.
         * @param user User name
         * @param pwd Password
         * @since 0.4
         */
        public Simple(
            @NotNull(message = "user name can't be NULL") final String user,
            @NotNull(message = "password can't be NULL") final String pwd) {
            this(
                Github.Simple.REQUEST.uri().userInfo(
                    String.format("%s:%s", user, pwd)
                ).back()
            );
        }
        /**
         * Public ctor, for authentication with OAuth2 token.
         * @param token OAuth token
         */
        public Simple(@NotNull(message = "token can't be NULL")
            final String token) {
            this(
                Github.Simple.REQUEST.header(
                    HttpHeaders.AUTHORIZATION,
                    String.format("token %s", token)
                )
            );
        }
        /**
         * Public ctor, with a custom request.
         * @param req Request to start from
         * @since 0.4
         */
        public Simple(@NotNull(message = "request can't be NULL")
            final Request req) {
            this.request = req;
        }
        @Override
        @NotNull(message = "repo is never NULL")
        public Repo repo(@NotNull(message = "repository name is never NULL")
            final String name) {
            return new GhRepo(this, this.request, new Coordinates.Simple(name));
        }
        @Override
        @NotNull(message = "gists are never NULL")
        public Gists gists() {
            return new GhGists(this, this.request);
        }
        @Override
        @NotNull(message = "users are never NULL")
        public Users users() {
            return new GhUsers(this, this.request);
        }
    }

}
