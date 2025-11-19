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
import jakarta.json.JsonStructure;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * GitHub labels of a repo.
 * @since 0.6
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "owner" })
final class RtLabels implements Labels {

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * GitHub.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repo we're in
     */
    RtLabels(final Request req, final Repo repo) {
        this.owner = repo;
        final Coordinates coords = repo.coordinates();
        this.entry = req;
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/labels")
            .back();
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Label create(
        final String name,
        final String color)
        throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (1 line)
            .add("name", name)
            .add("color", color)
            .build();
        this.request.method(Request.POST)
            .body().set(json).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_CREATED)
            .as(JsonResponse.class)
            .json();
        return new RtLabel(this.entry, this.owner, name);
    }

    @Override
    public Label get(final String name) {
        return new RtLabel(this.entry, this.owner, name);
    }

    @Override
    public void delete(final String name) throws IOException {
        this.request.method(Request.DELETE)
            .uri().path(name).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Iterable<Label> iterate() {
        return new RtPagination<>(
            this.request,
            object -> new RtLabel(
                this.entry,
                this.owner,
                object.getString("name")
            )
        );
    }

}
