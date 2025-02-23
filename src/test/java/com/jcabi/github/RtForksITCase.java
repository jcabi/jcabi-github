/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtForks}.
 *
 */
@OAuthScope(Scope.REPO)
public class RtForksITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RepoRule rule = new RepoRule();

    /**
     * RtForks should be able to iterate its forks.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public final void retrievesForks() throws Exception {
        final String organization = System.getProperty(
            "failsafe.github.organization"
        );
        Assume.assumeThat(organization, Matchers.notNullValue());
        final Repo repo = this.rule.repo(RtForksITCase.repos());
        try {
            final Fork fork = repo.forks().create(organization);
            MatcherAssert.assertThat(fork, Matchers.notNullValue());
            final Iterable<Fork> forks = repo.forks().iterate("newest");
            MatcherAssert.assertThat(forks, Matchers.notNullValue());
            MatcherAssert.assertThat(
                forks,
                Matchers.not(Matchers.emptyIterable())
            );
            MatcherAssert.assertThat(
                forks,
                Matchers.contains(fork)
            );
        } finally {
            RtForksITCase.repos().remove(repo.coordinates());
        }
    }

    /**
     * Returns github repos.
     * @return Github repos.
     */
    private static Repos repos() {
        return new GithubIT().connect().repos();
    }

}
