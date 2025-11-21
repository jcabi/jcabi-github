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
 * GitHub release asset.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
final class RtReleaseAsset implements ReleaseAsset {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Release we're in.
     */
    private final transient Release owner;

    /**
     * Release Asset number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param req RESTful Request
     * @param release Release
     * @param number Number of the release asset.
     */
    RtReleaseAsset(
        final Request req,
        final Release release,
        final int number
    ) {
        final Coordinates coords = release.repo().coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/releases")
            .path("/assets")
            .path(Integer.toString(number))
            .back();
        this.owner = release;
        this.num = number;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Release release() {
        return this.owner;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public void remove() throws IOException {
        this.request.method(Request.DELETE).fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public InputStream raw() throws IOException {
        return new ByteArrayInputStream(
            this.request.method(Request.GET)
                .reset(HttpHeaders.ACCEPT).header(
                    HttpHeaders.ACCEPT,
                    "application/vnd.github.v3.raw"
                )
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK)
                .binary()
        );
    }

}
