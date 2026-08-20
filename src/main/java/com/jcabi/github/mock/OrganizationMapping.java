/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Organization;
import com.jcabi.github.Organizations;
import com.jcabi.xml.XML;

/**
 * Mapping for Organizations.
 * @since 0.24
 */
final class OrganizationMapping
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
