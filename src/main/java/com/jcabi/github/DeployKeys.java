/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * GitHub deploy keys.
 * @see <a href="https://developer.github.com/v3/repos/keys/">Deploy Keys API</a>
 * @since 0.8
 */
@Immutable
public interface DeployKeys {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Iterate them all.
     * @return Iterator of deploy keys
     * @see <a href="https://developer.github.com/v3/repos/keys/#list">List</a>
     */
    Iterable<DeployKey> iterate();

    /**
     * Get a single deploy key.
     * @param number Id of a deploy key
     * @return Deploy key
     * @see <a href="https://developer.github.com/v3/repos/keys/#get">Get a deploy key</a>
     */
    DeployKey get(int number);

    /**
     * Create a deploy key.
     * @param title Title
     * @param key Key
     * @return A new deploy key
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/keys/#create">Add a new deploy key</a>
     */
    DeployKey create(String title, String key) throws IOException;

}
