/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Milestone;
import com.jcabi.github.Milestones;
import com.jcabi.github.Repo;
import com.jcabi.immutable.ArrayMap;
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
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void returnsRepo() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Repo owner = repo.milestones().repo();
        MatcherAssert.assertThat(repo, Matchers.is(owner));
    }

    /**
     * This tests that MkMilestones can create a MkMilestone.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void createsMilestone() throws Exception {
        final Milestones milestones = new MkGithub().randomRepo()
            .milestones();
        final Milestone milestone = milestones.create("test milestone");
        MatcherAssert.assertThat(milestone, Matchers.notNullValue());
        MatcherAssert.assertThat(
            milestones.create("another milestone"),
            Matchers.notNullValue()
        );
    }

    /**
     * This tests that MkMilestones can return a certain MkMilestone, by number.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void getsMilestone() throws Exception {
        final Milestones milestones = new MkGithub().randomRepo()
            .milestones();
        final Milestone created = milestones.create("test");
        MatcherAssert.assertThat(
            milestones.get(created.number()),
            Matchers.notNullValue()
        );
    }
    /**
     * This tests that MkMilestones can remove a certain MkMilestone, by number.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void removesMilestone() throws Exception {
        final Milestones milestones = new MkGithub().randomRepo()
            .milestones();
        final Milestone created = milestones.create("testTitle");
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<>()),
            Matchers.<Milestone>iterableWithSize(1)
        );
        milestones.remove(created.number());
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<>()),
            Matchers.<Milestone>iterableWithSize(0)
        );
    }
    /**
     * This tests that the iterate(Map<String, String> params)
     * method in MkMilestones works fine.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void iteratesMilestones() throws Exception {
        final Milestones milestones = new MkGithub().randomRepo()
            .milestones();
        milestones.create("testMilestone");
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<>()),
            Matchers.<Milestone>iterableWithSize(1)
        );
    }
}
