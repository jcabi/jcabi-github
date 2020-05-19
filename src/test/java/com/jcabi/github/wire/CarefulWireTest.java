/**
 * Copyright (c) 2013-2020, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
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
            .withReason(OK)
            .withHeader(REMAINING_HEADER, "9")
            .withHeader("X-RateLimit-Reset", String.valueOf(reset))
            .through(CarefulWire.class, threshold)
            .fetch();
        final long now = TimeUnit.MILLISECONDS
            .toSeconds(System.currentTimeMillis());
        MatcherAssert.assertThat(now, Matchers.greaterThanOrEqualTo(reset));
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
            .withReason(OK)
            .through(CarefulWire.class, threshold)
            .fetch();
        MatcherAssert.assertThat(
            "Did not crash when X-RateLimit-Remaining header was absent",
            true
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
            .withReason(OK)
            .withHeader(REMAINING_HEADER, "7")
            .through(CarefulWire.class, threshold)
            .fetch();
        MatcherAssert.assertThat(
            "Did not crash when X-RateLimit-Reset header was absent",
            true
        );
    }
}
