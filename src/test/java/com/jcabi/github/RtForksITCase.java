/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtForks}.
 * @since 0.1
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtForksITCase {

    @Test
    void createsFork() throws IOException {
        final String organization = RtForksITCase.organization();
        final Repo repo = new RepoRule().repo(RtForksITCase.repos());
        try {
            MatcherAssert.assertThat(
                "Fork is not created",
                repo.forks().create(organization),
                Matchers.notNullValue()
            );
        } finally {
            RtForksITCase.repos().remove(repo.coordinates());
        }
    }

    @Test
    void retrievesForks() throws IOException {
        final String organization = RtForksITCase.organization();
        final Repo repo = new RepoRule().repo(RtForksITCase.repos());
        try {
            MatcherAssert.assertThat(
                "Created fork is not retrieved",
                repo.forks().iterate("newest"),
                Matchers.contains(repo.forks().create(organization))
            );
        } finally {
            RtForksITCase.repos().remove(repo.coordinates());
        }
    }

    private static String organization() {
        final String organization = System.getProperty(
            "failsafe.github.organization"
        );
        Assumptions.assumeTrue(
            organization != null,
            "Organization must be set for this test"
        );
        return organization;
    }

    private static Repos repos() {
        return GitHubIT.connect().repos();
    }
}
