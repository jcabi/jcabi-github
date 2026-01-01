/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.wire.RetryWire;
import com.jcabi.immutable.ArrayMap;
import java.io.IOException;
import java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link Milestones}.
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtMilestonesITCase {
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
    static void setUp() throws IOException {
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
    static void tearDown() throws IOException {
        if (RtMilestonesITCase.repos != null && RtMilestonesITCase.repo != null) {
            RtMilestonesITCase.repos.remove(RtMilestonesITCase.repo.coordinates());
        }
    }

    @Test
    void iteratesIssues() throws IOException {
        final Milestones milestones = RtMilestonesITCase.repo.milestones();
        final Milestone milestone = milestones.create(
            RandomStringUtils.secure().nextAlphabetic(10)
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
    void createsNewMilestone() throws IOException {
        final Milestones milestones = RtMilestonesITCase.repo.milestones();
        final Milestone milestone = milestones.create(
            RandomStringUtils.secure().nextAlphabetic(10)
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
    void deleteMilestone() throws IOException {
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
