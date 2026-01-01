/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;

/**
 * GitHub hooks.
 * @see <a href="https://developer.github.com/v3/repos/hooks/">Hooks API</a>
 * @since 0.8
 */
@Immutable
public interface Hooks {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Iterate them all.
     * @return Iterator of hooks
     * @see <a href="https://developer.github.com/v3/repos/hooks/#list">List</a>
     */
    Iterable<Hook> iterate();

    /**
     * Remove hook by ID.
     * @param number ID of the label to remove
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/hooks/#delete-a-hook">List</a>
     */
    void remove(int number) throws IOException;

    /**
     * Get specific hook by number.
     * @param number Hook number
     * @return Hook
     * @see <a href="https://developer.github.com/v3/repos/hooks/#get-single-hook">Get single hook</a>
     */
    Hook get(int number);

    /**
     * Create new hook.
     * @param name Hook name
     * @param config Configuration for the hook
     * @param events Events that trigger the hook
     * @param active Actually trigger the hook when the events occur?
     * @return Hook
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/hooks/#create-a-hook">Create a hook</a>
     * @checkstyle ParameterNumberCheck (2 lines)
     */
    Hook create(
        String name, Map<String, String> config,
        Iterable<Event> events, boolean active
    ) throws IOException;
}
