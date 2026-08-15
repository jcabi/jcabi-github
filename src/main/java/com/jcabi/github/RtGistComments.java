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
import lombok.EqualsAndHashCode;

/**
 * GitHub comments.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner" })
final class RtGistComments implements GistComments {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Owner of comments.
     */
    private final transient Gist owner;

    /**
     * Public ctor.
     * @param req Request
     * @param gist Gist
     */
    RtGistComments(final Request req, final Gist gist) {
        this(
            req,
            req.uri()
                .path("/gists")
                .path(gist.identifier())
                .path("/comments")
                .back(),
            gist
        );
    }

    private RtGistComments(final Request entry, final Request request, final Gist owner) {
        this.entry = entry;
        this.request = request;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Gist gist() {
        return this.owner;
    }

    @Override
    public GistComment get(final int number) {
        return new RtGistComment(this.entry, this.owner, number);
    }

    @Override
    public GistComment post(final String text) throws IOException {
        return this.get(
            this.request.method(Request.POST)
                .body().set(
                    Json.createObjectBuilder()
                        .add("body", text)
                        .build()
                ).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("id")
        );
    }

    @Override
    public Iterable<GistComment> iterate() {
        return new RtPagination<>(
            this.request,
            object -> this.get(object.getInt("id"))
        );
    }
}
