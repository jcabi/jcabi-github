/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Gist comment.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
final class RtGistComment implements GistComment {
    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Gist we're in.
     */
    private final transient Gist owner;

    /**
     * Comment number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param req RESTful request
     * @param gist Gist of this comment
     * @param number Number of the get
     */
    RtGistComment(final Request req, final Gist gist, final int number) {
        this.request = req.uri()
            .path("/gists")
            .path(new Gist.Smart(gist).identifier())
            .path("/comments")
            .path(Integer.toString(number))
            .back();
        this.owner = gist;
        this.num = number;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Gist gist() {
        return this.owner;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public void remove() throws IOException {
        this.request.method(Request.DELETE).fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public int compareTo(
        final GistComment comment
    ) {
        return this.number() - comment.number();
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
