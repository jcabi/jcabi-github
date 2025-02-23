/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Github starring API.
 *
 * @since 0.15
 * @see <a href="https://developer.github.com/v3/activity/starring/">Starring API</a>
 */
@Immutable
public interface Stars {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Check if repo is starred.
     * @return True if repo is starred
     * @throws IOException - If anything goes wrong.
     */
    boolean starred() throws IOException;

    /**
     * Star repository.
     * @throws IOException - If anything goes wrong.
     */
    void star() throws IOException;

    /**
     * Unstar repository.
     * @throws IOException - If anything goes wrong.
     */
    void unstar() throws IOException;
}
