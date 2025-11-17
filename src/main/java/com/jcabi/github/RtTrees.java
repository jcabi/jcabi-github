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
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github trees.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "entry" })
final class RtTrees implements Trees {

    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param req Entry point of API
     * @param repo Repository
     */
    RtTrees(final Request req, final Repo repo) {
        this.entry = req;
        this.owner = repo;
        this.request = req.uri()
            .path("/repos")
            .path(repo.coordinates().user())
            .path(repo.coordinates().repo())
            .path("/git")
            .path("/trees")
            .back();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Tree get(final String sha) {
        return new RtTree(this.entry, this.owner, sha);
    }

    @Override
    public Tree getRec(
        final String sha
    ) {
        return new RtTree(
            this.entry.uri().queryParam("recursive", "1").back(),
            this.owner, sha
        );
    }

    @Override
    public Tree create(
        final JsonObject params
    ) throws IOException {
        return this.get(
            this.request.method(Request.POST)
                .body().set(params).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getString("sha")
        );
    }
}
