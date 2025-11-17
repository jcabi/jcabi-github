/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import jakarta.json.JsonObject;
import java.io.IOException;

/**
 * Github Git Data Trees.
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/git/trees/">Trees API</a>
 */
@Immutable
public interface Trees {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Get specific tree by sha.
     * @param sha Tree sha
     * @return Tree
     * @see <a href="https://developer.github.com/v3/git/trees">Get a tree</a>
     */
    Tree get(String sha);

    /**
     * Get specific tree recursively by sha.
     * @param sha Tree sha
     * @return Tree
     * @see <a href="https://developer.github.com/v3/git/trees">Get a tree</a>
     */
    Tree getRec(String sha);
    /**
     * Create new tree.
     * @param params Parameters to create new tree
     * @return Tree
     * @throws IOException If there is any I/O problem
     */
    Tree create(
        JsonObject params)
        throws IOException;
}
