/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
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
@OAuthScope({ Scope.REPO, Scope.USER })
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
        final Github github = new GithubIT().connect();
        repos = github.repos();
        repo = new RepoRule().repo(repos);
    }

    /**
     * Set up tests.
     * @throws IOException If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws IOException  {
        if (repos != null && repo != null) {
            repos.remove(repo.coordinates());
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
            repo.stars().starred(),
            Matchers.equalTo(false)
        );
        repo.stars().star();
        MatcherAssert.assertThat(
            repo.stars().starred(),
            Matchers.equalTo(true)
        );
        repo.stars().unstar();
        MatcherAssert.assertThat(
            repo.stars().starred(),
            Matchers.equalTo(false)
        );
    }
}
