/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import jakarta.json.JsonValue;

/**
 * List of stargazers.
 *
 * @since 1.7.1
 * @see <a href="https://docs.github.com/en/rest/activity/starring?apiVersion=2022-11-28#list-stargazers">List Stargazers</a>
 */
public interface Stargazers {

    /**
     * Iterate over stargazers.
     *
     * @return Iterator of stargazers
     * @throws IOException If there is any I/O problem
     */
    Iterable<JsonValue> iterable() throws IOException;
}
