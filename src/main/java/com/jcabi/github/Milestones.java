/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;

/**
 * Github Milestones.
 * @see <a href="https://developer.github.com/v3/issues/milestones/">Milestones API</a>
 * @since 0.7
 */
@Immutable
public interface Milestones {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Create Milestone.
     * @param title Milestone creation JSON
     * @return Milestone
     * @throws java.io.IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/issues/milestones/#create-a-milestone">Create Milestone</a>
     * @since 0.5
     */
    Milestone create(String title)
        throws IOException;

    /**
     * Get specific milestone by number.
     * @param number Milestone number
     * @return Milestone
     * @see <a href="https://developer.github.com/v3/issues/milestones/#get-a-single-milestone">Get a single milestone</a>
     */
    Milestone get(int number);

    /**
     * Iterate them all.
     * @param params Iterating parameters, as requested by API
     * @return Iterator of milestones
     * @see <a href="https://developer.github.com/v3/issues/milestones/#list-milestones-for-a-repository">List milestones for a repository</a>
     */
    Iterable<Milestone> iterate(
        Map<String, String> params);

    /**
     * Remove milestone by number.
     * @param number Milestone number
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/issues/milestones/#delete-a-milestone">Delete a milestone</a>
     */
    void remove(int number)
        throws IOException;
}
