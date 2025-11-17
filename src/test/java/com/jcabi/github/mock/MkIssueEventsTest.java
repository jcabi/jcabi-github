/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.google.common.base.Optional;
import com.jcabi.aspects.Tv;
import com.jcabi.github.Event;
import java.io.IOException;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkIssueEvents}.
 * @since 0.23
 */
public final class MkIssueEventsTest {
    /**
     * Absent optional string.
     */
    private static final Optional<String> ABSENT_STR = Optional.absent();

    /**
     * MkIssueEvents can create issue events.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsIssueEvent() throws Exception {
        final MkIssueEvents events = this.issueEvents();
        final String login = "jack";
        final String type = "locked";
        final long before = MkIssueEventsTest.now();
        final Event.Smart event = new Event.Smart(
            events.create(
                type,
                2,
                login,
                MkIssueEventsTest.ABSENT_STR
            )
        );
        final long after = MkIssueEventsTest.now();
        MatcherAssert.assertThat(
            event.type(),
            Matchers.equalTo(type)
        );
        MatcherAssert.assertThat(
            event.author().login(),
            Matchers.equalTo(login)
        );
        MatcherAssert.assertThat(
            event.url().toString(),
            Matchers.equalTo(
                String.format(
                    // @checkstyle LineLength (1 line)
                    "https://api.jcabi-github.invalid/repos/jeff/%s/issues/events/1",
                    events.repo().coordinates().repo()
                )
            )
        );
        MatcherAssert.assertThat(
            event.createdAt().getTime(),
            Matchers.greaterThanOrEqualTo(before)
        );
        MatcherAssert.assertThat(
            event.createdAt().getTime(),
            Matchers.lessThanOrEqualTo(after)
        );
    }

    /**
     * MkIssueEvents can create an issue event with a label attribute.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsIssueEventWithLabel() throws Exception {
        final MkIssueEvents events = this.issueEvents();
        final String label = "my label";
        final Event.Smart event = new Event.Smart(
            events.create(
                "labeled",
                2,
                "samuel",
                Optional.of(label)
            )
        );
        MatcherAssert.assertThat(
            event.label().get().name(),
            Matchers.equalTo(label)
        );
    }

    /**
     * MkIssueEvents can get a single issue event.
     * @throws Exception If some problem inside
     */
    @Test
    public void getsIssueEvent() throws Exception {
        final MkIssueEvents events = this.issueEvents();
        final String type = "unlocked";
        final String login = "jill";
        final int eventnum = events.create(
            type,
            2,
            login,
            MkIssueEventsTest.ABSENT_STR
        ).number();
        final Event.Smart event = new Event.Smart(events.get(eventnum));
        MatcherAssert.assertThat(
            event.number(),
            Matchers.equalTo(eventnum)
        );
        MatcherAssert.assertThat(
            event.type(),
            Matchers.equalTo(type)
        );
        MatcherAssert.assertThat(
            event.author().login(),
            Matchers.equalTo(login)
        );
    }

    /**
     * MkIssueEvents can iterate over issue events in correct order.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesIssueEvents() throws Exception {
        final MkIssueEvents events = this.issueEvents();
        final Event first = events.create(
            "closed",
            3,
            "john",
            MkIssueEventsTest.ABSENT_STR
        );
        final Event second = events.create(
            "reopened",
            3,
            "jane",
            MkIssueEventsTest.ABSENT_STR
        );
        MatcherAssert.assertThat(
            events.iterate(),
            Matchers.iterableWithSize(2)
        );
        final Iterator<Event> iter = events.iterate().iterator();
        MatcherAssert.assertThat(
            iter.next(),
            Matchers.equalTo(first)
        );
        MatcherAssert.assertThat(
            iter.next(),
            Matchers.equalTo(second)
        );
    }

    /**
     * Create an MkIssueEvents to work with.
     * Can't use normal IssueEvents because we need the mock-only
     * {@link MkIssueEvents#create(String, int, String, Optional)} method.
     * @return MkIssueEvents
     */
    private MkIssueEvents issueEvents() throws IOException {
        return MkIssueEvents.class.cast(
            new MkGithub().randomRepo().issueEvents()
        );
    }

    /**
     * Obtains the current time.
     * @return Current time (in milliseconds since epoch) truncated to the nearest second
     */
    private static long now() {
        final long sinceepoch = System.currentTimeMillis();
        return sinceepoch - sinceepoch % Tv.THOUSAND;
    }
}
