/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration case for {@link RtRepos}.
 */
@OAuthScope({ OAuthScope.Scope.REPO, OAuthScope.Scope.DELETE_REPO })
public final class RtReposITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RepoRule rule = new RepoRule();

    @Test
    public final void create() throws IOException {
        final Repos repos = new GitHubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            MatcherAssert.assertThat(
                "Value is null", repo, Matchers.notNullValue());
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test(expected = AssertionError.class)
    public final void failsOnCreationOfTwoRepos() throws IOException {
        final Repos repos = new GitHubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            repos.create(
                new Repos.RepoCreate(repo.coordinates().repo(), false)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    public final void exists() throws IOException {
        final Repos repos = new GitHubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            MatcherAssert.assertThat(
                "Values are not equal",
                repos.exists(repo.coordinates()),
                Matchers.equalTo(true)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    public final void createWithOrganization() throws IOException {
        final Repos repos = new GitHubIT().connect().repos();
        final Repo repo = repos.create(
            new Repos.RepoCreate("test", false).withOrganization("myorg")
        );
        try {
            MatcherAssert.assertThat(
                "Assertion failed",
                repo.coordinates(),
                Matchers.hasToString("/orgs/myorg/repos/test")
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

}
