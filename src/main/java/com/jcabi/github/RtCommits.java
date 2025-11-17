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
 * Github Commits.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"entry", "request", "owner" })
public final class RtCommits implements Commits {
    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request for the commits.
     */
    private final transient Request request;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * Public constructor.
     * @param req The entry request.
     * @param repo The owner repo.
     */
    RtCommits(
        final Request req,
        final Repo repo
    ) {
        this.entry = req;
        this.owner = repo;
        this.request = req.uri().path("/repos").path(repo.coordinates().user())
            .path(repo.coordinates().repo())
            .path("/git")
            .path("/commits").back();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Commit create(
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

    @Override
    public Commit get(
        final String sha
    ) {
        return new RtCommit(this.entry, this.owner, sha);
    }

    @Override
    public Statuses statuses(
        final String ref
    ) {
        return new RtStatuses(this.entry, this.get(ref));
    }
}
