/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtBranch}.
 * @since 0.8
 */
final class RtBranchTest {

    /**
     * Test branch name.
     */
    private static final String BRANCH_NAME = "topic";

    /**
     * Commit SHA for test branch.
     */
    private static final String SHA = "b9b0b8a357bbf70f7c9f8ef17160ee31feb508a9";

    @Test
    void fetchesCommitSha() throws IOException {
        MatcherAssert.assertThat(
            "Commit has a wrong SHA",
            RtBranchTest.newBranch(new MkGitHub().randomRepo()).commit().sha(),
            Matchers.equalTo(RtBranchTest.SHA)
        );
    }

    @Test
    void fetchesCommitRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Commit belongs to a wrong repo",
            RtBranchTest.newBranch(repo).commit().repo().coordinates(),
            Matchers.equalTo(repo.coordinates())
        );
    }

    @Test
    void fetchesName() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            RtBranchTest.newBranch(new MkGitHub().randomRepo()).name(),
            Matchers.equalTo(RtBranchTest.BRANCH_NAME)
        );
    }

    @Test
    void fetchesRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Branch belongs to a wrong repo",
            RtBranchTest.newBranch(repo).repo().coordinates(),
            Matchers.equalTo(repo.coordinates())
        );
    }

    /**
     * RtBranch for testing.
     * @param repo Repository to create the branch in
     * @return The RtBranch
     */
    private static Branch newBranch(final Repo repo) {
        return new RtBranch(
            new FakeRequest(),
            repo,
            RtBranchTest.BRANCH_NAME,
            RtBranchTest.SHA
        );
    }
}
