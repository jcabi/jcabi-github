/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Limit}.
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
public final class LimitTest {

    @Test
    public void throwsWhenResourceIsAbsent() throws IOException {
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
     * constructor assumes miliseconds.
     */
    @Test
    public void timeIsCreatedForReset() throws IOException {
        // @checkstyle MagicNumberCheck (21 lines)
        final RtLimit limit = new RtLimit(
            Mockito.mock(GitHub.class),
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
            "Values are not equal",
            smart.reset(),
            Matchers.equalTo(new Date(1372700873000L))
        );
    }

}
