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
 * GitHub organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "ghub", "request" })
final class RtOrganizations implements Organizations {
    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

    /**
     * Public ctor.
     * @param github GitHub
     * @param req Request
     */
    RtOrganizations(final GitHub github, final Request req) {
        this.entry = req;
        this.request = this.entry.uri().path("/user").path("/orgs").back();
        this.ghub = github;
    }

    @Override
    public Organization get(
        final String login
    ) {
        return new RtOrganization(this.ghub, this.entry, login);
    }

    @Override
    public Iterable<Organization> iterate() {
        return new RtPagination<>(
            this.request,
            object -> this.get(object.getString("login"))
        );
    }
}
