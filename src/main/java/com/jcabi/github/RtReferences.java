/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
 * GitHub references.
 * @since 0.24
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"entry", "request", "owner" })
final class RtReferences implements References {

    /**
     * Reference field name in JSON.
     */
    private static final String REF = "ref";

    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request for the commits.
     */
    private final transient Request request;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * Public constructor.
     * @param req RESTful request.
     * @param repo The owner repo.
     */
    RtReferences(final Request req, final Repo repo) {
        this.entry = req;
        this.owner = repo;
        this.request = req.uri().path("/repos").path(repo.coordinates().user())
            .path(repo.coordinates().repo()).path("/git").path("/refs").back();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Reference create(
        final String ref,
        final String sha
    ) throws IOException {
        final JsonObject json = Json.createObjectBuilder()
            .add("sha", sha).add(RtReferences.REF, ref).build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getString(RtReferences.REF)
        );
    }

    @Override
    public Reference get(
        final String identifier
    ) {
        return new RtReference(this.entry, this.owner, identifier);
    }

    @Override
    public Iterable<Reference> iterate() {
        return new RtPagination<>(
            this.request,
            object -> this.get(
                object.getString(RtReferences.REF)
            )
        );
    }

    @Override
    public Iterable<Reference> iterate(
        final String subnamespace
    ) {
        return new RtPagination<>(
            this.request.uri().path(subnamespace).back(),
            object -> this.get(
                object.getString(RtReferences.REF)
            )
        );
    }

    @Override
    public Iterable<Reference> tags() {
        return this.iterate("tags");
    }

    @Override
    public Iterable<Reference> heads() {
        return this.iterate("heads");
    }

    @Override
    public void remove(
        final String identifier
    ) throws IOException {
        this.request.method(Request.DELETE)
            .uri().path(identifier).back().fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

}
