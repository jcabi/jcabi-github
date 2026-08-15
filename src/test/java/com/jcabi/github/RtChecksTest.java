/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtChecks}.
 * @since 1.5.0
 */
@ExtendWith(RandomPort.class)
final class RtChecksTest {

    /**
     * Conclusion key in json check.
     */
    private static final String CONCLUSION_KEY = "conclusion";

    /**
     * Status key in json check.
     */
    private static final String STATUS_KEY = "status";

    /**
     * Checks whether RtChecks can get all checks.
     * @throws IOException If some problem happens.
     */
    @Test
    void getsAllChecks() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.jsonWithCheckRuns()
                )
                )
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtChecks(
                    new JdkRequest(container.home()),
                    RtChecksTest.repo().pulls().get(0)
                ).all(),
                Matchers.iterableWithSize(1)
            );
        }
    }

    /**
     * Checks whether RtChecks can return empty checks if they are absent.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    void returnsEmptyChecksIfTheyAreAbsent() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.empty()
                )
                )
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                ((Checks) new RtChecks(
                    new JdkRequest(container.home()),
                    RtChecksTest.repo().pulls().get(0)
                )).all(),
                Matchers.iterableWithSize(0)
            );
        }
    }

    /**
     * Checks whether RtChecks can throw an exception
     * if response code is not 200.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    void assertsOkResponse() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    RtChecksTest.jsonWithCheckRuns()
                )
            ).start(RandomPort.port())
        ) {
            Assertions.assertThrows(
                AssertionError.class,
                RtChecksTest.checks(container)::all,
                "Not found response is not reported as an error"
            );
        }
    }

    /**
     * Checks that library can retrieve a check without conclusion.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    void retrievesCheckWithoutConclusion() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.jsonChecks(
                        RtChecksTest.jsonCheck().add(
                            RtChecksTest.CONCLUSION_KEY,
                            Check.Conclusion.SUCCESS.value()
                        )
                    )
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                RtChecksTest.checks(container).all(),
                Matchers.hasSize(1)
            );
        }
    }

    /**
     * Checks that a check without conclusion is not successful.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    void retrievesUnfinishedCheckWithoutConclusion() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.jsonChecks(
                        RtChecksTest.jsonCheck().add(
                            RtChecksTest.CONCLUSION_KEY,
                            Check.Conclusion.SUCCESS.value()
                        )
                    )
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Check without status is successful",
                RtChecksTest.checks(container).all().iterator().next()
                    .successful(),
                Matchers.is(false)
            );
        }
    }

    /**
     * Checks that library can retrieve a check with nullable conclusion.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    void retrievesCheckWithNullableConclusion() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.queued()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                RtChecksTest.checks(container).all(),
                Matchers.hasSize(1)
            );
        }
    }

    /**
     * Checks that a check with nullable conclusion is not successful.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    void retrievesUnfinishedCheckWithNullableConclusion() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.queued()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Queued check is successful",
                RtChecksTest.checks(container).all().iterator().next()
                    .successful(),
                Matchers.is(false)
            );
        }
    }

    /**
     * Checks that library can retrieve a check without status.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    void retrievesCheckWithoutStatusAndConclusion() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.jsonChecks(RtChecksTest.jsonCheck())
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                RtChecksTest.checks(container).all(),
                Matchers.hasSize(1)
            );
        }
    }

    /**
     * Checks that a check without status is not successful.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    void retrievesUnfinishedCheckWithoutStatusAndConclusion()
        throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.jsonChecks(RtChecksTest.jsonCheck())
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Check without status and conclusion is successful",
                RtChecksTest.checks(container).all().iterator().next()
                    .successful(),
                Matchers.is(false)
            );
        }
    }

    /**
     * Checks served by the given container.
     * @param container Container to serve the checks
     * @return Checks
     * @throws IOException If some problem happens.
     */
    private static Checks checks(final MkContainer container)
        throws IOException {
        return new RtChecks(
            new JdkRequest(container.home()),
            RtChecksTest.repo().pulls().get(0)
        );
    }

    /**
     * Creates json of a queued check.
     * @return Json response body
     */
    private static String queued() {
        return RtChecksTest.jsonChecks(
            RtChecksTest.jsonCheck().add(
                RtChecksTest.CONCLUSION_KEY,
                JsonValue.NULL
            ).add(
                RtChecksTest.STATUS_KEY,
                Check.Status.QUEUED.value()
            )
        );
    }

    /**
     * Creates json response body.
     * @return Json response body
     */
    private static String jsonWithCheckRuns() {
        return RtChecksTest.jsonChecks(
            RtChecksTest.jsonCheck().add(
                RtChecksTest.STATUS_KEY,
                Check.Status.COMPLETED.value()
                ).add(
                    RtChecksTest.CONCLUSION_KEY,
                    Check.Conclusion.SUCCESS.value()
                )
        );
    }

    /**
     * Creates Json Check Builder.
     * @return JsonObjectBuilder
     */
    private static JsonObjectBuilder jsonCheck() {
        return Json.createObjectBuilder()
            .add("id", Json.createValue(new Random().nextInt()));
    }

    /**
     * Creates json checks.
     * @param checks All checks that have to be included
     * @return Json
     */
    private static String jsonChecks(final JsonObjectBuilder... checks) {
        final JsonArrayBuilder all = Json.createArrayBuilder();
        Arrays.stream(checks).map(JsonObjectBuilder::build).forEach(all::add);
        return Json.createObjectBuilder()
            .add("total_count", Json.createValue(1))
            .add("check_runs", all.build())
            .build()
            .toString();
    }

    /**
     * Creates json response body without check runs.
     * @return Json response body
     */
    private static String empty() {
        return Json.createObjectBuilder()
            .build()
            .toString();
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     * @throws IOException If some problem happens.
     */
    private static Repo repo() throws IOException {
        final Repo repo = Mockito.mock(Repo.class);
        final Pulls pulls = Mockito.mock(Pulls.class);
        final Pull pull = Mockito.mock(Pull.class);
        final PullRef ref = Mockito.mock(PullRef.class);
        Mockito.doReturn(
            new Coordinates.Simple("volodya-lombrozo", "jtcop")
        ).when(repo)
            .coordinates();
        Mockito.doReturn(pulls).when(repo).pulls();
        Mockito.doReturn(pull).when(pulls).get(0);
        Mockito.doReturn(repo).when(pull).repo();
        Mockito.doReturn(ref).when(pull).head();
        Mockito.doReturn("abcdef1").when(ref).sha();
        return repo;
    }
}
