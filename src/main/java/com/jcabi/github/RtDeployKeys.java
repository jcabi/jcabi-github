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
import java.io.IOException;
import java.net.HttpURLConnection;
import jakarta.json.Json;
import lombok.EqualsAndHashCode;

/**
 * Github deploy keys.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "entry" })
final class RtDeployKeys implements DeployKeys {
    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful API request for these deploy keys.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param req RESTful API entry point
     * @param repo Repository
     */
    RtDeployKeys(final Request req, final Repo repo) {
        this.owner = repo;
        this.entry = req;
        this.request = req.uri()
            .path("/repos")
            .path(repo.coordinates().user())
            .path(repo.coordinates().repo())
            .path("/keys")
            .back();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Iterable<DeployKey> iterate() {
        return new RtPagination<>(
            this.request,
            object -> {
                //@checkstyle MultipleStringLiteralsCheck (1 line)
                return this.get(object.getInt("id"));
            }
        );
    }

    @Override
    public DeployKey get(final int number) {
        return new RtDeployKey(this.entry, number, this.owner);
    }

    @Override
    public DeployKey create(
        final String title,
        final String key
    )
        throws IOException {
        return this.get(
            this.request.method(Request.POST)
                .body().set(
                    Json.createObjectBuilder()
                        .add("title", title)
                        .add("key", key)
                        .build()
                ).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("id")
        );
    }
}
