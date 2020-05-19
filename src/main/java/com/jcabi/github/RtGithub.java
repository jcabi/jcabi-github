/**
 * Copyright (c) 2013-2020, jcabi.com
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
import com.jcabi.http.wire.AutoRedirectingWire;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.json.JsonObject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = "request")
@SuppressWarnings("PMD.TooManyMethods")
public final class RtGithub implements Github {

    /**
     * Default request to start with.
     */
    private static final Request REQUEST =
        new ApacheRequest("https://api.github.com")
            .header(
                HttpHeaders.USER_AGENT,
                new FromProperties("jcabigithub.properties").format()
            )
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .through(AutoRedirectingWire.class);

    /**
     * REST request.
     */
    private final transient Request request;

    /**
     * Public ctor, for anonymous access to Github.
     * @since 0.4
     */
    public RtGithub() {
        this(RtGithub.REQUEST);
    }

    /**
     * Public ctor, for anonymous access to Github.<br><br>
     *
     * Use this ctor when you want to access Github's API over a
     * custom domain, other than https//api.github.com.<br><br>
     *
     * For instance, if you have your own instance of Github deployed
     * somewhere.
     *
     * <pre>
     *     final Github myGithub = new RtGithub(
     *         URI.create("https://github.mydomain.com")
     *     );
     * </pre>
     * @param domain Your domain.
     */
    public RtGithub(final URI domain) {
        this(RtGithub.REQUEST.uri().set(domain).back());
    }

    /**
     * Public ctor, for HTTP Basic Authentication.
     * @param user User name
     * @param pwd Password
     * @since 0.4
     */
    public RtGithub(final String user, final String pwd) {
        this(
            RtGithub.REQUEST.header(
                HttpHeaders.AUTHORIZATION,
                String.format(
                    "Basic %s",
                    DatatypeConverter.printBase64Binary(
                        String.format("%s:%s", user, pwd)
                            .getBytes(StandardCharsets.UTF_8)
                    )
                )
            )
        );
    }

    /**
     * Public ctor, for HTTP Basic Authentication.
     *
     * Use this ctor when you want to access Github's API over a
     * custom domain, other than https//api.github.com.<br><br>
     *
     * For instance, if you have your own instance of Github deployed
     * somewhere.
     *
     * <pre>
     *     final Github myGithub = new RtGithub(
     *         "john_doe", "johnspassword",
     *         URI.create("https://github.mydomain.com")
     *     );
     * </pre>
     * @param user User's username.
     * @param pwd User's password.
     * @param domain Your custom domain.
     */
    public RtGithub(final String user, final String pwd, final URI domain) {
        this(
            RtGithub.REQUEST.uri().set(domain).back()
                .header(
                    HttpHeaders.AUTHORIZATION,
                    String.format(
                        "Basic %s",
                        DatatypeConverter.printBase64Binary(
                            String.format("%s:%s", user, pwd)
                                .getBytes(StandardCharsets.UTF_8)
                        )
                    )
                )
        );
    }

    /**
     * Public ctor, for authentication with OAuth2 token.
     *
     * Use this ctor when you want to access Github's API over a
     * custom domain, other than https//api.github.com.<br><br>
     *
     * For instance, if you have your own instance of Github deployed
     * somewhere.
     *
     * <pre>
     *     final Github myGithub = new RtGithub(
     *         "john_doe", "johnspassword",
     *         URI.create("https://github.mydomain.com")
     *     );
     * </pre>
     *
     * @param token OAuth token
     * @param domain Your custom domain.
     */
    public RtGithub(final String token, final URI domain) {
        this(
            RtGithub.REQUEST.uri().set(domain).back()
                .header(
                    HttpHeaders.AUTHORIZATION,
                    String.format("token %s", token)
                )
        );
    }

    /**
     * Public ctor, for authentication with OAuth2 token.
     * @param token OAuth token
     */
    public RtGithub(final String token) {
        this(
            RtGithub.REQUEST.header(
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
    public RtGithub(final Request req) {
        this.request = req;
    }

    @Override
    public Request entry() {
        return this.request;
    }

    @Override
    public Repos repos() {
        return new RtRepos(this, this.request);
    }

    @Override
    public Gists gists() {
        return new RtGists(this, this.request);
    }

    @Override
    public Users users() {
        return new RtUsers(this, this.request);
    }

    @Override
    public Organizations organizations() {
        return new RtOrganizations(this, this.request);
    }

    @Override
    public Limits limits() {
        return new RtLimits(this, this.request);
    }

    @Override
    public Search search() {
        return new RtSearch(this, this.request);
    }

    @Override
    public JsonObject meta() throws IOException {
        return this.request.uri().path("meta").back().fetch()
            .as(JsonResponse.class)
            .json().readObject();
    }

    @Override
    public JsonObject emojis() throws IOException {
        return this.request.uri().path("emojis").back().fetch()
            .as(JsonResponse.class)
            .json().readObject();
    }

    @Override
    public Gitignores gitignores() throws IOException {
        return new RtGitignores(this);
    }

    @Override
    public Markdown markdown() {
        return new RtMarkdown(this, this.request);
    }

}
