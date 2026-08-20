/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.GitHub;
import com.jcabi.github.Organization;
import com.jcabi.github.User;
import com.jcabi.github.UserOrganizations;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * GitHub user organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
final class MkUserOrganizations implements UserOrganizations {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @throws IOException If there is any I/O problem
     */
    MkUserOrganizations(
        final MkStorage stg,
        final String login
    ) throws IOException {
        this(login, MkUserOrganizations.bootstrap(stg));
    }

    private MkUserOrganizations(final String login, final MkStorage stg) {
        this.storage = stg;
        this.self = login;
    }

    @Override
    public GitHub github() {
        return new MkGitHub(this.storage, this.self);
    }

    @Override
    public User user() {
        return new MkUser(this.storage, this.self);
    }

    @Override
    public Iterable<Organization> iterate() throws IOException {
        return new MkIterable<>(
            this.storage,
            "/github/orgs/org",
            new OrganizationMapping(new MkOrganizations(this.storage))
        );
    }

    private static MkStorage bootstrap(final MkStorage stg) throws IOException {
        stg.apply(
            new Directives().xpath("/github").addIf("orgs")
        );
        return stg;
    }
}
