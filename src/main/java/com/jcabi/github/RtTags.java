/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * GitHub Tags.
 * @since 0.15
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"entry", "request", "owner" })
final class RtTags implements Tags {

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
     * @param req The entry request
     * @param repo The owner repo
     */
    RtTags(final Request req, final Repo repo) {
        this(
            req,
            repo,
            req.uri().path("/repos").path(repo.coordinates().user())
                .path(repo.coordinates().repo()).path("/git").path("/tags").back()
        );
    }

    private RtTags(final Request entry, final Repo owner, final Request request) {
        this.entry = entry;
        this.owner = owner;
        this.request = request;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Tag create(final JsonObject params) throws IOException {
        final Tag created = this.get(
            this.request.method(Request.POST)
                .body().set(params).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getString("sha")
        );
        new RtReferences(this.entry, this.owner).create(
            String.format("refs/tags/%s", params.getString("tag")),
            created.key()
        );
        return created;
    }

    @Override
    public Tag get(final String sha) {
        return new RtTag(this.entry, this.owner, sha);
    }
}
