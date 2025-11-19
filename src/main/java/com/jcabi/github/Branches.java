/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * Git branches.
 * @since 0.24
 * @see <a href="https://developer.github.com/v3/repos/#list-branches">List Branches API</a>
 */
@Immutable
public interface Branches {
    /**
     * Repo which the branches are in.
     * @return Repo
     */
    Repo repo();

    /**
     * Iterate over all branches in the repo.
     * @return Iterator of branches
     * @see <a href="https://developer.github.com/v3/repos/#list-branches">List Branches API</a>
     */
    Iterable<Branch> iterate();

    /**
     * Find branches by name.
     * @param name The name of the branch.
     * @return Branch found by name
     * @see <a href="https://developer.github.com/v3/repos/branches/#get-branch">Get Branch API</a>
     */
    Branch find(String name);
}
