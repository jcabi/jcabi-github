/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github user organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 * @checkstyle MultipleStringLiterals (500 lines)
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
     * Github.
     */
    private final transient Github ghub;

    /**
     * User we're in.
     */
    private final transient User owner;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param user User
     */
    RtUserOrganizations(
        final Github github,
        final Request req,
        final User user
    ) {
        this.entry = req;
        this.ghub = github;
        this.owner = user;
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public User user() {
        return this.owner;
    }

    @Override
    public Iterable<Organization> iterate() throws IOException {
        final String login = this.owner.login();
        return new RtPagination<>(
            this.entry.uri().path("/users").path(login).path("/orgs").back(),
            new OrganizationMapping(this.ghub.organizations())
        );
    }

    /**
     * Maps organization JSON objects to Organization instances.
     */
    private static final class OrganizationMapping
        implements RtValuePagination.Mapping<Organization, JsonObject> {
        /**
         * Organizations.
         */
        private final transient Organizations organizations;

        /**
         * Ctor.
         * @param orgs Organizations
         */
        OrganizationMapping(final Organizations orgs) {
            this.organizations = orgs;
        }

        @Override
        public Organization map(final JsonObject object) {
            return this.organizations.get(object.getString("login"));
        }
    }
}
