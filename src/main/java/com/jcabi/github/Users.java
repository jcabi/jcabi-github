/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * GitHub users.
 * @see <a href="https://developer.github.com/v3/users/">Users API</a>
 * @since 0.1
 */
@Immutable
public interface Users {

    /**
     * GitHub we're in.
     * @return GitHub
     */
    GitHub github();

    /**
     * Get myself.
     * @return Myself
     */
    User self();

    /**
     * Get user by login.
     * @param login Login of it
     * @return User
     * @see <a href="https://developer.github.com/v3/users/#get-a-single-user">Get a Single User</a>
     */
    User get(String login);

    /**
     * Add user by login and returns it.
     * @param login Login of it
     * @return Added user
     */
    User add(String login);

    /**
     * Iterate all users, starting with the one you've seen already.
     * @param identifier The integer ID of the last User that you’ve seen.
     * @return Iterator of gists
     * @see <a href="https://developer.github.com/v3/users/#get-all-users">Get All Users</a>
     */
    Iterable<User> iterate(
        String identifier
    );

}
