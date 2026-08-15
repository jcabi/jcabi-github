/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtCheck}.
 * @since 1.5.0
 */
final class RtCheckTest {

    @Test
    void checksSuccessfulState() {
        MatcherAssert.assertThat(
            "Completed+success check must be successful",
            new RtCheck(
                Check.Status.COMPLETED,
                Check.Conclusion.SUCCESS
            ).successful(),
            Matchers.is(true)
        );
    }

    @Test
    void checksNotSuccessfulStateIfInProgress() {
        MatcherAssert.assertThat(
            "In-progress check must not be successful",
            new RtCheck(
                Check.Status.IN_PROGRESS,
                Check.Conclusion.SUCCESS
            ).successful(),
            Matchers.is(false)
        );
    }

    @Test
    void checksNotSuccessfulState() {
        MatcherAssert.assertThat(
            "Cancelled check must not be successful",
            new RtCheck(
                Check.Status.COMPLETED,
                Check.Conclusion.CANCELLED
            ).successful(),
            Matchers.is(false)
        );
    }

    @Test
    void checksSkippedState() {
        MatcherAssert.assertThat(
            "Completed+skipped check must be skipped",
            new RtCheck(
                Check.Status.COMPLETED,
                Check.Conclusion.SKIPPED
            ).skipped(),
            Matchers.is(true)
        );
    }

    @Test
    void checksNotSkippedStateIfInProgress() {
        MatcherAssert.assertThat(
            "In-progress check must not be skipped",
            new RtCheck(
                Check.Status.IN_PROGRESS,
                Check.Conclusion.SKIPPED
            ).skipped(),
            Matchers.is(false)
        );
    }

    @Test
    void checksNotSkippedStateIfSuccess() {
        MatcherAssert.assertThat(
            "Successful check must not be skipped",
            new RtCheck(
                Check.Status.COMPLETED,
                Check.Conclusion.SUCCESS
            ).skipped(),
            Matchers.is(false)
        );
    }

    @Test
    void createsWithUnexistingStatus() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new RtCheck("unexisting", "success").successful(),
            "Unexisting status is not rejected"
        );
    }

    @Test
    void createsWithUnexistingConclusion() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new RtCheck("completed", "unexist").successful(),
            "Unexisting conclusion is not rejected"
        );
    }
}
