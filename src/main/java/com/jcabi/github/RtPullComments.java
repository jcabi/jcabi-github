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
 * GitHub pull comment.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner" })
final class RtPullComments implements PullComments {

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
    private final transient Pull owner;

    /**
     * Public ctor.
     * @param req Request
     * @param pull Pull
     */
    RtPullComments(final Request req, final Pull pull) {
        this.entry = req;
        this.owner = pull;
        this.request = this.entry.uri()
            // @checkstyle MultipleStringLiterals (8 lines)
            .path("/repos")
            .path(pull.repo().coordinates().user())
            .path(pull.repo().coordinates().repo())
            .path("/pulls")
            .path(Integer.toString(pull.number()))
            .path("/comments")
            .back();
    }

    @Override
    public Pull pull() {
        return this.owner;
    }

    @Override
    public PullComment get(final int number) {
        return new RtPullComment(this.entry, this.owner, number);
    }

    @Override
    public Iterable<PullComment> iterate(
        final Map<String, String> params
    ) {
        return new RtPagination<>(
            this.request.uri().queryParams(params).back(),
            value -> this.get(
                // @checkstyle MultipleStringLiterals (3 lines)
                value.getInt("id")
            )
        );
    }

    @Override
    public Iterable<PullComment> iterate(
        final int number,
        final Map<String, String> params) {
        final Request newreq = this.entry.uri()
            .path("/repos")
            .path(this.owner.repo().coordinates().user())
            .path(this.owner.repo().coordinates().repo())
            .path("/pulls")
            .path(String.valueOf(number))
            .path("/comments")
            .back();
        return new RtPagination<>(
            newreq.uri().queryParams(params).back(),
            value -> this.get(
                value.getInt("id")
            )
        );
    }

    // @checkstyle ParameterNumberCheck (7 lines)
    @Override
    public PullComment post(
        final String body,
        final String commit,
        final String path,
        final int position
    ) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (4 line)
            .add("body", body)
            .add("commit_id", commit)
            .add("path", path)
            .add("position", position)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("id")
        );
    }

    @Override
    public PullComment reply(
        final String body,
        final int comment
    ) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("body", body)
            .add("in_reply_to", comment)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("id")
        );
    }

    @Override
    public void remove(final int number) throws IOException {
        this.request.uri().path(String.valueOf(number)).back()
            .method(Request.DELETE)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
}
