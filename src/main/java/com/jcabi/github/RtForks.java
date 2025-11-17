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
 * GitHub forks.
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/repos/forks/">Forks API</a>
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"request", "owner" })
final class RtForks implements Forks {

    /**
     * Fork's id name in JSON object.
     */
    public static final String ID = "id";

    /**
     * Restful Request.
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
    public RtForks(final Request req, final Repo repo) {
        this.request = req.uri()
            .path("/repos")
            .path(repo.coordinates().user())
            .path(repo.coordinates().repo())
            .path("forks")
            .back();
        this.owner = repo;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Iterable<Fork> iterate(
        final String sort) {
        return new RtPagination<>(
            this.request.uri().queryParam("sort", sort).back(),
            object -> this.get(object.getInt(RtForks.ID))
        );
    }

    @Override
    public Fork create(
        final String organization) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("organization", organization)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_ACCEPTED)
                .as(JsonResponse.class)
                .json().readObject().getInt(RtForks.ID)
        );
    }

    /**
     * Get fork by number.
     * @param number Fork number
     * @return Fork
     */
    private RtFork get(final Integer number) {
        return new RtFork(this.request, this.owner, number);
    }
}
