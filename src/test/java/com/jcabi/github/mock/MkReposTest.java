/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test case for {@link MkRepos}.
 * @since 0.5
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkReposTest {

    /**
     * Rule for checking thrown exception.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    @SuppressWarnings("deprecation")
    public transient ExpectedException thrown = ExpectedException.none();

    /**
     * MkRepos can create a repo.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsRepository() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = MkReposTest.repo(repos, "test", "test repo");
        MatcherAssert.assertThat(
            "Assertion failed",
            repo.coordinates(),
            Matchers.hasToString("jeff/test")
        );
    }

    /**
     * MkRepos can create a repo with organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsRepositoryWithOrganization() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "john");
        final Repo repo = MkReposTest.repoWithOrg(repos, "test", "myorg");
        MatcherAssert.assertThat(
            "Assertion failed",
            repo.coordinates(),
            Matchers.hasToString("/orgs/myorg/repos/test")
        );
    }

    /**
     * MkRepos can create a repo with details.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsRepositoryWithDetails() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = MkReposTest.repo(repos, "hello", "my test repo");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new Repo.Smart(repo).description(),
            Matchers.startsWith("my test")
        );
    }

    /**
     * MkRepos can remove an existing repo.
     * @throws Exception If some problem inside
     */
    @Test
    public void removesRepo() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = MkReposTest.repo(repos, "remove-me", "remove repo");
        MatcherAssert.assertThat(
            "Value is null",
            repos.get(repo.coordinates()),
            Matchers.notNullValue()
        );
    }

    /**
     * MkRepos can iterate repos.
     * @throws Exception if there is any error
     */
    @Test
    public void iterateRepos() throws Exception {
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
    public void createsPrivateRepo() throws IOException {
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
     * MkRepos can check for existing repos.
     * @throws Exception If some problem inside
     */
    @Test
    public void existsRepo() throws Exception {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "john");
        final Repo repo = MkReposTest.repo(repos, "exist", "existing repo");
        MatcherAssert.assertThat(
            "Values are not equal",
            repos.exists(repo.coordinates()),
            Matchers.is(true)
        );
    }

    /**
     * Create and return Repo to test.
     * @param repos Repos
     * @param name Repo name
     * @param desc Repo description
     * @return Repo
     */
    private static Repo repo(final Repos repos, final String name,
        final String desc) throws IOException {
        return repos.create(
            new Repos.RepoCreate(name, false).withDescription(desc)
        );
    }

    /**
     * Create and return Repo to test.
     * @param repos Repos
     * @param name Repo name
     * @param org Repo organization
     * @return Repo
     */
    private static Repo repoWithOrg(final Repos repos, final String name,
        final String org) throws IOException {
        return repos.create(
            new Repos.RepoCreate(name, false).withOrganization(org)
        );
    }
}
