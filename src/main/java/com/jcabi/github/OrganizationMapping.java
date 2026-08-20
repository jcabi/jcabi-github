/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.JsonObject;

/**
 * Maps organization JSON objects to Organization instances.
 * @since 0.24
 */
final class OrganizationMapping
    implements RtValuePagination.Mapping<Organization, JsonObject> {

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
    public Organization map(final JsonObject object) {
        return this.orgs.get(object.getString("login"));
    }
}
