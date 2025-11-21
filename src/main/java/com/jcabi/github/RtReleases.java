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
 * GitHub releases.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "request")
final class RtReleases implements Releases {

    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful API releases request.
     */
    private final transient Request request;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req RESTful API entry point
     * @param repo Repository
     */
    RtReleases(final Request req, final Repo repo) {
        this.entry = req;
        this.owner = repo;
        this.request = this.entry.uri()
            .path("/repos")
            .path(repo.coordinates().user())
            .path(repo.coordinates().repo())
            .path("/releases")
            .back();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Iterable<Release> iterate() {
        return new RtPagination<>(
            this.request,
            object -> new RtRelease(
                this.entry,
                this.owner,
                // @checkstyle MultipleStringLiterals (1 line)
                object.getInt("id")
            )
        );
    }

    @Override
    public Release get(final int number) {
        return new RtRelease(this.entry, this.owner, number);
    }

    @Override
    public Release create(
        final String tag
    ) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("tag_name", tag)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("id")
        );
    }

    @Override
    public void remove(final int number) throws IOException {
        this.request.method(Request.DELETE)
            .uri().path(Integer.toString(number)).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

}
