/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * GitHub organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
@Immutable
public interface Organizations {
    /**
     * Get specific organization by name.
     * @param login Login name of the organization.
     * @return Organization
     * @see <a href="https://developer.github.com/v3/orgs/#get-an-organization">Get a Single Organization</a>
     */
    Organization get(String login);

    /**
     * Iterate over organizations of the currently logged-in user.
     * @return Iterator of Organizations
     * @see <a href="https://developer.github.com/v3/orgs/#list-your-organizations">List Your Organizations</a>
     */
    Iterable<Organization> iterate();
}
