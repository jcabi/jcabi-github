/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import lombok.EqualsAndHashCode;

/**
 * Github comment.
 *
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
final class RtComment implements Comment {

    /**
     * Content field name.
     */
    private static final String CONTENT = "content";

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Issue we're in.
     */
    private final transient Issue owner;

    /**
     * Comment number.
     */
    private final transient long num;

    /**
     * Public ctor.
     * @param req RESTful request
     * @param issue Owner of this comment
     * @param number Number of the get
     */
    RtComment(final Request req, final Issue issue, final long number) {
        final Coordinates coords = issue.repo().coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/issues")
            .path("/comments")
            .path(Long.toString(number))
            .back();
        this.owner = issue;
        this.num = number;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Issue issue() {
        return this.owner;
    }

    @Override
    public long number() {
        return this.num;
    }

    @Override
    public void remove() throws IOException {
        this.request.method(Request.DELETE).fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public int compareTo(
        final Comment comment
    ) {
        return Long.compare(this.number(), comment.number());
    }

    @Override
    public void react(final Reaction reaction) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add(RtComment.CONTENT, reaction.type())
            .build();
        this.request.method(Request.POST)
            .body().set(json).back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public Iterable<Reaction> reactions() {
        return new RtPagination<>(
            this.request.uri().path("/reactions").back(),
            object -> new Reaction.Simple(object.getString(RtComment.CONTENT))
        );
    }
}
