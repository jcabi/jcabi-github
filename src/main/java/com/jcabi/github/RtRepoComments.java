/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * GitHub commit comments of a repository.
 *
 * @since 2.0
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner" })
final class RtRepoComments implements RepoComments {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request to the repository.
     */
    private final transient Request request;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     *
     * @param req Request
     * @param repo Repository
     */
    RtRepoComments(final Request req, final Repo repo) {
        this(
            req,
            repo,
            req.uri()
                .path("/repos")
                .path(repo.coordinates().user())
                .path(repo.coordinates().repo())
                .back()
        );
    }

    private RtRepoComments(final Request entry, final Repo owner, final Request request) {
        this.entry = entry;
        this.owner = owner;
        this.request = request;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public RepoComment get(final long number) {
        return new RtRepoComment(this.entry, this.owner, number);
    }

    @Override
    public Iterable<RepoComment> iterate() {
        return new RtPagination<>(
            this.request.uri().path("/comments").back(),
            object -> this.get(object.getJsonNumber("id").longValue())
        );
    }

    @Override
    public Iterable<RepoComment> iterate(final String sha) {
        return new RtPagination<>(
            this.commit(sha),
            object -> this.get(object.getJsonNumber("id").longValue())
        );
    }

    @Override
    public RepoComment post(final String sha, final String body)
        throws IOException {
        return this.post(
            sha,
            Json.createObjectBuilder().add("body", body).build()
        );
    }

    @Override
    public RepoComment post(
        final String sha,
        final String body,
        final String path,
        final int position
    ) throws IOException {
        return this.post(
            sha,
            Json.createObjectBuilder()
                .add("body", body)
                .add("path", path)
                .add("position", position)
                .build()
        );
    }

    private RepoComment post(final String sha, final JsonObject json)
        throws IOException {
        return this.get(
            this.commit(sha).method(Request.POST)
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject()
                .getJsonNumber("id").longValue()
        );
    }

    private Request commit(final String sha) {
        return this.request.uri()
            .path("/commits")
            .path(sha)
            .path("/comments")
            .back();
    }
}
