/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.wire;

import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link CarefulWire}.
 *
 */
public final class CarefulWireTest {
    /**
     * HTTP 200 status reason.
     */
    private static final String OK = "OK";
    /**
     * Name of GitHub's number-of-requests-remaining rate limit header.
     */
    private static final String REMAINING_HEADER = "X-RateLimit-Remaining";

    /**
     * CarefulWire can wait until the limit reset.
     * @throws IOException If some problem inside
     */
    @Test
    public void waitUntilReset() throws IOException {
        final int threshold = 10;
        // @checkstyle MagicNumber (2 lines)
        final long reset = TimeUnit.MILLISECONDS
            .toSeconds(System.currentTimeMillis()) + 5L;
        new FakeRequest()
            .withStatus(HttpURLConnection.HTTP_OK)
            .withReason(CarefulWireTest.OK)
            .withHeader(CarefulWireTest.REMAINING_HEADER, "9")
            .withHeader("X-RateLimit-Reset", String.valueOf(reset))
            .through(CarefulWire.class, threshold)
            .fetch();
        final long now = TimeUnit.MILLISECONDS
            .toSeconds(System.currentTimeMillis());
        MatcherAssert.assertThat(
            "Value is not greater than expected",now, Matchers.greaterThanOrEqualTo(reset));
    }

    /**
     * CarefulWire can tolerate the lack the X-RateLimit-Remaining header.
     * @throws IOException If some problem inside
     */
    @Test
    public void tolerateMissingRateLimitRemainingHeader() throws IOException {
        final int threshold = 10;
        // @checkstyle MagicNumber (1 lines)
        new FakeRequest()
            .withStatus(HttpURLConnection.HTTP_OK)
            .withReason(CarefulWireTest.OK)
            .through(CarefulWire.class, threshold)
            .fetch();
        MatcherAssert.assertThat(
            "Did not crash when X-RateLimit-Remaining header was absent",
            true,
            Matchers.is(true)
        );
    }

    /**
     * CarefulWire can tolerate the lack the X-RateLimit-Reset header.
     * @throws IOException If some problem inside
     */
    @Test
    public void tolerateMissingRateLimitResetHeader() throws IOException {
        final int threshold = 8;
        // @checkstyle MagicNumber (1 lines)
        new FakeRequest()
            .withStatus(HttpURLConnection.HTTP_OK)
            .withReason(CarefulWireTest.OK)
            .withHeader(CarefulWireTest.REMAINING_HEADER, "7")
            .through(CarefulWire.class, threshold)
            .fetch();
        MatcherAssert.assertThat(
            "Did not crash when X-RateLimit-Reset header was absent",
            true,
            Matchers.is(true)
        );
    }
}
