/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import lombok.EqualsAndHashCode;

/**
 * GitHub users.
 * @since 0.4
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "request" })
final class RtUsers implements Users {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param github GitHub
     * @param req Request
     */
    RtUsers(
        final GitHub github,
        final Request req
    ) {
        this.entry = req;
        this.ghub = github;
        this.request = this.entry.uri().path("/users").back();
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public GitHub github() {
        return this.ghub;
    }

    @Override
    public User self() {
        return new RtUser(this.ghub, this.entry, "");
    }

    @Override
    public User get(final String login) {
        return new RtUser(this.ghub, this.entry, login);
    }

    @Override
    public User add(final String login) {
        throw new UnsupportedOperationException("#add not implemented");
    }

    @Override
    public Iterable<User> iterate(
        final String identifier
    ) {
        return new RtPagination<>(
            this.request.uri().queryParam("since", identifier).back(),
            object -> this.get(
                String.valueOf(object.getInt("id"))
            )
        );
    }

}
