/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration case for {@link RtRepos}.
 *
 */
@OAuthScope({ Scope.REPO, Scope.DELETE_REPO })
public class RtReposITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RepoRule rule = new RepoRule();

    /**
     * RtRepos create repository test.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public final void create() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            MatcherAssert.assertThat(repo, Matchers.notNullValue());
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtRepos should fail on creation of two repos with the same name.
     * @throws Exception If some problem inside
     */
    @Test(expected = AssertionError.class)
    public final void failsOnCreationOfTwoRepos() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            repos.create(
                new Repos.RepoCreate(repo.coordinates().repo(), false)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtRepos exists repository test.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public final void exists() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            MatcherAssert.assertThat(
                repos.exists(repo.coordinates()),
                Matchers.equalTo(true)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtRepos create repository test.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public final void createWithOrganization() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = repos.create(
            new Repos.RepoCreate("test", false).withOrganization("myorg")
        );
        try {
            MatcherAssert.assertThat(
                repo.coordinates(),
                Matchers.hasToString("/orgs/myorg/repos/test")
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

}
