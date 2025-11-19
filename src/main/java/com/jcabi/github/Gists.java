/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;

/**
 * GitHub gists.
 * @see <a href="https://developer.github.com/v3/gists/">Gists API</a>
 * @since 0.1
 */
@Immutable
public interface Gists {

    /**
     * GitHub we're in.
     * @return GitHub
     */
    GitHub github();

    /**
     * Create a new gist.
     * @param files Names and content of files
     * @param visible Indicates whether the gist is public
     * @return Gist
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/gists/#create-a-gist">Create a Gist</a>
     */
    Gist create(
        Map<String, String> files, boolean visible
    ) throws IOException;

    /**
     * Get gist by name.
     * @param name Name of it
     * @return Gist
     * @see <a href="https://developer.github.com/v3/gists/#get-a-single-gist">Get a Single Gist</a>
     */
    Gist get(String name);

    /**
     * Iterate all gists.
     * @return Iterator of gists
     * @see <a href="https://developer.github.com/v3/gists/#list-gists">List Gists</a>
     */
    Iterable<Gist> iterate();

    /**
     * Removes a gist by id.
     *
     *
     * @param identifier Identifier of the gist to be removed.
     * @throws IOException If there is any I/O problem
     */
    void remove(String identifier) throws IOException;
}
