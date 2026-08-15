/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * GitHub Blob.
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"request", "hash" })
final class RtBlob implements Blob {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Blob SHA hash.
     */
    private final transient String hash;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     * @param sha Number of the get
     */
    RtBlob(final Request req, final Repo repo, final String sha) {
        this(
            req.uri()
                .path("/repos")
                .path(repo.coordinates().user())
                .path(repo.coordinates().repo())
                .path("/git")
                .path("/blobs")
                .path(sha)
                .back(),
            sha
        );
    }

    private RtBlob(final Request request, final String hash) {
        this.request = request;
        this.hash = hash;
    }

    @Override
    public String sha() {
        return this.hash;
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
