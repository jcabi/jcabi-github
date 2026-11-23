/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * GitHub commit comment.
 *
 * @since 2.0
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
final class RtRepoComment implements RepoComment {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Comment number.
     */
    private final transient long num;

    /**
     * Public ctor.
     *
     * @param req RESTful request
     * @param repo Repository of this comment
     * @param number Number of the comment
     */
    RtRepoComment(final Request req, final Repo repo, final long number) {
        this(
            req.uri()
                .path("/repos")
                .path(repo.coordinates().user())
                .path(repo.coordinates().repo())
                .path("/comments")
                .path(Long.toString(number))
                .back(),
            number,
            repo
        );
    }

    private RtRepoComment(final Request request, final long num, final Repo owner) {
        this.request = request;
        this.num = num;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public long number() {
        return this.num;
    }

    @Override
    public void remove() throws IOException {
        this.request.method(Request.DELETE).fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
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
    public int compareTo(final RepoComment other) {
        return Long.compare(this.number(), other.number());
    }
}
