/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.Collection;
import lombok.EqualsAndHashCode;

/**
 * GitHub pull comment.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
final class RtPullComment implements PullComment {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Pull we're in.
     */
    private final transient Pull owner;

    /**
     * Comment number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param req RESTful request
     * @param pull Owner of this comment
     * @param number Number of the get
     */
    RtPullComment(final Request req, final Pull pull, final int number) {
        final Coordinates coords = pull.repo().coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/pulls")
            .path("/comments")
            .path(Integer.toString(number))
            .back();
        this.owner = pull;
        this.num = number;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
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
    public Pull pull() {
        return this.owner;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public void react(final Reaction reaction) {
        throw new UnsupportedOperationException("React not implemented");
    }

    @Override
    public Collection<Reaction> reactions() {
        throw new UnsupportedOperationException(
            "reactions() not implemented"
        );
    }

    @Override
    public int compareTo(
        final PullComment comment
    ) {
        return this.number() - comment.number();
    }

}
