/**
 * Copyright (c) 2013-2015, jcabi.com
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
import com.jcabi.http.Request;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.net.URI;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.io.Charsets;

/**
 * Github client, starting point to the entire library.
 *
 * <p>This is how you start communicating with Github API:
 *
 * <pre> Github github = new RtGithub(oauthKey);
 * Repo repo = github.repos().get(
 *     new Coordinates.Simple("jcabi/jcabi-github")
 * );
 * Issues issues = repo.issues();
 * Issue issue = issues.create("issue title", "issue body");
 * issue.comments().post("issue comment");</pre>
 *
 * <p>It is strongly recommended to use
 * {@link com.jcabi.http.wire.RetryWire} to avoid
 * accidental I/O exceptions:
 *
 * <pre> Github github = new RtGithub(
 *   new RtGithub(oauthKey).entry().through(RetryWire.class)
 * );</pre>
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "request")
@SuppressWarnings("PMD.TooManyMethods")
public final class RtGithub implements Github {

    /**
     * Version of us.
     */
    private static final String USER_AGENT = String.format(
        "jcabi-github %s %s %s",
        Manifests.read("JCabi-Version"),
        Manifests.read("JCabi-Build"),
        Manifests.read("JCabi-Date")
    );

    public static final String DEFAULT_GITHUB_HOST = "https://api.github.com";

    /**
     * Create ApacheRequest used to talk to Github server.
     */
    private static Request getRequest(String uri) {
        return new ApacheRequest(uri)
            .header(HttpHeaders.USER_AGENT, RtGithub.USER_AGENT)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }

    /**
     * REST request.
     */
    private final transient Request request;

    /**
     * Public ctor, for anonymous access to Github.
     * @since 0.4
     */
    public RtGithub() {
        this(getRequest(DEFAULT_GITHUB_HOST));
    }

    /**
     * Public ctor, for HTTP Basic Authentication.
     * @param user User name
     * @param pwd Password
     * @since 0.4
     */
    public RtGithub(
        @NotNull(message = "user name can't be NULL") final String user,
        @NotNull(message = "password can't be NULL") final String pwd) {
        this(URI.create(DEFAULT_GITHUB_HOST), user, pwd);
    }

    /**
     * Public ctor, for authentication with OAuth2 token.
     * @param token OAuth token
     */
    public RtGithub(
        @NotNull(message = "token can't be NULL") final String token) {
        this(URI.create(DEFAULT_GITHUB_HOST), token);
    }

    /**
     * Public ctor, for anonymous access to Github Enterprise.
     * @param uri URI of Github server
     */
    public RtGithub(
        @NotNull(message = "uri can't be NULL") final URI uri) {
        this(getRequest(uri.toString()));
    }

    /**
     * Public ctor, for HTTP Basic Authentication.
     * @param uri URI of Github server
     * @param user User name
     * @param pwd Password
     */
    public RtGithub(
        @NotNull(message = "uri can't be NULL") final URI uri,
        @NotNull(message = "user name can't be NULL") final String user,
        @NotNull(message = "password can't be NULL") final String pwd) {
        this(
            getRequest(uri.toString()).header(
                HttpHeaders.AUTHORIZATION,
                String.format(
                    "Basic %s",
                    DatatypeConverter.printBase64Binary(
                        String.format("%s:%s", user, pwd)
                            .getBytes(Charsets.UTF_8)
                    )
                )
            )
        );
    }

    /**
     * Public ctor, for authentication with OAuth2 token.
     * @param uri URI of Github server
     * @param token OAuth token
     */
    public RtGithub(
        @NotNull(message = "uri can't be NULL") final URI uri,
        @NotNull(message = "token can't be NULL") final String token) {
        this(
            getRequest(uri.toString()).header(
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
    public RtGithub(
        @NotNull(message = "request can't be NULL") final Request req) {
        this.request = req;
    }

    @Override
    @NotNull(message = "request can't be NULL")
    public Request entry() {
        return this.request;
    }

    @Override
    @NotNull(message = "repos is never NULL")
    public Repos repos() {
        return new RtRepos(this, this.request);
    }

    @Override
    @NotNull(message = "gists are never NULL")
    public Gists gists() {
        return new RtGists(this, this.request);
    }

    @Override
    @NotNull(message = "users are never NULL")
    public Users users() {
        return new RtUsers(this, this.request);
    }

    @Override
    @NotNull(message = "organizations is never NULL")
    public Organizations organizations() {
        return new RtOrganizations(this, this.request);
    }

    @Override
    @NotNull(message = "limmits can't be NULL")
    public Limits limits() {
        return new RtLimits(this, this.request);
    }

    @Override
    @NotNull(message = "search is never NULL")
    public Search search() {
        return new RtSearch(this, this.request);
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject meta() throws IOException {
        return this.request.uri().path("meta").back().fetch()
            .as(JsonResponse.class)
            .json().readObject();
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject emojis() throws IOException {
        return this.request.uri().path("emojis").back().fetch()
            .as(JsonResponse.class)
            .json().readObject();
    }

    @Override
    @NotNull(message = "GitIgnores is never NULL")
    public Gitignores gitignores() throws IOException {
        return new RtGitignores(this);
    }

    @Override
    @NotNull(message = "Markdown is never NULL")
    public Markdown markdown() {
        return new RtMarkdown(this, this.request);
    }

}
