/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Milestone}.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class MilestoneTest {
    /**
     * Milestone.Smart can fetch title property from Milestone.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesTitle() throws Exception {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("title", "this is some title")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            new Milestone.Smart(milestone).title(),
            Matchers.notNullValue()
        );
    }

    /**
     * Milestone.Smart can fetch description property from Milestone.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesDescription() throws Exception {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("description", "description of the milestone")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            new Milestone.Smart(milestone).description(),
            Matchers.notNullValue()
        );
    }

    /**
     * Milestone.Smart can fetch state property from Milestone.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesState() throws Exception {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("state", "state of the milestone")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            new Milestone.Smart(milestone).state(),
            Matchers.notNullValue()
        );
    }

    /**
     * Milestone.Smart can fetch due_on property from Milestone.
     * @throws Exception If some problem inside
     */
    @Test
    public final void fetchesDueOn() throws Exception {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("due_on", "2011-04-10T20:09:31Z")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            new Milestone.Smart(milestone).dueOn(),
            Matchers.notNullValue()
        );
    }
}
