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
import jakarta.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtRepo}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtRepoTest {

    /**
     * Repo user for tests.
     */
    private static final String TEST_USER = "testuser";

    /**
     * Repo name for tests.
     */
    private static final String TEST_REPO = "testrepo";

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void iteratesEvents() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtRepoTest.event(Event.ASSIGNED))
                        .add(RtRepoTest.event(Event.MENTIONED))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                RtRepoTest.repo(
                    new ApacheRequest(container.home())
                ).issueEvents().iterate(),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    @Test
    void fetchesLabels() {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoTest.repo(
                new FakeRequest()
            ).labels(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesIssues() {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoTest.repo(
                new FakeRequest()
            ).issues(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesBranches() {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoTest.repo(
                new FakeRequest()
            ).branches(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesPulls() {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoTest.repo(
                new FakeRequest()
            ).pulls(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchHooks() {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoTest.repo(
                new FakeRequest()
            ).hooks(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchKeys() {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoTest.repo(
                new FakeRequest()
            ).keys(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchReleases() {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoTest.repo(
                new FakeRequest()
            ).releases(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchContents() {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoTest.repo(
                new FakeRequest()
            ).contents(),
            Matchers.notNullValue()
        );
    }

    @Test
    void identifiesItself() {
        final Coordinates coords = new Coordinates.Simple("me", "me-branch");
        MatcherAssert.assertThat(
            "Assertion failed",
            new RtRepo(
                Mockito.mock(GitHub.class),
                new FakeRequest(),
                coords
            ).coordinates(),
            Matchers.sameInstance(coords)
        );
    }

    @Test
    void executePatchRequest() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtRepoTest.event(Event.ASSIGNED).toString()
                )
            ).start(RandomPort.port())
        ) {
            final Repo repo = RtRepoTest.repo(
                new ApacheRequest(container.home())
            );
            repo.patch(RtRepoTest.event(Event.ASSIGNED));
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
            container.stop();
        }
    }

    @Test
    void describeAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            RtRepoTest.repo(
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add("full_name", "octocat/Hello-World")
                        .add("fork", true)
                        .build()
                        .toString()
                )
            ).json().toString(),
            Matchers.equalTo(
                "{\"full_name\":\"octocat/Hello-World\",\"fork\":true}"
            )
        );
    }

    @Test
    void fetchCommits() {
        MatcherAssert.assertThat(
            "Value is null", RtRepoTest.repo(
                new FakeRequest()
            ).commits(), Matchers.notNullValue()
        );
    }

    @Test
    void fetchesGit() {
        MatcherAssert.assertThat(
            "Value is null", RtRepoTest.repo(
                new FakeRequest()
            ).git(), Matchers.notNullValue()
        );
    }

    @Test
    void fetchStars() {
        MatcherAssert.assertThat(
            "Value is null", RtRepoTest.repo(
                new FakeRequest()
            ).stars(), Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its default branch.
     * @throws IOException If some problem occurs.
     */
    @Test
    void fetchDefaultBranch() throws IOException {
        final String expected = "main";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add("default_branch", expected)
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Values are not equal",
                RtRepoTest.repo(
                    new ApacheRequest(container.home())
                ).defaultBranch().name(),
                Matchers.equalTo(expected)
            );
            container.stop();
        }
    }

    @Test
    void fetchNotifications() {
        MatcherAssert.assertThat(
            "Value is null", RtRepoTest.repo(
                new FakeRequest()
            ).notifications(), Matchers.notNullValue()
        );
    }

    @Test
    void fetchLanguages() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add("Ruby", 1)
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Value is null", RtRepoTest.repo(
                    new ApacheRequest(container.home())
                ).languages(), Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    void iteratesFirstLanguage() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtRepoTest.languages())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "First language of the repo is different",
                RtRepoTest.repo(new ApacheRequest(container.home()))
                    .languages().iterator().next().name(),
                Matchers.is("C")
            );
        }
    }

    @Test
    void iteratesSecondLanguage() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtRepoTest.languages())
                .start(RandomPort.port())
        ) {
            final Iterator<Language> iter = RtRepoTest
                .repo(new ApacheRequest(container.home()))
                .languages().iterator();
            iter.next();
            MatcherAssert.assertThat(
                "Second language of the repo is different",
                iter.next().name(),
                Matchers.is("Java")
            );
        }
    }

    @Test
    void stopsIteratingLanguages() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtRepoTest.languages())
                .start(RandomPort.port())
        ) {
            final Iterator<Language> iter = RtRepoTest
                .repo(new ApacheRequest(container.home()))
                .languages().iterator();
            iter.next();
            iter.next();
            MatcherAssert.assertThat(
                "Repo has more than two languages",
                iter.hasNext(),
                Matchers.is(false)
            );
        }
    }

    @Test
    void retrievesStargazers() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "[]"))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "We expect no stargazers",
                RtRepoTest.repo(new ApacheRequest(container.home()))
                    .stargazers().iterable(),
                Matchers.emptyIterable()
            );
        }
    }

    @Test
    void retrievesStargazersWithGet() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "[]"))
                .start(RandomPort.port())
        ) {
            RtRepoTest.repo(new ApacheRequest(container.home()))
                .stargazers().iterable();
            MatcherAssert.assertThat(
                "Stargazers request should be a GET request",
                container.take().method(),
                Matchers.equalTo(Request.GET)
            );
        }
    }

    @Test
    void retrievesStargazersFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "[]"))
                .start(RandomPort.port())
        ) {
            RtRepoTest.repo(new ApacheRequest(container.home()))
                .stargazers().iterable();
            MatcherAssert.assertThat(
                "Stargazers are retrieved from a wrong URI",
                container.take().uri().getPath(),
                Matchers.containsString(
                    UriBuilder.fromPath("repos")
                        .path(RtRepoTest.TEST_USER)
                        .path(RtRepoTest.TEST_REPO)
                        .path("stargazers")
                        .build()
                        .getPath()
                )
            );
        }
    }

    private static MkAnswer languages() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add("C", 1)
                .add("Java", 2)
                .build().toString()
        );
    }

    private static JsonObject event(final String event) {
        return Json.createObjectBuilder()
            .add("id", 1)
            .add("event", event)
            .build();
    }

    private static Repo repo(final Request request) {
        return new RtRepo(
            Mockito.mock(GitHub.class),
            request,
            new Coordinates.Simple(RtRepoTest.TEST_USER, RtRepoTest.TEST_REPO)
        );
    }
}
