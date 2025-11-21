/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.http.wire.RetryWire;
import com.jcabi.immutable.ArrayMap;
import java.io.IOException;
import java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Integration case for {@link Milestones}.
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
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
     */
    @BeforeAll
    public void setUp() throws IOException {
        final GitHub github = new RtGitHub(
            GitHubIT.connect().entry().through(RetryWire.class)
        );
        RtMilestonesITCase.repos = github.repos();
        RtMilestonesITCase.repo = new RepoRule().repo(RtMilestonesITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    public void tearDown() throws IOException {
        if (RtMilestonesITCase.repos != null && RtMilestonesITCase.repo != null) {
            RtMilestonesITCase.repos.remove(RtMilestonesITCase.repo.coordinates());
        }
    }

    @Test
    public void iteratesIssues() throws IOException {
        final Milestones milestones = RtMilestonesITCase.repo.milestones();
        final Milestone milestone = milestones.create(
            RandomStringUtils.randomAlphabetic(Tv.TEN)
        );
        try {
            MatcherAssert.assertThat(
                "Collection does not contain expected item",
                milestones.iterate(Collections.singletonMap("state", "all")),
                Matchers.hasItem(milestone)
            );
        } finally {
            milestones.remove(milestone.number());
        }
    }

    @Test
    public void createsNewMilestone() throws IOException {
        final Milestones milestones = RtMilestonesITCase.repo.milestones();
        final Milestone milestone = milestones.create(
            RandomStringUtils.randomAlphabetic(Tv.TEN)
        );
        try {
            MatcherAssert.assertThat(
                "Collection is not empty",
                milestones.iterate(Collections.singletonMap("state", "all")),
                Matchers.not(Matchers.emptyIterable())
            );
        } finally {
            milestones.remove(milestone.number());
        }
    }

    @Test
    public void deleteMilestone() throws IOException {
        final Milestones milestones = RtMilestonesITCase.repo.milestones();
        final Milestone milestone = milestones.create("a milestones");
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            milestones.iterate(new ArrayMap<>()),
            Matchers.hasItem(milestone)
        );
        milestones.remove(milestone.number());
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            milestones.iterate(new ArrayMap<>()),
            Matchers.not(Matchers.hasItem(milestone))
        );
    }
}
