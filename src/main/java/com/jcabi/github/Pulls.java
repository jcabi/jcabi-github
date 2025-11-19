/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;

/**
 * GitHub pull requests.
 * @since 0.3
 * @see <a href="https://developer.github.com/v3/pulls/">Pull Request API</a>
 */
@Immutable
public interface Pulls {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Get specific get by number.
     * @param number Pull request number
     * @return Pull request
     * @see <a href="https://developer.github.com/v3/pulls/#get-a-single-pull-request">Get a Single Pull Request</a>
     */
    Pull get(int number);

    /**
     * Create new get.
     * @param title Title
     * @param head Head
     * @param base Base
     * @return Issue just created
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/pulls/#create-a-pull-request">Create a Pull Request</a>
     */
    Pull create(
        String title,
        String head,
        String base)
        throws IOException;

    /**
     * Iterate them all.
     * @param params Params
     * @return Iterator of issues
     * @see <a href="https://developer.github.com/v3/pulls/#list-pull-requests">List Pull Requests</a>
     */
    Iterable<Pull> iterate(Map<String, String> params);

}
