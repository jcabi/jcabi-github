/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;

/**
 * GitHub for IT testing.
 *
 * @since 1.0
 */
public final class GitHubIT {

    /**
     * Ctor.
     */
    private GitHubIT() {
    }

    /**
     * Return GitHub connected to live server.
     * @return The obj
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static GitHub connect() {
        final String key = System.getProperty("failsafe.github.key");
        Assumptions.assumeTrue(
            !Matchers.emptyOrNullString().matches(key),
            "GitHub key is required for integration tests"
        );
        return new RtGitHub(key);
    }
}
