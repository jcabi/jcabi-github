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
import java.util.Map;
import lombok.EqualsAndHashCode;

/**
 * GitHub milestones.
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"entry", "request", "owner" })
@SuppressWarnings({"PMD.ConstructorOnlyInitializesOrCallOtherConstructors"})
final class RtMilestones implements Milestones {
    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     */
    RtMilestones(final Request req, final Repo repo) {
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/milestones")
            .back();
        this.owner = repo;
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
    public Milestone create(
        final String title)
        throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("title", title)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("number")
        );
    }

    @Override
    public Milestone get(final int number) {
        return new RtMilestone(this.entry, this.owner, number);
    }

    @Override
    public void remove(final int number) throws IOException {
        this.request.method(Request.DELETE)
            .uri().path(Integer.toString(number)).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Iterable<Milestone> iterate(
        final Map<String, String> params) {
        return new RtPagination<>(
            this.request.uri().queryParams(params).back(),
            object -> this.get(object.getInt("number"))
        );
    }
}
