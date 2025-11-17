/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtRepoCommit}.
 */
public class RtRepoCommitTest {
    /**
     * RtRepoCommit has proper request URL.
     */
    @Test
    public final void hasProperRequestUrl() {
        final String sha = RandomStringUtils.randomAlphanumeric(50);
        final RtRepoCommit commit = new RtRepoCommit(
            new FakeRequest(), RtRepoCommitTest.repo(), sha
        );
        MatcherAssert.assertThat(
            commit.toString(),
            Matchers.endsWith(
                String.format(
                    "/see-FakeRequest-class/repos/user/repo/commits/%s",
                    sha
                )
            )
        );
    }

    /**
     * Create repository for tests.
     * @return Repository
     */
    private static Repo repo() {
        return new RtGithub().repos()
            .get(new Coordinates.Simple("user", "repo"));
    }

}
