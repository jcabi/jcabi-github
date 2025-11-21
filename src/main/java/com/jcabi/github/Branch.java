/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;

/**
 * Git branch.
 * @see <a href="https://developer.github.com/v3/repos/#list-branches">List Branches API</a>
 * @since 0.24
 */
@Immutable
public interface Branch {
    /**
     * The repo we're in.
     * @return Repo
     */
    Repo repo();

    /**
     * Name of the branch.
     * @return Branch name
     */
    String name();

    /**
     * Commit that the branch currently points to.
     * @return Commit the branch currently points to
     */
    Commit commit();
}
