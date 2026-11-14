/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Testcase for {@link RtStatuses}.
 *
 * @since 0.24
 * @todo #1130:30min Write RtStatusesITCase, an integration test case for
 *  RtStatuses/RtStatus against real GitHub commit status data.
 * @todo #1490:30min Continue to close grizzle servers open on tests. Use
 *  try-with-resource statement instead of try-catch whenever is possible.
 */
@ExtendWith(RandomPort.class)
final class RtStatusesTest {

    /**
     * Target URL of the created status.
     */
    private static final String URL = "https://ci.example.com/1000/output";

    /**
     * Description of the created status.
     */
    private static final String DESCRIPTION =
        "Build has completed successfully";

    /**
     * Context of the created status.
     */
    private static final String CONTEXT = "continuous-integration/jenkins";

    /**
     * RtStatuses can fetch its commit.
     *
     * @throws IOException If there is an I/O problem.
     */
    @Test
    void fetchesCommit() throws IOException {
        final Commit original = new MkGitHub().randomRepo().git()
            .commits().get("5e8d65e0dbfab0716db16493e03a0baba480625a");
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtStatuses(new FakeRequest(), original).commit(),
            Matchers.equalTo(original)
        );
    }

    @Test
    void createsStatusWithPost() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtStatusesTest.answer())
                .start(RandomPort.port())
        ) {
            RtStatusesTest.create(container);
            MatcherAssert.assertThat(
                "Status is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsStatusWithState() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtStatusesTest.answer())
                .start(RandomPort.port())
        ) {
            RtStatusesTest.create(container);
            MatcherAssert.assertThat(
                "Created status has a wrong state",
                RtStatusesTest.sent(container).getString("state"),
                Matchers.equalTo(Status.State.FAILURE.identifier())
            );
        }
    }

    @Test
    void createsStatusWithContext() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtStatusesTest.answer())
                .start(RandomPort.port())
        ) {
            RtStatusesTest.create(container);
            MatcherAssert.assertThat(
                "Created status has a wrong context",
                RtStatusesTest.sent(container).getString("context"),
                Matchers.equalTo(RtStatusesTest.CONTEXT)
            );
        }
    }

    @Test
    void createsStatusWithDescription() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtStatusesTest.answer())
                .start(RandomPort.port())
        ) {
            RtStatusesTest.create(container);
            MatcherAssert.assertThat(
                "Created status has a wrong description",
                RtStatusesTest.sent(container).getString("description"),
                Matchers.equalTo(RtStatusesTest.DESCRIPTION)
            );
        }
    }

    @Test
    void createsStatusWithTargetUrl() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtStatusesTest.answer())
                .start(RandomPort.port())
        ) {
            RtStatusesTest.create(container);
            MatcherAssert.assertThat(
                "Created status has a wrong target URL",
                RtStatusesTest.sent(container).getString("target_url"),
                Matchers.equalTo(RtStatusesTest.URL)
            );
        }
    }

    @Test
    void listsStatusesOfRef() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtStatusesTest.listed())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Wrong amount of statuses is listed",
                RtStatusesTest.statuses(container).list("feature-a"),
                Matchers.iterableWithSize(2)
            );
        }
    }

    @Test
    void listsStatusesWithGet() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtStatusesTest.listed())
                .start(RandomPort.port())
        ) {
            RtStatusesTest.statuses(container).list("feature-a")
                .iterator().next();
            MatcherAssert.assertThat(
                "Statuses are not listed with GET",
                container.take().method(),
                Matchers.equalTo(Request.GET)
            );
        }
    }

    @Test
    void listsStatusesFromRefUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtStatusesTest.listed())
                .start(RandomPort.port())
        ) {
            final Statuses statuses = RtStatusesTest.statuses(container);
            statuses.list("feature-a").iterator().next();
            MatcherAssert.assertThat(
                "Statuses are listed from a wrong URI",
                container.take().uri().getPath(),
                Matchers.equalTo(
                    String.format(
                        "/repos/%s/commits/feature-a/statuses",
                        statuses.commit().repo().coordinates()
                    )
                )
            );
        }
    }

    @Test
    void listsStatusesWithState() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtStatusesTest.listed())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Listed status has a wrong state",
                new Status.Smart(
                    RtStatusesTest.statuses(container).list("feature-a")
                        .iterator().next()
                ).state(),
                Matchers.equalTo(Status.State.SUCCESS)
            );
        }
    }

    private static Statuses statuses(final MkContainer container)
        throws IOException {
        final Request entry = new ApacheRequest(container.home());
        return new RtStatuses(
            entry,
            new RtCommit(
                entry,
                new MkGitHub().randomRepo(),
                "0abcd89jcabitest"
            )
        );
    }

    private static void create(final MkContainer container) throws IOException {
        final Request entry = new ApacheRequest(container.home());
        new RtStatuses(
            entry,
            new RtCommit(
                entry,
                new MkGitHub().randomRepo(),
                "0abcd89jcabitest"
            )
        ).create(
            new Statuses.StatusCreate(Status.State.FAILURE)
                .withTargetUrl(Optional.of(RtStatusesTest.URL))
                .withDescription(RtStatusesTest.DESCRIPTION)
                .withContext(Optional.of(RtStatusesTest.CONTEXT))
        );
    }

    private static JsonObject sent(final MkContainer container)
        throws IOException {
        try (
            JsonReader reader = Json.createReader(
                new StringReader(container.take().body())
            )
        ) {
            return reader.readObject();
        }
    }

    private static MkAnswer listed() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder()
                .add(RtStatusesTest.status(1, Status.State.SUCCESS))
                .add(RtStatusesTest.status(2, Status.State.PENDING))
                .build().toString()
        );
    }

    private static JsonObject status(final int number,
        final Status.State state) {
        return Json.createObjectBuilder()
            .add("id", number)
            .add("url", RtStatusesTest.URL)
            .add("state", state.identifier())
            .add("description", RtStatusesTest.DESCRIPTION)
            .add("context", RtStatusesTest.CONTEXT)
            .build();
    }

    private static MkAnswer answer() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            Json.createObjectBuilder()
                .add("state", "failure")
                .add("target_url", RtStatusesTest.URL)
                .add("description", RtStatusesTest.DESCRIPTION)
                .add("context", RtStatusesTest.CONTEXT)
                .build().toString()
        );
    }
}
