/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkBranch}.
 * @since 0.1
 */
final class MkBranchTest {
    /**
     * MkBranch can fetch its name.
     * @throws IOException If an I/O problem occurs
     */
    @Test
    void fetchesName() throws IOException {
        final String name = "topic";
        MatcherAssert.assertThat(
            "Values are not equal",
            MkBranchTest.branches(new MkGitHub().randomRepo())
                .create(name, "f8dfc75138a2b57859b65cfc45239978081b8de4")
                .name(),
            Matchers.equalTo(name)
        );
    }

    /**
     * MkBranch can fetch its commit.
     * @throws IOException If an I/O problem occurs
     */
    @Test
    void fetchesCommit() throws IOException {
        final String sha = "ad1298cac285d601cd66b37ec8989836d7c6e651";
        MatcherAssert.assertThat(
            "Values are not equal",
            MkBranchTest.branches(new MkGitHub().randomRepo())
                .create("feature-branch", sha).commit().sha(),
            Matchers.equalTo(sha)
        );
    }

    /**
     * MkBranch can fetch its repo.
     * @throws IOException If an I/O problem occurs
     */
    @Test
    void fetchesRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Coordinates coords = MkBranchTest.branches(repo)
            .create("test", "sha")
            .repo().coordinates();
        MatcherAssert.assertThat(
            "Values are not equal",
            coords.user(),
            Matchers.equalTo(repo.coordinates().user())
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            coords.repo(),
            Matchers.equalTo(repo.coordinates().repo())
        );
    }

    /**
     * MkBranches for MkBranch creation.
     * @param repo Repository to get MkBranches of
     * @return MkBranches
     */
    private static MkBranches branches(final Repo repo) {
        return (MkBranches) repo.branches();
    }
}
