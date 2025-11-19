/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.google.common.collect.Lists;
import com.jcabi.aspects.Tv;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Language;
import com.jcabi.github.Milestones;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Repo}.
 * @since 0.5
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkRepoTest {

    @Test
    public void works() throws IOException {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = repos.create(
            new Repos.RepoCreate("test5", false)
        );
        MatcherAssert.assertThat(
            "Assertion failed",
            repo.coordinates(),
            Matchers.hasToString("jeff/test5")
        );
    }

    /**
     * This tests that the milestones() method in MkRepo is working fine.
     */
    @Test
    public void returnsMkMilestones() throws IOException {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = repos.create(
            new Repos.RepoCreate("test1", false)
        );
        final Milestones milestones = repo.milestones();
        MatcherAssert.assertThat(
            "Value is null", milestones, Matchers.notNullValue()
        );
    }

    /**
     * Repo can fetch its commits.
     * @throws IOException if some problem inside
     */
    @Test
    public void fetchCommits() throws IOException {
        final String user = "testuser";
        final Repo repo = new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo")
        );
        MatcherAssert.assertThat(
            "Value is null", repo.commits(), Matchers.notNullValue()
        );
    }

    /**
     * Repo can fetch its branches.
     * @throws IOException if some problem inside
     */
    @Test
    public void fetchBranches() throws IOException {
        final String user = "testuser";
        final Repo repo = new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo")
        );
        MatcherAssert.assertThat(
            "Value is null", repo.branches(), Matchers.notNullValue()
        );
    }

    @Test
    public void exposesAttributes() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Value is null",
            new Repo.Smart(repo).description(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            new Repo.Smart(repo).isPrivate(),
            Matchers.is(true)
        );
    }

    /**
     * Repo can return Stars API.
     * @throws IOException if some problem inside
     */
    @Test
    public void fetchStars() throws IOException {
        final String user = "testuser2";
        final Repo repo = new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo2")
        );
        MatcherAssert.assertThat(
            "Value is null", repo.stars(), Matchers.notNullValue()
        );
    }

    /**
     * Repo can return Notifications API.
     * @throws IOException if some problem inside
     */
    @Test
    public void fetchNotifications() throws IOException {
        final String user = "testuser3";
        final Repo repo = new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo3")
        );
        MatcherAssert.assertThat(
            "Value is null", repo.notifications(), Matchers.notNullValue()
        );
    }

    /**
     * Repo can return Languages iterable.
     * @throws IOException if some problem inside
     */
    @Test
    public void fetchLanguages() throws IOException {
        final String user = "testuser4";
        final Repo repo = new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo4")
        );
        final Iterable<Language> languages = repo.languages();
        MatcherAssert.assertThat(
            "Value is null", languages, Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            Lists.newArrayList(languages),
            Matchers.hasSize(Tv.THREE)
        );
    }

    /**
     * MkRepo can return its default branch.
     * @throws IOException if some problem inside.
     */
    @Test
    public void retrievesDefaultBranch() throws IOException {
        final String user = "testuser5";
        final Repo repo = new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo5")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            repo.defaultBranch().name(),
            Matchers.equalTo("master")
        );
    }
}
