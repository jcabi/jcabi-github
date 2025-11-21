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
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * GitHub Git Data Blobs.
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "owner", "request" })
@SuppressWarnings({"PMD.ConstructorOnlyInitializesOrCallOtherConstructors"})
final class RtBlobs implements Blobs {

    /**
     * API entry point.
     */
    private final transient Request entry;

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
    RtBlobs(
        final Request req,
        final Repo repo) {
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/git")
            .path("/blobs")
            .back();
        this.owner = repo;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Blob get(
        final String sha) {
        return new RtBlob(this.entry, this.owner, sha);
    }

    @Override
    public Blob create(
        final String content,
        final String encoding)
        throws IOException {
        return this.get(
            this.request.method(Request.POST)
                .body().set(
                    Json.createObjectBuilder()
                        .add("content", content)
                        .add("encoding", encoding)
                        .build()
                ).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getString("sha")
        );
    }
}
