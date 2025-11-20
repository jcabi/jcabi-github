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
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * GitHub Assignees.
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
final class RtAssignees implements Assignees {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repo
     */
    RtAssignees(final Request req, final Repo repo) {
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/assignees")
            .back();
        this.owner = repo;
    }

    @Override
    public Iterable<User> iterate() {
        return new RtPagination<>(
            this.request,
            object -> new RtUser(
                this.owner.github(),
                this.entry,
                object.getString("login")
            )
        );
    }

    @Override
    public boolean check(
        final String login
    ) throws IOException {
        return this.request
            .method(Request.GET)
            .uri().path(login).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(
                Matchers.is(
                    Matchers.oneOf(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        HttpURLConnection.HTTP_NOT_FOUND
                    )
                )
            ).status() == HttpURLConnection.HTTP_NO_CONTENT;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }
}
