/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.util.Collection;

/**
 * Github checks.
 *
 * @see <a href="https://docs.github.com/en/rest/checks/runs?apiVersion=2022-11-28">Check Runs API</a>
 * @since 1.5.0
 */
public interface Checks {

    /**
     * Get all checks.
     * @return Checks.
     * @throws IOException If there is any I/O problem.
     */
    Collection<? extends Check> all() throws IOException;

}
