/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Organization;
import com.jcabi.github.PublicMembers;
import com.jcabi.github.User;
import java.io.IOException;
import org.xembly.Directives;

/**
 * Mock for public members of a GitHub organization.
 *
 * @see <a href="https://developer.github.com/v3/orgs/members/">Organization Members API</a>
 * @since 0.24
 */
public final class MkPublicMembers implements PublicMembers {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Organization.
     */
    private final transient Organization organization;

    /**
     * Public ctor.
     * @param stg Storage
     * @param organ Organization
     */
    public MkPublicMembers(
        final MkStorage stg,
        final Organization organ
    ) {
        this.storage = stg;
        this.organization = organ;
    }

    @Override
    public Organization org() {
        return this.organization;
    }

    @Override
    public void conceal(
        final User user
    ) throws IOException {
        this.storage.apply(
            new Directives()
                .xpath(this.xpath(user))
                .set("false")
        );
    }

    @Override
    public void publicize(
        final User user
    ) throws IOException {
        this.storage.apply(
            new Directives()
                .xpath(this.xpath(user))
                .set("true")
        );
    }

    @Override
    public Iterable<User> iterate() {
        return new MkIterable<>(
            this.storage,
            String.format("%s/member[public='true']/login", this.xpath()),
            xml -> new MkUser(
                this.storage,
                xml.xpath("text()").get(0)
            )
        );
    }

    @Override
    public boolean contains(
        final User user
    ) {
        boolean result = false;
        for (final User member : this.iterate()) {
            if (member.equals(user)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * XPath of publicity of user's membership in the organization.
     * @param user User
     * @return XPath
     * @throws IOException If there is an I/O problem
     */
    private String xpath(final User user) throws IOException {
        return String.format(
            "%s/member[login='%s']/public",
            this.xpath(),
            user.login()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/orgs/org[login='%s']/members",
            this.organization.login()
        );
    }
}
