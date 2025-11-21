/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtForks}.
 * @since 0.1
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtForksITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RepoRule rule = new RepoRule();

    @Test
    public void retrievesForks() throws IOException {
        final String organization = System.getProperty(
            "failsafe.github.organization"
        );
        Assume.assumeThat(organization, Matchers.notNullValue());
        final Repo repo = this.rule.repo(RtForksITCase.repos());
        try {
            final Fork fork = repo.forks().create(organization);
            MatcherAssert.assertThat(
                "Value is null",
                fork,
                Matchers.notNullValue()
            );
            final Iterable<Fork> forks = repo.forks().iterate("newest");
            MatcherAssert.assertThat(
                "Value is null",
                forks,
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                "Collection is not empty",
                forks,
                Matchers.not(Matchers.emptyIterable())
            );
            MatcherAssert.assertThat(
                "Assertion failed",
                forks,
                Matchers.contains(fork)
            );
        } finally {
            RtForksITCase.repos().remove(repo.coordinates());
        }
    }

    /**
     * Returns github repos.
     * @return GitHub repos.
     */
    private static Repos repos() {
        return GitHubIT.connect().repos();
    }

}
