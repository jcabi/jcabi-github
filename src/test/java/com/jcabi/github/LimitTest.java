/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.time.Instant;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Limit}.
 * @since 0.1
 */
final class LimitTest {

    @Test
    void throwsWhenResourceIsAbsent() throws IOException {
        final Limit limit = Mockito.mock(Limit.class);
        final Limit.Throttled throttled = new Limit.Throttled(limit, 23);
        Mockito.when(limit.json()).thenReturn(
            Json.createObjectBuilder().add("absent", "absentValue").build()
        );
        Assertions.assertThrows(
            IllegalStateException.class,
            throttled::json,
            "Should throw when resource is absent"
        );
    }

    /**
     * Limit reset() method properly converts time.
     * GitHub reset property is in seconds, but java.util.Date
     * constructor assumes milliseconds.
     */
    @Test
    void timeIsCreatedForReset() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Limit.Smart(
                new RtLimit(
                    Mockito.mock(GitHub.class),
                    new FakeRequest().withBody(
                        Json.createObjectBuilder().add(
                            "rate", Json.createObjectBuilder()
                                .add("limit", 5000)
                                .add("remaining", 4999)
                                .add("reset", 1_372_700_873)
                                .build()
                        ).add(
                            "resources", Json.createObjectBuilder().add(
                                "core", Json.createObjectBuilder()
                                    .add("limit", 5000)
                                    .add("remaining", 4999)
                                    .add("reset", 1_372_700_873)
                                    .build()
                            ).add(
                                "search", Json.createObjectBuilder()
                                    .add("limit", 5000)
                                    .add("remaining", 4999)
                                    .add("reset", 1_372_700_873)
                                    .build()
                            ).build()
                        ).build().toString()
                    ),
                    "core"
                    )
            ).reset(),
            Matchers.equalTo(Instant.ofEpochMilli(1_372_700_873_000L))
        );
    }
}
