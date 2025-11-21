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
 * GitHub starring API.
 * @see <a href="https://developer.github.com/v3/activity/starring/">Starring API</a>
 * @since 0.15
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"owner", "request"})
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
final class RtStars implements Stars {

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
    RtStars(final Request req, final Repo repo) {
        final Coordinates coords = repo.coordinates();
        this.request = req.uri()
            .path("/user/starred")
            .path(coords.user())
            .path(coords.repo())
            .back();
        this.owner = repo;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public boolean starred() throws IOException {
        return this.request
            .fetch().as(RestResponse.class)
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
    public void star() throws IOException {
        this.request
            .method(Request.PUT)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public void unstar() throws IOException {
        this.request
            .method(Request.DELETE)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
}
