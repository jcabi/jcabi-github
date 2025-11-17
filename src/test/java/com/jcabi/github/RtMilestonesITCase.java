/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.github.OAuthScope.Scope;
import com.jcabi.http.wire.RetryWire;
import com.jcabi.immutable.ArrayMap;
import java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration case for {@link Milestones}.
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@OAuthScope(Scope.REPO)
public final class RtMilestonesITCase {
    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * Set up test fixtures.
     * @throws Exception If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final Github github = new RtGithub(
            new GithubIT().connect().entry().through(RetryWire.class)
        );
        repos = github.repos();
        repo = new RepoRule().repo(repos);
    }

    /**
     * Tear down test fixtures.
     * @throws Exception If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if (repos != null && repo != null) {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtMilestones can iterate milestones.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesIssues() throws Exception {
        final Milestones milestones = repo.milestones();
        final Milestone milestone = milestones.create(
            RandomStringUtils.randomAlphabetic(Tv.TEN)
        );
        try {
            MatcherAssert.assertThat(
                milestones.iterate(Collections.singletonMap("state", "all")),
                Matchers.hasItem(milestone)
            );
        } finally {
            milestones.remove(milestone.number());
        }
    }

    /**
     * RtMilestones can create a new milestone.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsNewMilestone() throws Exception {
        final Milestones milestones = repo.milestones();
        final Milestone milestone = milestones.create(
            RandomStringUtils.randomAlphabetic(Tv.TEN)
        );
        try {
            MatcherAssert.assertThat(
                milestones.iterate(Collections.singletonMap("state", "all")),
                Matchers.not(Matchers.emptyIterable())
            );
        } finally {
            milestones.remove(milestone.number());
        }
    }

    /**
     * RtMilestones can remove a milestone.
     * @throws Exception if some problem inside
     */
    @Test
    public void deleteMilestone() throws Exception {
        final Milestones milestones = repo.milestones();
        final Milestone milestone = milestones.create("a milestones");
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<>()),
            Matchers.hasItem(milestone)
        );
        milestones.remove(milestone.number());
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<>()),
            Matchers.not(Matchers.hasItem(milestone))
        );
    }
}
