/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkRepos}.
 * @since 0.5
 */
final class MkReposTest {

    /**
     * MkRepos can create a repo.
     * @throws Exception If some problem inside
     */
    @Test
    void createsRepository() throws Exception {
        MatcherAssert.assertThat(
            "Assertion failed",
            MkReposTest.repo(
                new MkRepos(new MkStorage.InFile(), "jeff"), "test", "test repo"
            ).coordinates(),
            Matchers.hasToString("jeff/test")
        );
    }

    /**
     * MkRepos can create a repo with organization.
     * @throws Exception If some problem inside
     */
    @Test
    void createsRepositoryWithOrganization() throws Exception {
        MatcherAssert.assertThat(
            "Assertion failed",
            MkReposTest.repoWithOrg(
                new MkRepos(new MkStorage.InFile(), "john"), "test", "myorg"
            ).coordinates(),
            Matchers.hasToString("/orgs/myorg/repos/test")
        );
    }

    /**
     * MkRepos can create a repo with details.
     * @throws Exception If some problem inside
     */
    @Test
    void createsRepositoryWithDetails() throws Exception {
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new Repo.Smart(
                MkReposTest.repo(
                    new MkRepos(new MkStorage.InFile(), "jeff"),
                    "hello",
                    "my test repo"
                )
            ).description(),
            Matchers.startsWith("my test")
        );
    }

    /**
     * MkRepos can remove an existing repo.
     * @throws Exception If some problem inside
     */
    @Test
    void removesRepo() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        MatcherAssert.assertThat(
            "Value is null",
            repos.get(MkReposTest.repo(repos, "remove-me", "remove repo").coordinates()),
            Matchers.notNullValue()
        );
    }

    /**
     * MkRepos can iterate repos.
     * @throws Exception if there is any error
     */
    @Test
    void iterateRepos() throws Exception {
        final String since = "1";
        final Repos repos = new MkRepos(new MkStorage.InFile(), "tom");
        MkReposTest.repo(repos, since, "repo 1");
        MkReposTest.repo(repos, "2", "repo 2");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            repos.iterate(since),
            Matchers.iterableWithSize(2)
        );
    }

    @Test
    void createsPrivateRepo() throws IOException {
        final boolean priv = true;
        MatcherAssert.assertThat(
            "Values are not equal",
            new Repo.Smart(
                new MkGitHub().repos().create(
                    new Repos.RepoCreate("test", priv)
                )
            ).isPrivate(),
            Matchers.is(priv)
        );
    }

    /**
     * MkRepo's JSON contains an "owner" object with the login,
     * matching the format of the real GitHub API.
     * @throws Exception If some problem inside
     */
    @Test
    void jsonContainsOwner() throws Exception {
        MatcherAssert.assertThat(
            "Repo JSON should contain an 'owner' object",
            MkReposTest.owned().containsKey("owner"),
            Matchers.is(true)
        );
    }

    /**
     * MkRepo's JSON contains the login of its owner.
     * @throws Exception If some problem inside
     */
    @Test
    void jsonContainsOwnerWithLogin() throws Exception {
        MatcherAssert.assertThat(
            "owner.login should match the user that created the repo",
            MkReposTest.owned().getJsonObject("owner").getString("login"),
            Matchers.is("amihaiemil")
        );
    }

    /**
     * MkRepos can check for existing repos.
     * @throws Exception If some problem inside
     */
    @Test
    void existsRepo() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "john");
        MatcherAssert.assertThat(
            "Values are not equal",
            repos.exists(MkReposTest.repo(repos, "exist", "existing repo").coordinates()),
            Matchers.is(true)
        );
    }

    private static JsonObject owned() throws IOException {
        return MkReposTest.repo(
            new MkRepos(new MkStorage.InFile(), "amihaiemil"),
            "test",
            "owner test"
        ).json();
    }

    private static Repo repo(final Repos repos, final String name,
        final String desc) throws IOException {
        return repos.create(
            new Repos.RepoCreate(name, false).withDescription(desc)
        );
    }

    private static Repo repoWithOrg(final Repos repos, final String name,
        final String org) throws IOException {
        return repos.create(
            new Repos.RepoCreate(name, false).withOrganization(org)
        );
    }
}
