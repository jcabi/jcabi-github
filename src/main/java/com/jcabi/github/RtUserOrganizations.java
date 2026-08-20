/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * GitHub user organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "ghub", "owner" })
final class RtUserOrganizations implements UserOrganizations {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

    /**
     * User we're in.
     */
    private final transient User owner;

    /**
     * Public ctor.
     * @param github GitHub
     * @param req Request
     * @param user User
     */
    RtUserOrganizations(
        final GitHub github,
        final Request req,
        final User user
    ) {
        this.entry = req;
        this.ghub = github;
        this.owner = user;
    }

    @Override
    public GitHub github() {
        return this.ghub;
    }

    @Override
    public User user() {
        return this.owner;
    }

    @Override
    public Iterable<Organization> iterate() throws IOException {
        return new RtPagination<>(
            this.entry.uri().path("/users").path(this.owner.login()).path("/orgs").back(),
            new OrganizationMapping(this.ghub.organizations())
        );
    }
}
