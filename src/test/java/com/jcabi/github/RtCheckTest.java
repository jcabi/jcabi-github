/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link RtCheck}.
 *
 * @since 1.5.0
 */
public final class RtCheckTest {

    /**
     * RtCheck can check successful state.
     */
    @Test
    public void checksSuccessfulState() {
        MatcherAssert.assertThat(
            new RtCheck(
                Check.Status.COMPLETED,
                Check.Conclusion.SUCCESS
            ).successful(),
            Matchers.is(true)
        );
    }

    /**
     * RtCheck can check not successful state if in progress.
     */
    @Test
    public void checksNotSuccessfulStateIfInProgress() {
        MatcherAssert.assertThat(
            new RtCheck(
                Check.Status.IN_PROGRESS,
                Check.Conclusion.SUCCESS
            ).successful(),
            Matchers.is(false)
        );
    }

    /**
     * RtCheck can check not successful state if cancelled.
     */
    @Test
    public void checksNotSuccessfulState() {
        MatcherAssert.assertThat(
            new RtCheck(
                Check.Status.COMPLETED,
                Check.Conclusion.CANCELLED
            ).successful(),
            Matchers.is(false)
        );
    }

    /**
     * Can not create RtCheck with unexisting status.
     */
    @Test
    public void createsWithUnexistingStatus() {
        Assert.assertThrows(
            IllegalArgumentException.class,
            () -> new RtCheck(
                "unexisting",
                "success"
            ).successful()
        );
    }

    /**
     * Can not create RtCheck with unexisting conclusion.
     */
    @Test
    public void createsWithUnexistingConclusion() {
        Assert.assertThrows(
            IllegalArgumentException.class,
            () -> new RtCheck(
                "completed",
                "unexist"
            ).successful()
        );
    }
}
