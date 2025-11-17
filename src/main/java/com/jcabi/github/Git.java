/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * GitHub Git.
 *
 * @since 0.8
 */
@Immutable
public interface Git {

    /**
     * Owner of it.
     * @return Repo
     */
    Repo repo();

    /**
     * Get its blobs.
     * @return Blobs
     * @see <a href="https://developer.github.com/v3/git/blobs/">Blobs API</a>
     * @throws IOException If some io problem occurs
     */
    Blobs blobs() throws IOException;

    /**
     * Get its commits.
     * @return Commits
     * @see <a href="https://developer.github.com/v3/git/commits/">Commits API</a>
     */
    Commits commits();

    /**
     * Get its references.
     * @return References
     * @see <a href="https://developer.github.com/v3/git/references/">References API</a>
     */
    References references();

    /**
     * Get its tags.
     * @return Tags
     * @see <a href="https://developer.github.com/v3/git/tags/">Tags API</a>
     */
    Tags tags();

    /**
     * Get its trees.
     * @return Trees
     * @see <a href="https://developer.github.com/v3/git/trees/">Trees API</a>
     */
    Trees trees();
}
