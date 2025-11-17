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
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkRepoTest {

    /**
     * Repo can work.
     */
    @Test
    public void works() throws IOException {
        final Repos repos = new MkRepos(new MkStorage.InFile(), "jeff");
        final Repo repo = repos.create(
            new Repos.RepoCreate("test5", false)
        );
        MatcherAssert.assertThat(
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
        MatcherAssert.assertThat(milestones, Matchers.notNullValue());
    }

    /**
     * Repo can fetch its commits.
     *
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
        MatcherAssert.assertThat(repo.commits(), Matchers.notNullValue());
    }

    /**
     * Repo can fetch its branches.
     *
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
        MatcherAssert.assertThat(repo.branches(), Matchers.notNullValue());
    }

    /**
     * Repo can exponse attributes.
     */
    @Test
    public void exposesAttributes() throws IOException {
        final Repo repo = new MkGithub().randomRepo();
        MatcherAssert.assertThat(
            new Repo.Smart(repo).description(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
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
        MatcherAssert.assertThat(repo.stars(), Matchers.notNullValue());
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
        MatcherAssert.assertThat(repo.notifications(), Matchers.notNullValue());
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
        MatcherAssert.assertThat(languages, Matchers.notNullValue());
        MatcherAssert.assertThat(
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
            repo.defaultBranch().name(),
            Matchers.equalTo("master")
        );
    }
}
