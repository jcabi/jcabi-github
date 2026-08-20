/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtEvent}.
 * @since 0.6.1
 */
@ExtendWith(RandomPort.class)
final class RtEventTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void canRetrieveOwnRepo() {
        final Repo repo = RtEventTest.repo();
        MatcherAssert.assertThat(
            "Assertion failed",
            new RtEvent(new FakeRequest(), repo, 1).repo(),
            Matchers.sameInstance(repo)
        );
    }

    @Test
    void canRetrieveOwnNumber() {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtEvent(new FakeRequest(), RtEventTest.repo(), 2).number(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void retrieveEventAsJson() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"test\":\"events\"}"
                )
                ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Values are not equal",
                new RtEvent(
                    new ApacheRequest(container.home()),
                    RtEventTest.repo(),
                    3
                ).json().getString("test"),
                Matchers.equalTo("events")
            );
        }
    }

    @Test
    void comparesSmallerEvent() {
        MatcherAssert.assertThat(
            "Event is not less than the greater one",
            RtEventTest.event(1).compareTo(RtEventTest.event(2)),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerEvent() {
        MatcherAssert.assertThat(
            "Event is not greater than the smaller one",
            RtEventTest.event(2).compareTo(RtEventTest.event(1)),
            Matchers.greaterThan(0)
        );
    }

    private static RtEvent event(final int number) {
        return new RtEvent(new FakeRequest(), RtEventTest.repo(), number);
    }

    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "event"))
            .when(repo).coordinates();
        return repo;
    }
}
