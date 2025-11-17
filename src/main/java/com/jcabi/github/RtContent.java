/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * GitHub content.
 *
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "location", "request", "owner" })
final class RtContent implements Content {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Path of this Content.
     */
    private final transient String location;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     * @param path Path of the content
     */
    RtContent(final Request req, final Repo repo, final String path) {
        final Coordinates coords = repo.coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/contents")
            .path(path)
            .back();
        this.owner = repo;
        this.location = path;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public String path() {
        return this.location;
    }

    @Override
    public int compareTo(
        final Content other
    ) {
        return this.path().compareTo(other.path());
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public InputStream raw() throws IOException {
        return new ByteArrayInputStream(
            this.request.reset(HttpHeaders.ACCEPT)
                .header(
                    HttpHeaders.ACCEPT,
                    "application/vnd.github.v3.raw"
                ).fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK).binary()
        );
    }
}
