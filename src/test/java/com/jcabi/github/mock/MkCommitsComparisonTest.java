/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.CommitsComparison;
import com.jcabi.github.Coordinates;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkCommitsComparison}.
 * @since 0.1
 */
final class MkCommitsComparisonTest {

    /**
     * MkCommitsComparison can get a repo.
     * @throws IOException if some problem inside
     */
    @Test
    void fetchesRepo() throws IOException {
        final String user = "test_user";
        MatcherAssert.assertThat(
            "Value is null",
            new MkCommitsComparison(
                new MkStorage.InFile(), user,
                new Coordinates.Simple(user, "test_repo")
            ).repo(), Matchers.notNullValue()
        );
    }

    @Test
    void fetchesStatus() throws IOException {
        MatcherAssert.assertThat(
            "Status is absent",
            MkCommitsComparisonTest.comparison().json().getString("status"),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesAheadBy() throws IOException {
        MatcherAssert.assertThat(
            "Number of commits ahead is absent",
            MkCommitsComparisonTest.comparison().json().getInt("ahead_by"),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesCommits() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            new CommitsComparison.Smart(
                MkCommitsComparisonTest.comparison()
            ).commits(),
            Matchers.iterableWithSize(0)
        );
    }

    @Test
    void fetchesCommitsInJson() throws IOException {
        MatcherAssert.assertThat(
            "Commits are absent",
            MkCommitsComparisonTest.comparison().json()
                .getJsonArray("commits"),
            Matchers.notNullValue()
        );
    }

    /**
     * Comparison of two commits.
     * @return Comparison
     * @throws IOException if some problem inside
     */
    private static CommitsComparison comparison() throws IOException {
        return new MkCommitsComparison(
            new MkStorage.InFile(), "test-9",
            new Coordinates.Simple("test_user_A", "test_repo_B")
        );
    }
}
