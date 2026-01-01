/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Public members of a GitHub organization.
 *
 * @see <a href="https://developer.github.com/v3/orgs/members/">Organization Members API</a>
 * @since 0.24
 */
@Immutable
public interface PublicMembers {
    /**
     * Organization of which these are public members.
     * @return Organization
     */
    Organization org();

    /**
     * Conceal a user's membership from public view.
     * @param user User whose membership to conceal
     * @throws IOException If an I/O problem occurs
     * @see <a href="https://developer.github.com/v3/orgs/members/#conceal-a-users-membership">Conceal a user's membership</a>
     */
    void conceal(
        User user
    ) throws IOException;

    /**
     * Make a user's membership publicly visible.
     * @param user User whose membership to publicize
     * @throws IOException If an I/O problem occurs
     * @see <a href="https://developer.github.com/v3/orgs/members/#publicize-a-users-membership">Publicize a user's membership</a>
     */
    void publicize(
        User user
    ) throws IOException;

    /**
     * Get all users who are public members of this organization.
     * @return Members
     * @see <a href="https://developer.github.com/v3/orgs/members/#public-members-list">Public members list</a>
     */
    Iterable<User> iterate();

    /**
     * Check whether the user is a public member of this organization.
     * @param user User to check public organization membership of
     * @return Is the user a public member of this organization?
     * @throws IOException If an I/O problem occurs
     * @see <a href="https://developer.github.com/v3/orgs/members/#check-public-membership">Check public membership</a>
     */
    boolean contains(
        User user
    ) throws IOException;
}
