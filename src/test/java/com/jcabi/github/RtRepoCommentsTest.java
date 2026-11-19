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
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtRepoComments}.
 *
 * @since 2.0
 */
@ExtendWith(RandomPort.class)
final class RtRepoCommentsTest {

    /**
     * SHA of the commit used in tests.
     */
    private static final String SHA = "6dcb09b5b57875f334f61aebed695e2e4193db5e";

    @Test
    void fetchesCommentByNumber() {
        MatcherAssert.assertThat(
            "Comment number is wrong",
            new RtRepoComments(new FakeRequest(), RtRepoCommentsTest.repo())
                .get(3_000_000_000L).number(),
            Matchers.equalTo(3_000_000_000L)
        );
    }

    @Test
    void iteratesRepoComments() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtRepoCommentsTest.comments(1L, 3_000_000_000L)
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Comments are not iterated",
                RtRepoCommentsTest.numbers(
                    RtRepoCommentsTest.comments(container).iterate()
                ),
                Matchers.contains(1L, 3_000_000_000L)
            );
        }
    }

    @Test
    void iteratesRepoCommentsFromRepoUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtRepoCommentsTest.comments(1L)
            ).start(RandomPort.port())
        ) {
            RtRepoCommentsTest.comments(container).iterate().iterator().next();
            MatcherAssert.assertThat(
                "Comments are iterated from a wrong URI",
                container.take().uri().getPath(),
                Matchers.equalTo("/repos/octocat/hello/comments")
            );
        }
    }

    @Test
    void iteratesCommitComments() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtRepoCommentsTest.comments(5L, 7L)
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Commit comments are not iterated",
                RtRepoCommentsTest.numbers(
                    RtRepoCommentsTest.comments(container)
                        .iterate(RtRepoCommentsTest.SHA)
                ),
                Matchers.contains(5L, 7L)
            );
        }
    }

    @Test
    void iteratesCommitCommentsFromCommitUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtRepoCommentsTest.comments(5L)
            ).start(RandomPort.port())
        ) {
            RtRepoCommentsTest.comments(container)
                .iterate(RtRepoCommentsTest.SHA).iterator().next();
            MatcherAssert.assertThat(
                "Commit comments are iterated from a wrong URI",
                container.take().uri().getPath(),
                Matchers.equalTo(
                    String.format(
                        "/repos/octocat/hello/commits/%s/comments",
                        RtRepoCommentsTest.SHA
                    )
                )
            );
        }
    }

    @Test
    void postsCommitCommentWithPost() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtRepoCommentsTest.created(9L)
            ).start(RandomPort.port())
        ) {
            RtRepoCommentsTest.comments(container)
                .post(RtRepoCommentsTest.SHA, "Great stuff");
            MatcherAssert.assertThat(
                "Comment is not posted with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void postsCommitCommentToCommitUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtRepoCommentsTest.created(9L)
            ).start(RandomPort.port())
        ) {
            RtRepoCommentsTest.comments(container)
                .post(RtRepoCommentsTest.SHA, "Nice commit");
            MatcherAssert.assertThat(
                "Comment is posted to a wrong URI",
                container.take().uri().getPath(),
                Matchers.equalTo(
                    String.format(
                        "/repos/octocat/hello/commits/%s/comments",
                        RtRepoCommentsTest.SHA
                    )
                )
            );
        }
    }

    @Test
    void postsCommitCommentWithBodyOnly() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtRepoCommentsTest.created(9L)
            ).start(RandomPort.port())
        ) {
            RtRepoCommentsTest.comments(container)
                .post(RtRepoCommentsTest.SHA, "Whole commit");
            MatcherAssert.assertThat(
                "Comment on the whole commit is posted with wrong JSON",
                RtRepoCommentsTest.sent(container),
                Matchers.equalTo(
                    Json.createObjectBuilder()
                        .add("body", "Whole commit")
                        .build()
                )
            );
        }
    }

    @Test
    void postsInlineCommitComment() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtRepoCommentsTest.created(9L)
            ).start(RandomPort.port())
        ) {
            RtRepoCommentsTest.comments(container).post(
                RtRepoCommentsTest.SHA, "Typo here", "src/Main.java", 4
            );
            MatcherAssert.assertThat(
                "In-line comment is posted with wrong JSON",
                RtRepoCommentsTest.sent(container),
                Matchers.equalTo(
                    Json.createObjectBuilder()
                        .add("body", "Typo here")
                        .add("path", "src/Main.java")
                        .add("position", 4)
                        .build()
                )
            );
        }
    }

    @Test
    void returnsPostedComment() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                RtRepoCommentsTest.created(3_000_000_000L)
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Posted comment has a wrong number",
                RtRepoCommentsTest.comments(container)
                    .post(RtRepoCommentsTest.SHA, "Hello").number(),
                Matchers.equalTo(3_000_000_000L)
            );
        }
    }

    @Test
    void failsToPostOnUnexpectedStatus() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND, "{}")
            ).start(RandomPort.port())
        ) {
            final RepoComments comments = RtRepoCommentsTest.comments(container);
            Assertions.assertThrows(
                AssertionError.class,
                () -> comments.post(RtRepoCommentsTest.SHA, "Unknown commit"),
                "Comment is posted despite an unexpected status"
            );
        }
    }

    private static RepoComments comments(final MkContainer container) {
        return new RtRepoComments(
            new ApacheRequest(container.home()), RtRepoCommentsTest.repo()
        );
    }

    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("octocat", "hello"))
            .when(repo).coordinates();
        return repo;
    }

    private static List<Long> numbers(final Iterable<RepoComment> comments) {
        final List<Long> numbers = new ArrayList<>(0);
        for (final RepoComment comment : comments) {
            numbers.add(comment.number());
        }
        return numbers;
    }

    private static MkAnswer comments(final long... numbers) {
        final JsonArrayBuilder array = Json.createArrayBuilder();
        for (final long number : numbers) {
            array.add(Json.createObjectBuilder().add("id", number));
        }
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK, array.build().toString()
        );
    }

    private static MkAnswer created(final long number) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            Json.createObjectBuilder()
                .add("id", number)
                .add("commit_id", RtRepoCommentsTest.SHA)
                .build().toString()
        );
    }

    private static JsonObject sent(final MkContainer container) {
        try (
            JsonReader reader = Json.createReader(
                new StringReader(container.take().body())
            )
        ) {
            return reader.readObject();
        }
    }
}
