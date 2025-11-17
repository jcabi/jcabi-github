/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Organization;
import com.jcabi.github.Organizations;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * GitHub organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage" })
final class MkOrganizations implements Organizations {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Public ctor.
     * @param stg Storage
     * @throws IOException If there is any I/O problem
     */
    MkOrganizations(
        final MkStorage stg
    )
        throws IOException {
        this.storage = stg;
        this.storage.apply(
            new Directives().xpath("/github").addIf("orgs")
        );
    }

    @Override
    public Organization get(
        final String login
    ) {
        try {
            this.storage.apply(
                new Directives()
                    .xpath(
                        String.format(
                            "/github/orgs[not(org[login='%s'])]",
                            login
                    )
                )
                    .add("org")
                    .add("login").set(login).up()
                    .add("members").up()
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return new MkOrganization(this.storage, login);
    }

    @Override
    public Iterable<Organization> iterate() {
        return new MkIterable<>(
            this.storage,
            String.format("%s/org", this.xpath()),
            xml -> this.get(
                xml.xpath("login/text()").get(0)
            )
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return "/github/orgs";
    }
}
