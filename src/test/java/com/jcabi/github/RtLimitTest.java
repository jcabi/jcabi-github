/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtLimit}.
 * @since 0.6
 */
final class RtLimitTest {

    @Test
    void describeAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtLimit(
                Mockito.mock(GitHub.class),
                new FakeRequest().withBody(RtLimitTest.body()),
                "core"
            ).json().toString(),
            Matchers.equalTo(
                "{\"limit\":5000,\"remaining\":4999,\"reset\":1372700873}"
            )
        );
    }

    @Test
    void throwsWhenResourceIsAbsent() {
        Assertions.assertThrows(
            IllegalStateException.class,
            new RtLimit(
                Mockito.mock(GitHub.class),
                new FakeRequest().withBody(RtLimitTest.body()),
                "absent"
            )::json,
            "Should throw when resource is absent"
        );
    }

    private static String body() {
        return String.join(
            "",
            "{\"resources\":{\"core\":{\"limit\":5000, ",
            "\"remaining\":4999, \"reset\":1372700873}, ",
            "\"search\":{\"limit\":20, \"remaining\":18, ",
            "\"reset\":1372697452}}, \"rate\":{\"limit\":5000, ",
            "\"remaining\":4999, \"reset\":1372700873}}"
        );
    }
}
