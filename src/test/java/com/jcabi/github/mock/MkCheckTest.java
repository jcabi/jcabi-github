/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Check;
import com.jcabi.github.Pull;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkCheck}.
 * @since 1.6.1
 */
final class MkCheckTest {

    @Test
    void createsSuccessfulCheck() throws IOException {
        MatcherAssert.assertThat(
            "Check is not successful",
            ((MkChecks) MkCheckTest.pull().checks())
                .create(Check.Status.COMPLETED, Check.Conclusion.SUCCESS)
                .successful(),
            Matchers.is(true)
        );
    }

    @Test
    void createsFailedCheck() throws IOException {
        MatcherAssert.assertThat(
            "Check is not failed",
            ((MkChecks) MkCheckTest.pull().checks())
                .create(Check.Status.COMPLETED, Check.Conclusion.FAILURE)
                .successful(),
            Matchers.is(false)
        );
    }

    @Test
    void createsSkippedCheck() throws IOException {
        MatcherAssert.assertThat(
            "Check is not skipped",
            ((MkChecks) MkCheckTest.pull().checks())
                .create(Check.Status.COMPLETED, Check.Conclusion.SKIPPED)
                .skipped(),
            Matchers.is(true)
        );
    }

    /**
     * Pull request to make checks for.
     * @return Pull request
     * @throws IOException If some problem with I/O
     */
    private static Pull pull() throws IOException {
        return new MkGitHub()
            .randomRepo()
            .pulls()
            .create("Test PR", "abcdea8", "abcdea9");
    }
}
