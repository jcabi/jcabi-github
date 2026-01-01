/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link RtRepos}.
 * @since 0.5
 */
@OAuthScope({ OAuthScope.Scope.REPO, OAuthScope.Scope.DELETE_REPO })
final class RtReposITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    public final transient RepoRule rule = new RepoRule();

    @Test
    void create() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            MatcherAssert.assertThat(
                "Value is null", repo, Matchers.notNullValue()
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    void failsOnCreationOfTwoRepos() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
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
    void exists() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
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
    void createWithOrganization() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
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
