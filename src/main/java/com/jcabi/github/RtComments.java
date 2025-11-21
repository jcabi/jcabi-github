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
import java.util.Date;
import lombok.EqualsAndHashCode;

/**
 * GitHub comments.
 *
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner" })
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
final class RtComments implements Comments {

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
    private final transient Issue owner;

    /**
     * Public ctor.
     * @param req Request
     * @param issue Issue
     */
    RtComments(final Request req, final Issue issue) {
        this.entry = req;
        final Coordinates coords = issue.repo().coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/issues")
            .path(Integer.toString(issue.number()))
            .path("/comments")
            .back();
        this.owner = issue;
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
    public Comment get(final long number) {
        return new RtComment(this.entry, this.owner, number);
    }

    @Override
    public Comment post(final String text) throws IOException {
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
                .json().readObject().getJsonNumber("id").longValue()
        );
    }

    @Override
    public Iterable<Comment> iterate(final Date since) {
        return new RtPagination<>(
            this.request.uri()
                .queryParam("since", new GitHub.Time(since))
                .back(),
            object -> this.get(object.getJsonNumber("id").longValue())
        );
    }

}
