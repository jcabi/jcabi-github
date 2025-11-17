/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Milestone;
import com.jcabi.github.Milestones;
import com.jcabi.github.Repo;
import com.jcabi.immutable.ArrayMap;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test class for MkMilestones.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkMilestonesTest {

    /**
     * This tests that MkMilestones can return its owner repo.
     */
    @Test
    public void returnsRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Repo owner = repo.milestones().repo();
        MatcherAssert.assertThat(
            "Values are not equal",repo, Matchers.is(owner));
    }

    /**
     * This tests that MkMilestones can create a MkMilestone.
     */
    @Test
    public void createsMilestone() throws IOException {
        final Milestones milestones = new MkGitHub().randomRepo()
            .milestones();
        final Milestone milestone = milestones.create("test milestone");
        MatcherAssert.assertThat(
            "Value is null",milestone, Matchers.notNullValue());
        MatcherAssert.assertThat(
            "Value is null",
            milestones.create("another milestone"),
            Matchers.notNullValue()
        );
    }

    /**
     * This tests that MkMilestones can return a certain MkMilestone, by number.
     */
    @Test
    public void getsMilestone() throws IOException {
        final Milestones milestones = new MkGitHub().randomRepo()
            .milestones();
        final Milestone created = milestones.create("test");
        MatcherAssert.assertThat(
            "Value is null",
            milestones.get(created.number()),
            Matchers.notNullValue()
        );
    }
    /**
     * This tests that MkMilestones can remove a certain MkMilestone, by number.
     */

    @Test
    public void removesMilestone() throws IOException {
        final Milestones milestones = new MkGitHub().randomRepo()
            .milestones();
        final Milestone created = milestones.create("testTitle");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            milestones.iterate(new ArrayMap<>()),
            Matchers.iterableWithSize(1)
        );
        milestones.remove(created.number());
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            milestones.iterate(new ArrayMap<>()),
            Matchers.iterableWithSize(0)
        );
    }
    /**
     * This tests that the iterate(Map<String, String> params)
     * method in MkMilestones works fine.
     */

    @Test
    public void iteratesMilestones() throws IOException {
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
