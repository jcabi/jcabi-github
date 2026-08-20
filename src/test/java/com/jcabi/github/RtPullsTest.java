/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

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
 */
@ExtendWith(RandomPort.class)
final class RtPullsTest {

    @Test
    void createsPullWithPost() throws IOException {
        final String title = "new feature";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPullsTest.answer(title))
                .start(RandomPort.port())
        ) {
            RtPullsTest.create(container, title);
            MatcherAssert.assertThat(
                "Pull is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsPullWithTitle() throws IOException {
        final String title = "new feature";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPullsTest.answer(title))
                .next(RtPullsTest.fetched(title))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created pull has a wrong title",
                new Pull.Smart(RtPullsTest.create(container, title)).title(),
                Matchers.equalTo(title)
            );
        }
    }

    @Test
    void fetchesSinglePull() throws IOException {
        final String title = "new-feature";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtPullsTest.pull(title).toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Values are not equal",
                new Pull.Smart(
                    new RtPulls(
                        new ApacheRequest(container.home()),
                        RtPullsTest.repo()
                    ).get(1_000_000_000)
                ).title(),
                Matchers.equalTo(title)
            );
            container.stop();
        }
    }

    @Test
    void iteratePulls() throws IOException {
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
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtPulls(
                    new ApacheRequest(container.home()),
                    RtPullsTest.repo()
                ).iterate(new ArrayMap<>()),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    private static Pull create(
        final MkContainer container,
        final String title
    ) throws IOException {
        return new RtPulls(
            new ApacheRequest(container.home()),
            RtPullsTest.repo()
        ).create(title, "octocat", "master");
    }

    private static MkAnswer answer(final String title) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            RtPullsTest.pull(title).toString()
        );
    }

    private static MkAnswer fetched(final String title) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtPullsTest.pull(title).toString()
        );
    }

    private static JsonObject pull(final String title) {
        return Json.createObjectBuilder()
            .add("number", 1_000_000_000)
            .add("state", Issue.OPEN_STATE)
            .add("title", title)
            .build();
    }

    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }
}
