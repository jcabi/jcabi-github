/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github.mock;

import com.google.common.base.Optional;
import com.jcabi.aspects.Tv;
import com.jcabi.github.Event;
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
            Matchers.<Event>iterableWithSize(2)
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
     * @throws Exception If some problem inside
     */
    private MkIssueEvents issueEvents() throws Exception {
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
