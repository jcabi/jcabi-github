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
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtRepoComment}.
 *
 * @since 2.0
 */
@ExtendWith(RandomPort.class)
final class RtRepoCommentTest {

    @Test
    void fetchesJsonFromCommentUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "{\"id\":42}")
            ).start(RandomPort.port())
        ) {
            RtRepoCommentTest.comment(container, 42L).json();
            MatcherAssert.assertThat(
                "Comment is fetched from a wrong URI",
                container.take().uri().getPath(),
                Matchers.equalTo("/repos/octocat/hello/comments/42")
            );
        }
    }

    @Test
    void returnsItsJson() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK, "{\"body\":\"Nice change\"}"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Comment JSON is wrong",
                RtRepoCommentTest.comment(container, 1L).json()
                    .getString("body"),
                Matchers.equalTo("Nice change")
            );
        }
    }

    @Test
    void patchesComment() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "{}")
            ).start(RandomPort.port())
        ) {
            RtRepoCommentTest.comment(container, 1L).patch(
                Json.createObjectBuilder().add("body", "Updated").build()
            );
            MatcherAssert.assertThat(
                "Comment is not patched with PATCH",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
        }
    }

    @Test
    void removesComment() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            RtRepoCommentTest.comment(container, 3_000_000_000L).remove();
            MatcherAssert.assertThat(
                "Comment is not removed with DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void removesCommentAtCommentUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            RtRepoCommentTest.comment(container, 3_000_000_000L).remove();
            MatcherAssert.assertThat(
                "Comment is removed at a wrong URI",
                container.take().uri().getPath(),
                Matchers.equalTo("/repos/octocat/hello/comments/3000000000")
            );
        }
    }

    @Test
    void failsToRemoveOnUnexpectedStatus() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND, "{}")
            ).start(RandomPort.port())
        ) {
            Assertions.assertThrows(
                AssertionError.class,
                RtRepoCommentTest.comment(container, 2L)::remove,
                "Comment is removed despite an unexpected status"
            );
        }
    }

    @Test
    void comparesByNumber() {
        final Repo repo = RtRepoCommentTest.repo();
        MatcherAssert.assertThat(
            "Comments are compared wrongly",
            new RtRepoComment(new FakeRequest(), repo, 2L).compareTo(
                new RtRepoComment(new FakeRequest(), repo, 3_000_000_000L)
            ),
            Matchers.lessThan(0)
        );
    }

    private static RepoComment comment(final MkContainer container,
        final long number) {
        return new RtRepoComment(
            new ApacheRequest(container.home()), RtRepoCommentTest.repo(), number
        );
    }

    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("octocat", "hello"))
            .when(repo).coordinates();
        return repo;
    }
}
