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
 * @since 0.23
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
public class RtStatuses implements Statuses {

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
     * @param req Http request
     * @param commit Specific commit
     */
    RtStatuses(final Request req, final Commit commit) {
        final Coordinates coords = commit.repo().coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/statuses")
            .path(commit.sha())
            .back();
        this.cmmt = commit;
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
        final JsonObject response = this.request.method(Request.POST)
            .body().set(status.json()).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_CREATED)
            .as(JsonResponse.class)
            .json().readObject();
        return new RtStatus(this.cmmt, response);
    }

    // @todo #1126:30min Implement this method which gets all status
    //  messages for a given commit.
    @Override
    public final Iterable<Status> list(
        final String ref
    ) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public final JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
