/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonStructure;
import lombok.EqualsAndHashCode;

/**
 * Github comments.
 *
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
        this.entry = req;
        this.request = this.entry.uri()
            .path("/gists")
            .path(gist.identifier())
            .path("/comments")
            .back();
        this.owner = gist;
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
    public GistComment post(
        final String text
    ) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("body", text)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                // @checkstyle MultipleStringLiterals (1 line)
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
