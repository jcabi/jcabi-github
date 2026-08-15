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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import lombok.EqualsAndHashCode;

/**
 * GitHub pull requests.
 * @since 0.3
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
final class RtPulls implements Pulls {

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
    RtPulls(final Request req, final Repo repo) {
        this(
            req,
            req.uri()
                .path("/repos")
                .path(repo.coordinates().user())
                .path(repo.coordinates().repo())
                .path("/pulls")
                .back(),
            repo
        );
    }

    private RtPulls(final Request entry, final Request request, final Repo owner) {
        this.entry = entry;
        this.request = request;
        this.owner = owner;
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
    public Pull get(final int number) {
        return new RtPull(this.entry, this.owner, number);
    }

    @Override
    public Pull create(
        final String title,
        final String head,
        final String base) throws IOException {
        return this.get(
            this.request.method(Request.POST)
                .body().set(
                    Json.createObjectBuilder()
                        .add("title", title)
                        .add("head", head)
                        .add("base", base)
                        .build()
                ).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("number")
        );
    }

    @Override
    public Iterable<Pull> iterate(final Map<String, String> params) {
        return new RtPagination<>(
            this.request.uri().queryParams(params).back(),
            object -> this.get(object.getInt("number"))
        );
    }
}
