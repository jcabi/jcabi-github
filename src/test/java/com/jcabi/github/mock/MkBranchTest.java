/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkBranch}.
 *
 */
public final class MkBranchTest {
    /**
     * MkBranch can fetch its name.
     * @throws IOException If an I/O problem occurs
     */
    @Test
    public void fetchesName() throws IOException {
        final String name = "topic";
        MatcherAssert.assertThat(
            MkBranchTest.branches(new MkGithub().randomRepo())
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
    public void fetchesCommit() throws IOException {
        final String sha = "ad1298cac285d601cd66b37ec8989836d7c6e651";
        MatcherAssert.assertThat(
            MkBranchTest.branches(new MkGithub().randomRepo())
                .create("feature-branch", sha).commit().sha(),
            Matchers.equalTo(sha)
        );
    }

    /**
     * MkBranch can fetch its repo.
     * @throws IOException If an I/O problem occurs
     */
    @Test
    public void fetchesRepo() throws IOException {
        final Repo repo = new MkGithub().randomRepo();
        final Coordinates coords = MkBranchTest.branches(repo)
            .create("test", "sha")
            .repo().coordinates();
        MatcherAssert.assertThat(
            coords.user(),
            Matchers.equalTo(repo.coordinates().user())
        );
        MatcherAssert.assertThat(
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
