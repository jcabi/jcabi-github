/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Milestones;
import com.jcabi.github.Repo;
import com.jcabi.immutable.ArrayMap;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test class for MkMilestones.
 * @since 0.1
 */
final class MkMilestonesTest {

    @Test
    void returnsRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Values are not equal", repo, Matchers.is(repo.milestones().repo())
        );
    }

    @Test
    void createsMilestone() throws IOException {
        MatcherAssert.assertThat(
            "Milestone is not created",
            new MkGitHub().randomRepo().milestones()
                .create("test milestone"),
            Matchers.notNullValue()
        );
    }

    @Test
    void createsAnotherMilestone() throws IOException {
        final Milestones milestones = new MkGitHub().randomRepo()
            .milestones();
        milestones.create("test milestone");
        MatcherAssert.assertThat(
            "Second milestone is not created",
            milestones.create("another milestone"),
            Matchers.notNullValue()
        );
    }

    /**
     * This tests that MkMilestones can return a certain MkMilestone, by number.
     */
    @Test
    void getsMilestone() throws IOException {
        final Milestones milestones = new MkGitHub().randomRepo()
            .milestones();
        MatcherAssert.assertThat(
            "Value is null",
            milestones.get(milestones.create("test").number()),
            Matchers.notNullValue()
        );
    }

    /**
     * This tests that MkMilestones can remove a certain MkMilestone, by number.
     */
    @Test
    void removesMilestone() throws IOException {
        final Milestones milestones = new MkGitHub().randomRepo()
            .milestones();
        milestones.remove(milestones.create("testTitle").number());
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            milestones.iterate(new ArrayMap<>()),
            Matchers.iterableWithSize(0)
        );
    }

    /**
     * This tests that the iterate method with params
     * in MkMilestones works fine.
     */
    @Test
    void iteratesMilestones() throws IOException {
        final Milestones milestones = new MkGitHub().randomRepo()
            .milestones();
        milestones.create("testMilestone");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            milestones.iterate(new ArrayMap<>()),
            Matchers.iterableWithSize(1)
        );
    }
}
