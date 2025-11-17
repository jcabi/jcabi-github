/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * GitHub forks.
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/repos/forks/">Forks API</a>
 */
@Immutable
public interface Forks {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Iterate all forks.
     *
     * @param sort The sort order.
     * @return All forks
     * @see <a href="https://developer.github.com/v3/repos/forks/#list-forks">List forks</a>
     */
    Iterable<Fork> iterate(String sort);

    /**
     * Create a fork for the authenticated user.
     *
     * @param organization The organization the repository will be forked into.
     * @return The new fork
     * @throws IOException  If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/forks/#create-a-fork">Create a fork</a>
     */
    Fork create(String organization) throws IOException;
}
