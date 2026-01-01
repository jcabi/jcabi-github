/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.RequestURI;
import com.jcabi.http.response.RestResponse;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import lombok.EqualsAndHashCode;

/**
 * Commits of a GitHub repository.
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "entry" })
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
final class RtRepoCommits implements RepoCommits {

    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request for the commits.
     */
    private final transient Request request;

    /**
     * RESTful request for the comparison.
     */
    private final transient Request comp;

    /**
     * Parent repository.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req Entry point of API
     * @param repo Repository
     */
    RtRepoCommits(final Request req, final Repo repo) {
        this.entry = req;
        this.owner = repo;
        final RequestURI rep = req.uri()
            .path("/repos")
            .path(repo.coordinates().user())
            .path(repo.coordinates().repo());
        this.request = rep
            .path("/commits")
            .back();
        this.comp = rep
            .path("/compare")
            .back();
    }

    @Override
    public Iterable<RepoCommit> iterate(
        final Map<String, String> params
    ) {
        return new RtPagination<>(
            this.request.uri().queryParams(params).back(),
            value -> this.get(value.getString("sha"))
        );
    }

    @Override
    public RepoCommit get(
        final String sha
    ) {
        return new RtRepoCommit(this.entry, this.owner, sha);
    }

    @Override
    public CommitsComparison compare(
        final String base,
        final String head) {
        return new RtCommitsComparison(this.entry, this.owner, base, head);
    }

    @Override
    public String diff(
        final String base,
        final String head)
        throws IOException {
        return this.comp.reset(HttpHeaders.ACCEPT)
            .header(HttpHeaders.ACCEPT, "application/vnd.github.v3.diff")
            .uri()
            .path(String.format("%s...%s", base, head))
            .back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .body();
    }

    @Override
    public String patch(
        final String base,
        final String head)
        throws IOException {
        return this.comp.reset(HttpHeaders.ACCEPT)
            .header(HttpHeaders.ACCEPT, "application/vnd.github.v3.patch")
            .uri()
            .path(String.format("%s...%s", base, head))
            .back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .body();
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
