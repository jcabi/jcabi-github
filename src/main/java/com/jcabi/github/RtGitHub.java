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
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub client, starting point to the entire library.
 *
 * <p>This is how you start communicating with GitHub API:
 *
 * <pre> GitHub github = new RtGitHub(oauthKey);
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
 * <pre> GitHub github = new RtGitHub(
 *   new RtGitHub(oauthKey).entry().through(RetryWire.class)
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
public final class RtGitHub implements GitHub {

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
     * Public ctor, for anonymous access to GitHub.
     * @since 0.4
     */
    public RtGitHub() {
        this(RtGitHub.REQUEST);
    }

    /**
     * Public ctor, for anonymous access to GitHub.<br><br>
     *
     * Use this ctor when you want to access GitHub's API over a
     * custom domain, other than https//api.github.com.<br><br>
     *
     * For instance, if you have your own instance of GitHub deployed
     * somewhere.
     *
     * <pre>
     *     final GitHub myGitHub = new RtGitHub(
     *         URI.create("https://github.mydomain.com")
     *     );
     * </pre>
     * @param domain Your domain.
     */
    public RtGitHub(final URI domain) {
        this(RtGitHub.REQUEST.uri().set(domain).back());
    }

    /**
     * Public ctor, for HTTP Basic Authentication.
     * @param user User name
     * @param pwd Password
     * @since 0.4
     */
    public RtGitHub(final String user, final String pwd) {
        this(
            RtGitHub.REQUEST.header(
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
     * Use this ctor when you want to access GitHub's API over a
     * custom domain, other than https//api.github.com.<br><br>
     *
     * For instance, if you have your own instance of GitHub deployed
     * somewhere.
     *
     * <pre>
     *     final GitHub myGitHub = new RtGitHub(
     *         "john_doe", "johnspassword",
     *         URI.create("https://github.mydomain.com")
     *     );
     * </pre>
     * @param user User's username.
     * @param pwd User's password.
     * @param domain Your custom domain.
     */
    public RtGitHub(final String user, final String pwd, final URI domain) {
        this(
            RtGitHub.REQUEST.uri().set(domain).back()
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
     * Use this ctor when you want to access GitHub's API over a
     * custom domain, other than https//api.github.com.<br><br>
     *
     * For instance, if you have your own instance of GitHub deployed
     * somewhere.
     *
     * <pre>
     *     final GitHub myGitHub = new RtGitHub(
     *         "john_doe", "johnspassword",
     *         URI.create("https://github.mydomain.com")
     *     );
     * </pre>
     *
     * @param token OAuth token
     * @param domain Your custom domain.
     */
    public RtGitHub(final String token, final URI domain) {
        this(
            RtGitHub.REQUEST.uri().set(domain).back()
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
    public RtGitHub(final String token) {
        this(
            RtGitHub.REQUEST.header(
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
    public RtGitHub(final Request req) {
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
