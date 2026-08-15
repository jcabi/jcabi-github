/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.google.common.base.Optional;
import com.jcabi.github.Event;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkIssueEvents}.
 * @since 0.23
 */
final class MkIssueEventsTest {

    /**
     * Absent optional string.
     */
    private static final Optional<String> ABSENT_STR = Optional.absent();

    /**
     * Login of the author of the events.
     */
    private static final String LOGIN = "jack";

    /**
     * MkIssueEvents can create issue events.
     * @throws Exception If some problem inside
     */
    @Test
    void createsIssueEvent() throws Exception {
        MatcherAssert.assertThat(
            "Created event has a wrong type",
            MkIssueEventsTest.locked(MkIssueEventsTest.issueEvents()).type(),
            Matchers.equalTo("locked")
        );
    }

    /**
     * MkIssueEvents can remember the author of an issue event.
     * @throws Exception If some problem inside
     */
    @Test
    void createsIssueEventWithAuthor() throws Exception {
        MatcherAssert.assertThat(
            "Created event has a wrong author",
            MkIssueEventsTest.locked(MkIssueEventsTest.issueEvents())
                .author().login(),
            Matchers.equalTo(MkIssueEventsTest.LOGIN)
        );
    }

    /**
     * MkIssueEvents can give a URL to an issue event.
     * @throws Exception If some problem inside
     */
    @Test
    void createsIssueEventWithUrl() throws Exception {
        final MkIssueEvents events = MkIssueEventsTest.issueEvents();
        MatcherAssert.assertThat(
            "Created event has a wrong URL",
            MkIssueEventsTest.locked(events).url().toString(),
            Matchers.equalTo(
                String.format(
                    "https://api.jcabi-github.invalid/repos/jeff/%s/issues/events/1",
                    events.repo().coordinates().repo()
                )
            )
        );
    }

    /**
     * MkIssueEvents can remember the moment of creation of an issue event.
     * @throws Exception If some problem inside
     */
    @Test
    void createsIssueEventWithCreationTime() throws Exception {
        MatcherAssert.assertThat(
            "Created event has a wrong creation time",
            MkIssueEventsTest.locked(MkIssueEventsTest.issueEvents())
                .createdAt().toEpochMilli(),
            Matchers.allOf(
                Matchers.greaterThanOrEqualTo(
                    MkIssueEventsTest.now() - TimeUnit.MINUTES.toMillis(1L)
                ),
                Matchers.lessThanOrEqualTo(MkIssueEventsTest.now())
            )
        );
    }

    /**
     * MkIssueEvents can create an issue event with a label attribute.
     * @throws Exception If some problem inside
     */
    @Test
    void createsIssueEventWithLabel() throws Exception {
        final MkIssueEvents events = MkIssueEventsTest.issueEvents();
        final String label = "my label";
        MatcherAssert.assertThat(
            "Values are not equal",
            new Event.Smart(
                events.create(
                    "labeled",
                    2,
                    "samuel",
                    Optional.of(label)
                )
            ).label().get().name(),
            Matchers.equalTo(label)
        );
    }

    /**
     * MkIssueEvents can get a single issue event.
     * @throws Exception If some problem inside
     */
    @Test
    void getsIssueEvent() throws Exception {
        final MkIssueEvents events = MkIssueEventsTest.issueEvents();
        final int number = MkIssueEventsTest.locked(events).number();
        MatcherAssert.assertThat(
            "Fetched event has a wrong number",
            new Event.Smart(events.get(number)).number(),
            Matchers.equalTo(number)
        );
    }

    /**
     * MkIssueEvents can get the type of a single issue event.
     * @throws Exception If some problem inside
     */
    @Test
    void getsTypeOfIssueEvent() throws Exception {
        final MkIssueEvents events = MkIssueEventsTest.issueEvents();
        MatcherAssert.assertThat(
            "Fetched event has a wrong type",
            new Event.Smart(
                events.get(MkIssueEventsTest.locked(events).number())
            ).type(),
            Matchers.equalTo("locked")
        );
    }

