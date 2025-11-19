/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtLimit}.
 * @since 0.6
 */
public final class RtLimitTest {

    @Test
    public void describeAsJson() throws IOException {
        final JsonReadable limit = new RtLimit(
            Mockito.mock(GitHub.class),
            new FakeRequest().withBody(this.body()),
            "core"
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            limit.json().toString(),
            Matchers.equalTo(
                "{\"limit\":5000, \"remaining\":4999, \"reset\":1372700873}"
            )
        );
    }

    @Test(expected = IllegalStateException.class)
    public void throwsWhenResourceIsAbsent() throws IOException {
        final JsonReadable limit = new RtLimit(
            Mockito.mock(GitHub.class),
            new FakeRequest().withBody(this.body()),
            "absent"
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            limit.json().toString(),
            Matchers.equalTo("{}")
        );
    }

    /**
     * Example response from rate API.
     * @return Body string.
     */
    private String body() {
        return new StringBuilder(Tv.HUNDRED)
            .append("{\"resources\":{\"core\":{\"limit\":5000, ")
            .append("\"remaining\":4999, \"reset\":1372700873}, ")
            .append("\"search\":{\"limit\":20, \"remaining\":18, ")
            .append("\"reset\":1372697452}}, \"rate\":{\"limit\":5000, ")
            .append("\"remaining\":4999, \"reset\":1372700873}}")
            .toString();
    }
}
