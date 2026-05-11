/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Milestone}.
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 */
final class MilestoneTest {
    @Test
    void fetchesTitle() throws IOException {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("title", "this is some title")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Milestone.Smart(milestone).title(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesDescription() throws IOException {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("description", "description of the milestone")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Milestone.Smart(milestone).description(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesState() throws IOException {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("state", "state of the milestone")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Milestone.Smart(milestone).state(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesDueOn() throws IOException {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("due_on", "2011-04-10T20:09:31Z")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Milestone.Smart(milestone).dueOn(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesUpdatedAt() throws IOException {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("updated_at", "2014-03-03T18:58:10Z")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            "updatedAt() is null",
            new Milestone.Smart(milestone).updatedAt(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesClosedAt() throws IOException {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("closed_at", "2013-02-12T13:22:01Z")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            "closedAt() is null",
            new Milestone.Smart(milestone).closedAt(),
            Matchers.notNullValue()
        );
    }
}
