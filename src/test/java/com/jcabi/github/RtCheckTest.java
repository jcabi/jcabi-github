/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtCheck}.
 *
 * @since 1.5.0
 */
public final class RtCheckTest {

    @Test
    public void checksSuccessfulState() {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtCheck(
                Check.Status.COMPLETED,
                Check.Conclusion.SUCCESS
            ).successful(),
            Matchers.is(true)
        );
    }

    @Test
    public void checksNotSuccessfulStateIfInProgress() {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtCheck(
                Check.Status.IN_PROGRESS,
                Check.Conclusion.SUCCESS
            ).successful(),
            Matchers.is(false)
        );
    }

    @Test
    public void checksNotSuccessfulState() {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtCheck(
                Check.Status.COMPLETED,
                Check.Conclusion.CANCELLED
            ).successful(),
            Matchers.is(false)
        );
    }

    @Test
    public void createsWithUnexistingStatus() {
        try {
            new RtCheck(
                "unexisting",
                "success"
            ).successful();
            MatcherAssert.assertThat(
                "IllegalArgumentException was expected",
                false,
                Matchers.is(true)
            );
        } catch (final IllegalArgumentException ex) {
            MatcherAssert.assertThat(
                "Exception was thrown as expected",
                ex,
                Matchers.notNullValue()
            );
        }
    }

    @Test
    public void createsWithUnexistingConclusion() {
        try {
            new RtCheck(
                "completed",
                "unexist"
            ).successful();
            MatcherAssert.assertThat(
                "IllegalArgumentException was expected",
                false,
                Matchers.is(true)
            );
        } catch (final IllegalArgumentException ex) {
            MatcherAssert.assertThat(
                "Exception was thrown as expected",
                ex,
                Matchers.notNullValue()
            );
        }
    }
}
