/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.google.common.collect.Lists;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Language;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Repo}.
 * @since 0.5
 */
final class MkRepoTest {

    @Test
    void works() throws IOException {
        MatcherAssert.assertThat(
            "Assertion failed",
            new MkRepos(new MkStorage.InFile(), "jeff").create(
                new Repos.RepoCreate("test5", false)
            ).coordinates(),
            Matchers.hasToString("jeff/test5")
        );
    }

    /**
     * This tests that the milestones() method in MkRepo is working fine.
     */
    @Test
    void returnsMkMilestones() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkRepos(new MkStorage.InFile(), "jeff").create(
                new Repos.RepoCreate("test1", false)
            ).milestones(),
            Matchers.notNullValue()
        );
    }

    /**
     * Repo can fetch its commits.
     * @throws IOException if some problem inside
     */
    @Test
    void fetchCommits() throws IOException {
        final String user = "testuser";
        MatcherAssert.assertThat(
            "Value is null", new MkRepo(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo")
            ).commits(), Matchers.notNullValue()
        );
    }

    /**
     * Repo can fetch its branches.
     * @throws IOException if some problem inside
     */
    @Test
    void fetchBranches() throws IOException {
        final String user = "testuser";
        MatcherAssert.assertThat(
            "Value is null", new MkRepo(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo")
            ).branches(), Matchers.notNullValue()
        );
    }

    @Test
    void exposesDescription() throws IOException {
        MatcherAssert.assertThat(
            "Description is absent",
            new Repo.Smart(new MkGitHub().randomRepo()).description(),
            Matchers.notNullValue()
        );
    }

    @Test
    void exposesPrivacy() throws IOException {
        MatcherAssert.assertThat(
            "Repo is not private",
            new Repo.Smart(new MkGitHub().randomRepo()).isPrivate(),
            Matchers.is(true)
        );
    }

    /**
     * Repo can return Stars API.
     * @throws IOException if some problem inside
     */
    @Test
    void fetchStars() throws IOException {
        final String user = "testuser2";
        MatcherAssert.assertThat(
            "Value is null", new MkRepo(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo2")
            ).stars(), Matchers.notNullValue()
        );
    }

    /**
     * Repo can return Notifications API.
     * @throws IOException if some problem inside
     */
    @Test
    void fetchNotifications() throws IOException {
        final String user = "testuser3";
        MatcherAssert.assertThat(
            "Value is null", new MkRepo(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo3")
            ).notifications(), Matchers.notNullValue()
        );
    }

    /**
     * Repo can return Languages iterable.
     * @throws IOException if some problem inside
     */
    @Test
    void fetchLanguages() throws IOException {
        MatcherAssert.assertThat(
            "Languages are absent",
            MkRepoTest.languages(),
            Matchers.notNullValue()
        );
    }

    /**
     * Repo can count its own languages.
     * @throws IOException if some problem inside
     */
    @Test
    void countsLanguages() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            Lists.newArrayList(MkRepoTest.languages()),
            Matchers.hasSize(3)
        );
    }

    /**
     * MkRepo can return its default branch.
     * @throws IOException if some problem inside.
     */
    @Test
    void retrievesDefaultBranch() throws IOException {
        final String user = "testuser5";
        MatcherAssert.assertThat(
            "Values are not equal",
            new MkRepo(
                new MkStorage.InFile(),
                user,
                new Coordinates.Simple(user, "testrepo5")
            ).defaultBranch().name(),
            Matchers.equalTo("master")
        );
    }

    private static Iterable<Language> languages() throws IOException {
        final String user = "testuser4";
        return new MkRepo(
            new MkStorage.InFile(),
            user,
            new Coordinates.Simple(user, "testrepo4")
        ).languages();
    }
}
