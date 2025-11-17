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
import lombok.EqualsAndHashCode;

/**
 * GitHub public key.
 *
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
final class RtPublicKey implements PublicKey {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * User we're in.
     */
    private final transient User owner;

    /**
     * Public key ID number.
     */
    private final transient int num;

    /**
     * Public ctor.
     *
     * @param req RESTful request
     * @param user Owner of this comment
     * @param number Number of the get
     */
    public RtPublicKey(final Request req, final User user, final int number) {
        this.request = req.uri()
            .path("/user")
            .path("/keys")
            .path(Integer.toString(number))
            .back();
        this.owner = user;
        this.num = number;
    }

    @Override
    public User user() {
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
        final JsonObject json)
        throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }
}
