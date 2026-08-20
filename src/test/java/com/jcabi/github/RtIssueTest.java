/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtIssue}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtIssueTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void fetchesComments() {
        MatcherAssert.assertThat(
            "Value is null",
            new RtIssue(new FakeRequest(), RtIssueTest.repo(), 1).comments(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesLabels() {
        MatcherAssert.assertThat(
            "Value is null",
            new RtIssue(new FakeRequest(), RtIssueTest.repo(), 1).labels(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesEvents() {
        MatcherAssert.assertThat(
            "Value is null",
            new RtIssue(new FakeRequest(), RtIssueTest.repo(), 1).events(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchIssueAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtIssue(
                new FakeRequest().withBody("{\"issue\":\"json\"}"),
                RtIssueTest.repo(),
                1
            ).json().getString("issue"),
            Matchers.equalTo("json")
        );
    }

    @Test
    void patchesThroughPatchMethod() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
            ).start(RandomPort.port())
        ) {
            RtIssueTest.patch(container);
            MatcherAssert.assertThat(
                "Issue is not patched through PATCH",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
        }
    }

    @Test
    void sendsPatchInRequestBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
            ).start(RandomPort.port())
        ) {
            RtIssueTest.patch(container);
            MatcherAssert.assertThat(
                "Patch is not sent in the request body",
                container.take().body(),
                Matchers.equalTo("{\"patch\":\"test\"}")
            );
        }
    }

    @Test
    void comparesSmallerIssue() {
        MatcherAssert.assertThat(
            "Issue is not less than the greater one",
            RtIssueTest.issue(1).compareTo(RtIssueTest.issue(2)),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerIssue() {
        MatcherAssert.assertThat(
            "Issue is not greater than the smaller one",
            RtIssueTest.issue(2).compareTo(RtIssueTest.issue(1)),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void locksWithPutMethod() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            RtIssueTest.lock(container);
            MatcherAssert.assertThat(
                "Lock request was not sent with PUT method",
                container.take().method(),
                Matchers.equalTo(Request.PUT)
            );
        }
    }

    @Test
    void locksWithValidReason() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            RtIssueTest.lock(container);
            MatcherAssert.assertThat(
                "Lock request body did not contain the expected reason",
                container.take().body(),
                Matchers.equalTo("{\"lock_reason\":\"off-topic\"}")
            );
        }
    }

    @Test
    void rejectsInvalidLockReason() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> RtIssueTest.issue(1).lock("not-a-valid-reason")
        );
    }

    @Test
    void reacts() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            final Issue issue = new RtIssue(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo(),
                10
            );
            issue.react(new Reaction.Simple(Reaction.HEART));
            MatcherAssert.assertThat(
                "Issue was unable to react",
                container.take().method(),
                new IsEqual<>(Request.POST)
            );
        }
    }

    private static void patch(final MkContainer container) throws IOException {
        new RtIssue(
            new ApacheRequest(container.home()),
            RtIssueTest.repo(),
            1
        ).patch(Json.createObjectBuilder().add("patch", "test").build());
    }

    private static void lock(final MkContainer container) throws IOException {
        new RtIssue(
            new ApacheRequest(container.home()),
            RtIssueTest.repo(),
            1
        ).lock("off-topic");
    }

    private static RtIssue issue(final int number) {
        return new RtIssue(new FakeRequest(), RtIssueTest.repo(), number);
    }

    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("user").when(coords).user();
        Mockito.doReturn("repo").when(coords).repo();
        return repo;
    }
}
