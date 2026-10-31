/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Hidden}.
 *
 * @since 1.0
 */
final class HiddenTest {

    @Test
    void printsStatusOfOriginalResponse() throws Exception {
        MatcherAssert.assertThat(
            "Status of the original response is not printed",
            new Hidden(new FakeRequest().withStatus(422).fetch()).toString(),
            Matchers.containsString("422")
        );
    }
}
