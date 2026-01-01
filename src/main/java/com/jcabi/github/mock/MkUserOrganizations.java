/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.GitHub;
import com.jcabi.github.Organization;
import com.jcabi.github.Organizations;
import com.jcabi.github.User;
import com.jcabi.github.UserOrganizations;
import com.jcabi.xml.XML;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * GitHub user organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 * @checkstyle ClassDataAbstractionCoupling (200 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
@SuppressWarnings("PMD.TooManyMethods")
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
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    MkUserOrganizations(
        final MkStorage stg,
        final String login
    )
        throws IOException {
        this.storage = stg;
        this.self = login;
        this.storage.apply(
            new Directives().xpath("/github").addIf("orgs")
        );
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
            new MkUserOrganizations.OrganizationMapping(new MkOrganizations(this.storage))
        );
    }

    /**
     * Mapping for Organizations.
     * @since 0.24
     */
    private static final class OrganizationMapping
        implements MkIterable.Mapping<Organization> {
        /**
         * Organizations.
         */
        private final transient Organizations orgs;

        /**
         * Ctor.
         * @param organizations Organizations
         */
        OrganizationMapping(final Organizations organizations) {
            this.orgs = organizations;
        }

        @Override
        public Organization map(final XML xml) {
            return this.orgs.get(
                xml.xpath("login/text()").get(0)
            );
        }
    }
}
