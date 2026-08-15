/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Integration test case for {@link RtStars}.
 * @since 0.8
 */
@OAuthScope({ OAuthScope.Scope.REPO, OAuthScope.Scope.USER })
final class RtStarsITCase {

    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * Set up tests.
     * @throws IOException If some errors occurred.
     */
    @BeforeAll
    static void setUp() throws IOException {
        final GitHub github = GitHubIT.connect();
        RtStarsITCase.repos = github.repos();
        RtStarsITCase.repo = new RepoRule().repo(RtStarsITCase.repos);
    }

    /**
     * Set up tests.
     * @throws IOException If some errors occurred.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtStarsITCase.repos != null && RtStarsITCase.repo != null) {
            RtStarsITCase.repos.remove(RtStarsITCase.repo.coordinates());
        }
    }

    @Test
    void findsRepoNotStarred() throws IOException {
        MatcherAssert.assertThat(
            "Fresh repo is starred",
            RtStarsITCase.repo.stars().starred(),
            Matchers.equalTo(false)
        );
    }

    @Test
    void starsRepo() throws IOException {
        RtStarsITCase.repo.stars().star();
        try {
            MatcherAssert.assertThat(
                "Starred repo is not starred",
                RtStarsITCase.repo.stars().starred(),
                Matchers.equalTo(true)
            );
        } finally {
            RtStarsITCase.repo.stars().unstar();
        }
    }

    @Test
    void unstarsRepo() throws IOException {
        RtStarsITCase.repo.stars().star();
        RtStarsITCase.repo.stars().unstar();
        MatcherAssert.assertThat(
            "Unstarred repo is still starred",
            RtStarsITCase.repo.stars().starred(),
            Matchers.equalTo(false)
        );
    }
}
