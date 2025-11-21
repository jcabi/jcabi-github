/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.immutable.ArrayMap;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPulls}.
 * @since 0.7
 * @checkstyle ClassDataAbstractionCouplingCheck (200 lines)
 */
@ExtendWith(RandomPort.class)
public final class RtPullsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    public void createPull() throws IOException {
        final String title = "new feature";
        final String body = RtPullsTest.pull(title).toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body))
                .start(RandomPort.port())
        ) {
            final RtPulls pulls = new RtPulls(
                new ApacheRequest(container.home()),
                RtPullsTest.repo()
            );
            final Pull pull = pulls.create(title, "octocat", "master");
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                new Pull.Smart(pull).title(),
                Matchers.equalTo(title)
            );
            container.stop();
        }
    }

    @Test
    public void getSinglePull() throws IOException {
        final String title = "new-feature";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtPullsTest.pull(title).toString()
                )
            ).start(RandomPort.port())
        ) {
            final RtPulls pulls = new RtPulls(
                new ApacheRequest(container.home()),
                RtPullsTest.repo()
            );
            final Pull pull = pulls.get(Tv.BILLION);
            MatcherAssert.assertThat(
                "Values are not equal",
                new Pull.Smart(pull).title(),
                Matchers.equalTo(title)
            );
            container.stop();
        }
    }

    @Test
    public void iteratePulls() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtPullsTest.pull("new-topic"))
                        .add(RtPullsTest.pull("Amazing new feature"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            final RtPulls pulls = new RtPulls(
                new ApacheRequest(container.home()),
                RtPullsTest.repo()
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                pulls.iterate(new ArrayMap<>()),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * Create and return JsonObject to test.
     * @param title The title of the pull request
     * @return JsonObject
     */
    private static JsonObject pull(final String title) {
        return Json.createObjectBuilder()
            .add("number", Tv.BILLION)
            .add("state", Issue.OPEN_STATE)
            .add("title", title)
            .build();
    }

    /**
     * Create and return repo to test.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }
}
