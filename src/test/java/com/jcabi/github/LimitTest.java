/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.Limit.Throttled;
import com.jcabi.http.request.FakeRequest;
import java.util.Date;
import jakarta.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Limit}.
 *
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
public final class LimitTest {

    /**
     * Limit can throw exception when resource is absent.
     *
     * @throws Exception if some problem inside
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenResourceIsAbsent() throws Exception {
        final Limit limit = Mockito.mock(Limit.class);
        final Throttled throttled = new Throttled(limit, 23);
        Mockito.when(limit.json()).thenReturn(
            Json.createObjectBuilder().add("absent", "absentValue").build()
        );
        throttled.json();
    }

    /**
     * Limit reset() method properly converts time.
     * GitHub reset property is in seconds, but java.util.Date
     * constructor assumes miliseconds.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void timeIsCreatedForReset() throws Exception {
        // @checkstyle MagicNumberCheck (21 lines)
        final RtLimit limit = new RtLimit(
            Mockito.mock(Github.class),
            new FakeRequest().withBody(
                Json.createObjectBuilder().add(
                    "rate", Json.createObjectBuilder()
                        .add("limit", 5000)
                        .add("remaining", 4999)
                        .add("reset", 1372700873)
                        .build()
                ).add(
                    "resources", Json.createObjectBuilder().add(
                        "core", Json.createObjectBuilder()
                            .add("limit", 5000)
                            .add("remaining", 4999)
                            .add("reset", 1372700873)
                            .build()
                    ).add(
                        "search", Json.createObjectBuilder()
                            .add("limit", 5000)
                            .add("remaining", 4999)
                            .add("reset", 1372700873)
                            .build()
                    ).build()
                ).build().toString()
            ),
            "core"
        );
        final RtLimit.Smart smart = new RtLimit.Smart(limit);
        // @checkstyle MagicNumberCheck (3 lines)
        MatcherAssert.assertThat(
            smart.reset(),
            Matchers.equalTo(new Date(1372700873000L))
        );
    }

}
