/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.ws.rs.core.UriBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtRepo}.
 *
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class RtRepoTest {

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
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtRepo can fetch events.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesEvents() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(event(Event.ASSIGNED))
                        .add(event(Event.MENTIONED))
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final Repo repo = RtRepoTest.repo(
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                repo.issueEvents().iterate(),
                Matchers.<Event>iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtRepo can fetch its labels.
     *
     */
    @Test
    public void fetchesLabels() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            repo.labels(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its issues.
     *
     */
    @Test
    public void fetchesIssues() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            repo.issues(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its branches.
     *
     */
    @Test
    public void fetchesBranches() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            repo.branches(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its pulls.
     *
     */
    @Test
    public void fetchesPulls() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            repo.pulls(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its hooks.
     *
     */
    @Test
    public void fetchHooks() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            repo.hooks(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its keys.
     *
     */
    @Test
    public void fetchKeys() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            repo.keys(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its releases.
     *
     */
    @Test
    public void fetchReleases() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            repo.releases(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch its contents.
     *
     */
    @Test
    public void fetchContents() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(
            repo.contents(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can identify itself.
     */
    @Test
    public void identifiesItself() {
        final Coordinates coords = new Coordinates.Simple("me", "me-branch");
        final Repo repo = new RtRepo(
            Mockito.mock(Github.class),
            new FakeRequest(),
            coords
        );
        MatcherAssert.assertThat(
            repo.coordinates(),
            Matchers.sameInstance(coords)
        );
    }

    /**
     * RtRepo can execute PATCH request.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void executePatchRequest() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    event(Event.ASSIGNED).toString()
                )
            ).start(this.resource.port())
        ) {
            final Repo repo = RtRepoTest.repo(
                new ApacheRequest(container.home())
            );
            repo.patch(RtRepoTest.event(Event.ASSIGNED));
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
            container.stop();
        }
    }

    /**
     * RtRepo can describe as a JSON object.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void describeAsJson() throws Exception {
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
            repo.json().toString(),
            Matchers.equalTo(
                "{\"full_name\":\"octocat/Hello-World\",\"fork\":true}"
            )
        );
    }

    /**
     * RtRepo can fetch commits.
     */
    @Test
    public void fetchCommits() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(repo.commits(), Matchers.notNullValue());
    }

    /**
     * RtRepo can fetch Git.
     */
    @Test
    public void fetchesGit() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(repo.git(), Matchers.notNullValue());
    }

    /**
     * RtRepo can fetch stars.
     */
    @Test
    public void fetchStars() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(repo.stars(), Matchers.notNullValue());
    }

    /**
     * RtRepo can fetch its default branch.
     *
     * @throws IOException If some problem occurs.
     */
    @Test
    public void fetchDefaultBranch() throws IOException {
        final String expected = "main";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add("default_branch", expected)
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            MatcherAssert.assertThat(
                RtRepoTest.repo(
                    new ApacheRequest(container.home())
                ).defaultBranch().name(),
                Matchers.equalTo(expected)
            );
            container.stop();
        }
    }

    /**
     * RtRepo can fetch notifications.
     */
    @Test
    public void fetchNotifications() {
        final Repo repo = RtRepoTest.repo(
            new FakeRequest()
        );
        MatcherAssert.assertThat(repo.notifications(), Matchers.notNullValue());
    }

    /**
     * RtRepo can fetch languages.
     * @throws Exception if a problem occurs.
     */
    @Test
    public void fetchLanguages() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add("Ruby", 1)
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final Repo repo = RtRepoTest.repo(
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(repo.languages(), Matchers.notNullValue());
            container.stop();
        }
    }

    /**
     * RtRepo can iterate languages.
     *
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesLanguages() throws Exception {
        final String lang = "C";
        final String other = "Java";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add(lang, 1)
                        .add(other, 2)
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final Repo repo = RtRepoTest.repo(
                new ApacheRequest(container.home())
            );
            final Iterator<Language> iter = repo.languages().iterator();
            MatcherAssert.assertThat(
                iter.hasNext(),
                Matchers.is(true)
            );
            MatcherAssert.assertThat(
                iter.next().name(),
                Matchers.is(lang)
            );
            MatcherAssert.assertThat(
                iter.hasNext(),
                Matchers.is(true)
            );
            MatcherAssert.assertThat(
                iter.next().name(),
                Matchers.is(other)
            );
            MatcherAssert.assertThat(
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
    public void retrievesStargazers() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer()
                .next(
                    new MkAnswer.Simple(
                        HttpURLConnection.HTTP_OK,
                        "[]"
                    )
                ).start(this.resource.port())
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
            Mockito.mock(Github.class),
            request,
            new Coordinates.Simple(RtRepoTest.TEST_USER, RtRepoTest.TEST_REPO)
        );
    }
}
