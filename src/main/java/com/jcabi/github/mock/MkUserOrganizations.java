/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Github;
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
 * Github user organizations.
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
    public Github github() {
        return new MkGithub(this.storage, this.self);
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

    private static final class OrganizationMapping
        implements MkIterable.Mapping<Organization> {
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
        public Organization map(final XML xml) {
            return this.organizations.get(
                xml.xpath("login/text()").get(0)
            );
        }
    }
}
