/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * GitHub statuses for a given commit.
 *
 * @since 0.23
 */
public class RtStatuses implements Statuses {

    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Commit cmmt.
     */
    private final transient Commit cmmt;

    /**
     * Create a new status-aware object based on given commit.
     *
     * @param req Http request
     * @param commit Specific commit
     */
    RtStatuses(final Request req, final Commit commit) {
        this(
            req,
            commit,
            req.uri()
                .path("/repos")
                .path(commit.repo().coordinates().user())
                .path(commit.repo().coordinates().repo())
                .path("/statuses")
                .path(commit.sha())
                .back()
        );
    }

    private RtStatuses(final Request entry, final Commit cmmt, final Request request) {
        this.entry = entry;
        this.cmmt = cmmt;
        this.request = request;
    }

    @Override
    public final String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public final Commit commit() {
        return this.cmmt;
    }

    @Override
    public final Status create(
        final Statuses.StatusCreate status
    ) throws IOException {
        return new RtStatus(
            this.cmmt,
            this.request.method(Request.POST)
                .body().set(status.json()).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject()
        );
    }

    @Override
    public final Iterable<Status> list(final String ref) {
        return new RtPagination<>(
            this.entry.uri()
                .path("/repos")
                .path(this.cmmt.repo().coordinates().user())
                .path(this.cmmt.repo().coordinates().repo())
                .path("/commits")
                .path(ref)
                .path("/statuses")
                .back(),
            object -> new RtStatus(this.cmmt, object)
        );
    }

    @Override
    public final JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