    /**
     * MkIssueEvents can get the author of a single issue event.
     * @throws Exception If some problem inside
     */
    @Test
    void getsAuthorOfIssueEvent() throws Exception {
        final MkIssueEvents events = MkIssueEventsTest.issueEvents();
        MatcherAssert.assertThat(
            "Fetched event has a wrong author",
            new Event.Smart(
                events.get(MkIssueEventsTest.locked(events).number())
            ).author().login(),
            Matchers.equalTo(MkIssueEventsTest.LOGIN)
        );
    }

    /**
     * MkIssueEvents can iterate over issue events in correct order.
     * @throws Exception If some problem inside
     */
    @Test
    void iteratesIssueEvents() throws Exception {
        final MkIssueEvents events = MkIssueEventsTest.issueEvents();
        MkIssueEventsTest.closed(events);
        MkIssueEventsTest.reopened(events);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            events.iterate(),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * MkIssueEvents can iterate over the first issue event.
     * @throws Exception If some problem inside
     */
    @Test
    void iteratesOverFirstIssueEvent() throws Exception {
        final MkIssueEvents events = MkIssueEventsTest.issueEvents();
        final Event first = MkIssueEventsTest.closed(events);
        MkIssueEventsTest.reopened(events);
        MatcherAssert.assertThat(
            "First event is in a wrong place",
            events.iterate().iterator().next(),
            Matchers.equalTo(first)
        );
    }

    /**
     * MkIssueEvents can iterate over the second issue event.
     * @throws Exception If some problem inside
     */
    @Test
    void iteratesOverSecondIssueEvent() throws Exception {
        final MkIssueEvents events = MkIssueEventsTest.issueEvents();
        MkIssueEventsTest.closed(events);
        final Event second = MkIssueEventsTest.reopened(events);
        final Iterator<Event> iter = events.iterate().iterator();
        iter.next();
        MatcherAssert.assertThat(
            "Second event is in a wrong place",
            iter.next(),
            Matchers.equalTo(second)
        );
    }

    /**
     * Create an MkIssueEvents to work with.
     * Can't use normal IssueEvents because we need the mock-only
     * {@link MkIssueEvents#create(String, int, String, Optional)} method.
     * @return MkIssueEvents
     * @throws IOException If some problem inside
     */
    private static MkIssueEvents issueEvents() throws IOException {
        return MkIssueEvents.class.cast(
            new MkGitHub().randomRepo().issueEvents()
        );
    }

    /**
     * Create an event about a locked issue.
     * @param events Events to create the event in
     * @return Created event
     * @throws IOException If some problem inside
     */
    private static Event.Smart locked(final MkIssueEvents events)
        throws IOException {
        return new Event.Smart(
            events.create(
                "locked",
                2,
                MkIssueEventsTest.LOGIN,
                MkIssueEventsTest.ABSENT_STR
            )
        );
    }

    /**
     * Create an event about a closed issue.
     * @param events Events to create the event in
     * @return Created event
     * @throws IOException If some problem inside
     */
    private static Event closed(final MkIssueEvents events) throws IOException {
        return events.create(
            "closed",
            3,
            "john",
            MkIssueEventsTest.ABSENT_STR
        );
    }

    /**
     * Create an event about a reopened issue.
     * @param events Events to create the event in
     * @return Created event
     * @throws IOException If some problem inside
     */
    private static Event reopened(final MkIssueEvents events)
        throws IOException {
        return events.create(
            "reopened",
            3,
            "jane",
            MkIssueEventsTest.ABSENT_STR
        );
    }

    /**
     * Obtains the current time.
     * @return Current time (in milliseconds since epoch) truncated to the nearest second
     */
    private static long now() {
        final long sinceepoch = System.currentTimeMillis();
        return sinceepoch - sinceepoch % 1000;
    }
}
