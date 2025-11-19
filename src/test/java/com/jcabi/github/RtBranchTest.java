/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtBranch}.
 * @since 0.8
 */
public final class RtBranchTest {
    /**
     * Test branch name.
     */
    private static final String BRANCH_NAME = "topic";

    /**
     * Commit SHA for test branch.
     * @checkstyle LineLengthCheck (2 lines)
     */
    private static final String SHA = "b9b0b8a357bbf70f7c9f8ef17160ee31feb508a9";

    @Test
    public void fetchesCommit() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Commit commit = RtBranchTest.newBranch(repo).commit();
        MatcherAssert.assertThat(
            "Values are not equal",commit.sha(), Matchers.equalTo(RtBranchTest.SHA));
        final Coordinates coords = commit.repo().coordinates();
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

    @Test
    public void fetchesName() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            RtBranchTest.newBranch(new MkGitHub().randomRepo()).name(),
            Matchers.equalTo(RtBranchTest.BRANCH_NAME)
        );
    }

    @Test
    public void fetchesRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Coordinates coords = RtBranchTest.newBranch(repo)
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
     * RtBranch for testing.
     * @param repo Repository to create the branch in
     * @return The RtBranch.
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
