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
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
@ExtendWith(RandomPort.class)
final class RtEventTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    void canRetrieveOwnRepo() {
        final Repo repo = RtEventTest.repo();
        final RtEvent event = new RtEvent(new FakeRequest(), repo, 1);
        MatcherAssert.assertThat(
            "Assertion failed",
            event.repo(),
            Matchers.sameInstance(repo)
        );
    }

    @Test
    void canRetrieveOwnNumber() {
        final Repo repo = RtEventTest.repo();
        final RtEvent event = new RtEvent(new FakeRequest(), repo, 2);
        MatcherAssert.assertThat(
            "Values are not equal",
            event.number(),
            Matchers.equalTo(2)
        );
    }

    @Test
    void retrieveEventAsJson() throws IOException {
        try (MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                "{\"test\":\"events\"}"
            )
        ).start(RandomPort.port())) {
            final RtEvent event = new RtEvent(
                new ApacheRequest(container.home()),
                RtEventTest.repo(),
                3
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                event.json().getString("test"),
                Matchers.equalTo("events")
            );
        }
    }

    @Test
    void canCompareInstances() {
        final RtEvent less = new RtEvent(new FakeRequest(), RtEventTest.repo(), 1);
        final RtEvent greater = new RtEvent(new FakeRequest(), RtEventTest.repo(), 2);
        MatcherAssert.assertThat(
            "Value is not less than expected",
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            greater.compareTo(less), Matchers.greaterThan(0)
        );
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "event"))
            .when(repo).coordinates();
        return repo;
    }

}
