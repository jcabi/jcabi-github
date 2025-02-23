/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.Matchers;
import org.junit.Assume;

/**
 * GitHub for IT testing.
 *
 * @since 1.0
 */
public final class GithubIT {

    /**
     * Return Github connected to live server.
     * @return The obj
     */
    public Github connect() {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(
            key,
            Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
        );
        return new RtGithub(key);
    }
}
