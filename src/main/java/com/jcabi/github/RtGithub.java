/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
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
    public Gitignores gitignores() {
        return new RtGitignores(this);
    }

    @Override
    public Markdown markdown() {
        return new RtMarkdown(this, this.request);
    }

}
