/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test case for {@link RtStars}.
 *
 */
@OAuthScope({ OAuthScope.Scope.REPO, OAuthScope.Scope.USER })
public final class RtStarsITCase {
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
    @BeforeClass
    public static void setUp() throws IOException  {
        final GitHub github = new GitHubIT().connect();
        RtStarsITCase.repos = github.repos();
        RtStarsITCase.repo = new RepoRule().repo(RtStarsITCase.repos);
    }

    /**
     * Set up tests.
     * @throws IOException If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws IOException  {
        if (RtStarsITCase.repos != null && RtStarsITCase.repo != null) {
            RtStarsITCase.repos.remove(RtStarsITCase.repo.coordinates());
        }
    }

    /**
     * RtStars can star, unstar and check whether the github repository is
     * starred.
     * @throws IOException If some errors occurred.
     */
    @Test
    public void starsUnstarsChecksStar() throws IOException {
        MatcherAssert.assertThat(
            RtStarsITCase.repo.stars().starred(),
            Matchers.equalTo(false)
        );
        RtStarsITCase.repo.stars().star();
        MatcherAssert.assertThat(
            RtStarsITCase.repo.stars().starred(),
            Matchers.equalTo(true)
        );
        RtStarsITCase.repo.stars().unstar();
        MatcherAssert.assertThat(
            RtStarsITCase.repo.stars().starred(),
            Matchers.equalTo(false)
        );
    }
}
