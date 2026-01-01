/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtGit}.
 * @since 0.8
 */
final class RtGitTest {

    @Test
    void canFetchOwnRepo() {
        final Repo repo = RtGitTest.repo();
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtGit(new FakeRequest(), repo).repo(),
            Matchers.is(repo)
        );
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "git"))
            .when(repo).coordinates();
        return repo;
    }

}
