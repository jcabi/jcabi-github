/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
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
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
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
     * @checkstyle VisibilityModifierCheck (3 lines)
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
            final Repo repo = RtRepoTest.repo(
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                repo.issueEvents().iterate(),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    @Test
    void fetchesLabels() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null",
            repo.labels(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesIssues() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null",
            repo.issues(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesBranches() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null",
            repo.branches(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesPulls() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null",
            repo.pulls(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchHooks() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null",
            repo.hooks(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchKeys() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null",
            repo.keys(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchReleases() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null",
            repo.releases(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchContents() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null",
            repo.contents(),
            Matchers.notNullValue()
        );
    }

    @Test
    void identifiesItself() {
        final Coordinates coords = new Coordinates.Simple("me", "me-branch");
        final Repo repo = new RtRepo(
            Mockito.mock(GitHub.class),
            new FakeRequest(),
            coords
        );
        MatcherAssert.assertThat(
            "Assertion failed",
            repo.coordinates(),
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
        final Repo repo = RtRepoTest.repo(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("full_name", "octocat/Hello-World")
                    .add("fork", true)
                    .build()
                    .toString()
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            repo.json().toString(),
            Matchers.equalTo(
                "{\"full_name\":\"octocat/Hello-World\",\"fork\":true}"
            )
        );
    }

    @Test
    void fetchCommits() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null", repo.commits(), Matchers.notNullValue()
        );
    }

    @Test
    void fetchesGit() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null", repo.git(), Matchers.notNullValue()
        );
    }

    @Test
    void fetchStars() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null", repo.stars(), Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its default branch.
     *
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
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            "Value is null", repo.notifications(), Matchers.notNullValue()
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
            final Repo repo = RtRepoTest.repo(
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                "Value is null", repo.languages(), Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    void iteratesLanguages() throws IOException {
        final String lang = "C";
        final String other = "Java";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add(lang, 1)
                        .add(other, 2)
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            final Repo repo = RtRepoTest.repo(
                new ApacheRequest(container.home())
            );
            final Iterator<Language> iter = repo.languages().iterator();
            MatcherAssert.assertThat(
                "Values are not equal",
                iter.hasNext(),
                Matchers.is(true)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                iter.next().name(),
                Matchers.is(lang)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                iter.hasNext(),
                Matchers.is(true)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                iter.next().name(),
                Matchers.is(other)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                iter.hasNext(),
                Matchers.is(false)
            );
            container.stop();
        }
    }

    /**
     * RtStars can retrieve stargazers.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    void retrievesStargazers() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(
                    new MkAnswer.Simple(
                        HttpURLConnection.HTTP_OK,
                        "[]"
                    )
                ).start(RandomPort.port())
        ) {
            final Repo repo = RtRepoTest.repo(
                new ApacheRequest(container.home())
            );
            final Iterable<JsonValue> stargazers = repo.stargazers()
                .iterable();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "We expect no stargazers",
                stargazers,
                Matchers.emptyIterable()
            );
            MatcherAssert.assertThat(
                "Stargazers request should be a GET request",
                query.method(),
                Matchers.equalTo(Request.GET)
            );
            MatcherAssert.assertThat(
                "String does not contain expected value",
                query.uri().getPath(),
                Matchers.containsString(
                    UriBuilder.fromPath("repos")
                        .path(RtRepoTest.TEST_USER)
                        .path(RtRepoTest.TEST_REPO)
                        .path("stargazers")
                        .build()
                        .getPath()
                )
            );
            container.stop();
        }
    }

    /**
     * Create and return JsonObject to test.
     * @param event Event type
     * @return JsonObject
     */
    private static JsonObject event(final String event) {
        return Json.createObjectBuilder()
            .add("id", 1)
            .add("event", event)
            .build();
    }

    /**
     * Create test Repo.
     * @param request Request
     * @return Repo
     */
    private static Repo repo(final Request request) {
        return new RtRepo(
            Mockito.mock(GitHub.class),
            request,
            new Coordinates.Simple(RtRepoTest.TEST_USER, RtRepoTest.TEST_REPO)
        );
    }
}
