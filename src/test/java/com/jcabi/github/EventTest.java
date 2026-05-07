/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Event.Smart}.
 * @since 1.7.0
 */
final class EventTest {

    @Test
    void readsCommitIdWhenPresent() throws IOException {
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db51";
        MatcherAssert.assertThat(
            "Commit SHA is not equal",
            new Event.Smart(
                EventTest.eventWithJson(
                    Json.createObjectBuilder()
                        .add("commit_id", sha)
                        .build()
                )
            ).commitId(),
            Matchers.equalTo(Optional.of(sha))
        );
    }

    @Test
    void commitIdIsAbsentWhenJsonNull() throws IOException {
        MatcherAssert.assertThat(
            "Commit ID should be absent when JSON value is null",
            new Event.Smart(
                EventTest.eventWithJson(
                    Json.createObjectBuilder()
                        .add("commit_id", JsonValue.NULL)
                        .build()
                )
            ).commitId(),
            Matchers.equalTo(Optional.<String>absent())
        );
    }

    @Test
    void commitIdIsAbsentWhenMissing() throws IOException {
        MatcherAssert.assertThat(
            "Commit ID should be absent when key is missing",
            new Event.Smart(
                EventTest.eventWithJson(
                    Json.createObjectBuilder().build()
                )
            ).commitId(),
            Matchers.equalTo(Optional.<String>absent())
        );
    }

    /**
     * Build an Event whose json() method returns the given object.
     * @param obj JSON object to expose via Event.json()
     * @return Mocked Event
     * @throws IOException If stubbing fails
     */
    private static Event eventWithJson(final JsonObject obj)
        throws IOException {
        final Event event = Mockito.mock(Event.class);
        Mockito.doReturn(obj).when(event).json();
        return event;
    }
}
