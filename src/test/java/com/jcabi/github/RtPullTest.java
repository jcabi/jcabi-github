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
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Random;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPull}.
 * @since 0.7
 */
@ExtendWith(RandomPort.class)
final class RtPullTest {

    /**
     * Property name for ref name in pull request ref JSON object.
     */
    private static final String REF_PROP = "ref";

    /**
     * Property name for commit SHA in pull request ref JSON object.
     */
    private static final String SHA_PROP = "sha";

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void fetchesCommits() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"commits\":\"test\"}]"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Value is null",
                new RtPull(
                    new ApacheRequest(container.home()),
                    RtPullTest.repo(),
                    1
                ).commits(),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    void fetchesFiles() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"file1\":\"testFile\"}]"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Values are not equal",
                new RtPull(
                    new ApacheRequest(container.home()),
                    RtPullTest.repo(),
                    2
                ).files().iterator().next().getString("file1"),
                Matchers.equalTo("testFile")
            );
            container.stop();
        }
    }

    @Test
    void fetchesBase() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPullTest.answer(RtPullTest.base()))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Base of the pull is absent",
                RtPullTest.pull(container, 1).base(),
                Matchers.notNullValue()
            );
        }
    }

    @Test
    void fetchesRefOfBase() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPullTest.answer(RtPullTest.base()))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Base of the pull has a wrong ref",
                RtPullTest.pull(container, 1).base().ref(),
                Matchers.equalTo("sweet-feature-branch")
            );
        }
    }

    @Test
    void fetchesShaOfBase() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPullTest.answer(RtPullTest.base()))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Base of the pull has a wrong SHA",
                RtPullTest.pull(container, 1).base().sha(),
                Matchers.equalTo("e93c6a2216c69daa574abc16e7c14767fce44ad6")
            );
        }
    }

    @Test
    void fetchesHead() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPullTest.answer(RtPullTest.head()))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Head of the pull is absent",
                RtPullTest.pull(container, 1).head(),
                Matchers.notNullValue()
            );
        }
    }

    @Test
    void fetchesRefOfHead() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPullTest.answer(RtPullTest.head()))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Head of the pull has a wrong ref",
                RtPullTest.pull(container, 1).head().ref(),
                Matchers.equalTo("ref-ref")
            );
        }
    }

    @Test
    void fetchesShaOfHead() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPullTest.answer(RtPullTest.head()))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Head of the pull has a wrong SHA",
                RtPullTest.pull(container, 1).head().sha(),
                Matchers.equalTo("6d299617d9094ae6940b3958bbabab68fd1ddabb")
            );
        }
    }

    @Test
    void mergesWithPut() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testMerge")
            ).start(RandomPort.port())
        ) {
            RtPullTest.pull(container, 3).merge("Test commit.");
            MatcherAssert.assertThat(
                "Pull is not merged with PUT",
                container.take().method(),
                Matchers.equalTo(Request.PUT)
            );
        }
    }

    @Test
    void sendsCommitMessageWhileMerging() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testMerge")
            ).start(RandomPort.port())
        ) {
            RtPullTest.pull(container, 3).merge("Test commit.");
            MatcherAssert.assertThat(
                "Commit message is not sent while merging",
                container.take().body(),
                Matchers.equalTo("{\"commit_message\":\"Test commit.\"}")
            );
        }
    }

    @Test
    void canFetchChecks() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPullTest.answer(RtPullTest.head()))
                .next(RtPullTest.answer(RtPullTest.check()))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Pull has a wrong amount of checks",
                RtPullTest.pull(container, 1).checks().all(),
                Matchers.hasSize(1)
            );
        }
    }

    @Test
    void fetchesSuccessfulCheck() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPullTest.answer(RtPullTest.head()))
                .next(RtPullTest.answer(RtPullTest.check()))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Check of the pull is not successful",
                RtPullTest.pull(container, 1).checks().all()
                    .iterator().next().successful(),
                Matchers.is(true)
            );
        }
    }

    @Test
    void comparesSmallerPull() {
        MatcherAssert.assertThat(
            "Pull is not less than the greater one",
            RtPullTest.pull(1).compareTo(RtPullTest.pull(2)),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerPull() {
        MatcherAssert.assertThat(
            "Pull is not greater than the smaller one",
            RtPullTest.pull(2).compareTo(RtPullTest.pull(1)),
            Matchers.greaterThan(0)
        );
    }

    @Test
    @Disabled
    void canFetchComments() {
        Assertions.fail("Fetching of pull comments is not tested yet");
    }

    /**
     * Pull served by the given container.
     * @param container Container to serve the pull
     * @param number Number of the pull
     * @return The pull
     * @throws IOException If there is any I/O problem
     */
    private static RtPull pull(final MkContainer container, final int number)
        throws IOException {
        return new RtPull(
            new ApacheRequest(container.home()), RtPullTest.repo(), number
        );
    }

    /**
     * Pull with the given number.
     * @param number Number of the pull
     * @return The pull
     */
    private static RtPull pull(final int number) {
        return new RtPull(new FakeRequest(), RtPullTest.repo(), number);
    }

    /**
     * Answer with the given JSON body.
     * @param body Body of the answer
     * @return Answer
     */
    private static MkAnswer answer(final JsonObject body) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK, body.toString()
        );
    }

    /**
     * Base as JSON object.
     * @return Base as JSON object
     */
    private static JsonObject base() {
        return Json.createObjectBuilder().add(
            "base",
            Json.createObjectBuilder()
                .add(RtPullTest.REF_PROP, "sweet-feature-branch").add(
                    RtPullTest.SHA_PROP,
                    "e93c6a2216c69daa574abc16e7c14767fce44ad6"
                )
        ).build();
    }

    /**
     * Mock repository for testing purposes.
     * @return Repo the mock repository
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("/user").when(coords).user();
        Mockito.doReturn("/repo").when(coords).repo();
        return repo;
    }

    /**
     * Check as JSON object.
     * @return Check as JSON object
     */
    private static JsonObject check() {
        return Json.createObjectBuilder()
            .add("total_count", Json.createValue(1)).add(
                "check_runs",
                Json.createArrayBuilder().add(
                    Json.createObjectBuilder()
                        .add("id", Json.createValue(new Random().nextInt()))
                        .add("status", "completed")
                        .add("conclusion", "success")
                        .build()
                    )
            ).build();
    }

    /**
     * Head as JSON object.
     * @return Head as JSON object
     */
    private static JsonObject head() {
        return RtPullTest.head(
            "ref-ref",
            "6d299617d9094ae6940b3958bbabab68fd1ddabb"
        );
    }

    /**
     * Head as JSON object.
     * @param ref Ref
     * @param sha Sha
     * @return Head as JSON object
     */
    private static JsonObject head(final String ref, final String sha) {
        return Json.createObjectBuilder().add(
            "head",
            Json.createObjectBuilder()
                .add(RtPullTest.REF_PROP, ref)
                .add(RtPullTest.SHA_PROP, sha)
                .build()
            )
            .build();
    }
}
