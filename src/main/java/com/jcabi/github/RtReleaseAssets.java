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
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import lombok.EqualsAndHashCode;

/**
 * Github release assets.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner" })
final class RtReleaseAssets implements ReleaseAssets {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Owner of assets.
     */
    private final transient Release owner;

    /**
     * Public ctor.
     * @param req Request
     * @param release Issue
     */
    RtReleaseAssets(
        final Request req,
        final Release release
    ) {
        this.entry = req;
        final Coordinates coords = release.repo().coordinates();
        // @checkstyle MultipleStringLiteralsCheck (7 lines)
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/releases")
            .path(Integer.toString(release.number()))
            .path("/assets")
            .back();
        this.owner = release;
    }

    @Override
    public Release release() {
        return this.owner;
    }

    @Override
    public Iterable<ReleaseAsset> iterate() {
        return new RtPagination<>(
            this.request.uri().back()
                .method(Request.GET),
            value -> this.get(
                //@checkstyle MultipleStringLiteralsCheck (1 line)
                value.getInt("id")
            )
        );
    }

    @Override
    public ReleaseAsset upload(
        final byte[] content,
        final String type,
        final String name
    ) throws IOException {
        return this.get(
            this.request.uri()
                .set(URI.create("https://uploads.github.com"))
                .path("/repos")
                .path(this.owner.repo().coordinates().user())
                .path(this.owner.repo().coordinates().repo())
                .path("/releases")
                .path(String.valueOf(this.owner.number()))
                .path("/assets")
                .queryParam("name", name)
                .back()
                .method(Request.POST)
                .reset(HttpHeaders.CONTENT_TYPE)
                .header(HttpHeaders.CONTENT_TYPE, type)
                .body().set(content).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("id")
        );
    }

    @Override
    public ReleaseAsset get(final int number) {
        return new RtReleaseAsset(this.entry, this.owner, number);
    }

}
