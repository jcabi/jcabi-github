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
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPullComments}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtPullCommentsTest {

    /**
     * RtPullComments can fetch a single comment.
     * @throws Exception If something goes wrong.
     */
    @Test
    void fetchesPullComment() throws Exception {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(RtPullCommentsTest.repo()).when(pull).repo();
        MatcherAssert.assertThat(
            "Value is null",
            new RtPullComments(new FakeRequest(), pull).get(1),
            Matchers.notNullValue()
        );
    }

    /**
     * RtPullComments can fetch all pull comments for a repo.
     * @throws Exception If something goes wrong.
     */
    @Test
    void iteratesRepoPullComments() throws Exception {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(RtPullCommentsTest.repo()).when(pull).repo();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtPullCommentsTest.comment("comment 1"))
                        .add(RtPullCommentsTest.comment("comment 2"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtPullComments(
                    new JdkRequest(container.home()), pull
                ).iterate(Collections.emptyMap()),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtPullComments can fetch pull comments for a pull request.
     * @throws Exception If something goes wrong.
     */
    @Test
    void iteratesPullRequestComments() throws Exception {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(RtPullCommentsTest.repo()).when(pull).repo();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtPullCommentsTest.comment("comment 3"))
                        .add(RtPullCommentsTest.comment("comment 4"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtPullComments(
                    new JdkRequest(container.home()), pull
                ).iterate(1, Collections.emptyMap()),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    @Test
    void createsPullCommentWithPost() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    RtPullCommentsTest.pulls(
                        "test-body", "test-commit-id", "test-path", 4
                    ).toString()
                )
            ).start(RandomPort.port())
        ) {
            RtPullCommentsTest.comments(container).post(
                "test-body", "test-commit-id", "test-path", 4
            );
            MatcherAssert.assertThat(
                "Comment is not posted with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsPullCommentForCommit() throws Exception {
        final String commit = "test-commit-id";
        final String response = RtPullCommentsTest.pulls(
            "test-body", commit, "test-path", 4
        ).toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED, response
                )
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Posted comment belongs to a wrong commit",
                new PullComment.Smart(
                    RtPullCommentsTest.comments(container).post(
                        "test-body", commit, "test-path", 4
                    )
                ).commitId(),
                Matchers.equalTo(commit)
            );
        }
    }

    @Test
    void createsPullCommentReplyWithPost() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    RtPullCommentsTest.reply(4).toString()
                )
            ).start(RandomPort.port())
        ) {
            RtPullCommentsTest.comments(container).reply("test-body", 4);
            MatcherAssert.assertThat(
                "Reply is not posted with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsPullCommentReplyToComment() throws Exception {
        final int number = 4;
        final String response = RtPullCommentsTest.reply(number).toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED, response
                )
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Reply is posted to a wrong comment",
                new PullComment.Smart(
                    RtPullCommentsTest.comments(container)
                        .reply("test-body", number)
                ).reply(),
                Matchers.equalTo(number)
            );
        }
    }

    @Test
    void removesPullCommentWithDelete() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            RtPullCommentsTest.comments(container).remove(2);
            MatcherAssert.assertThat(
                "Comment is not removed with DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void removesPullCommentAtCorrectUri() throws Exception {
        final Repo repository = RtPullCommentsTest.repo();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            final Pull pull = Mockito.mock(Pull.class);
            Mockito.doReturn(repository).when(pull).repo();
            new RtPullComments(new ApacheRequest(container.home()), pull)
                .remove(2);
            MatcherAssert.assertThat(
                "Comment is removed at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith(
                    String.format(
                        "/repos/johnny/%s/pulls/0/comments/2",
                        repository.coordinates().repo()
                    )
                )
            );
        }
    }

    private static RtPullComments comments(final MkContainer container)
        throws IOException {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(RtPullCommentsTest.repo()).when(pull).repo();
        return new RtPullComments(new ApacheRequest(container.home()), pull);
    }

    private static JsonObject reply(final int number) {
        return Json.createObjectBuilder()
            .add("id", 1_000_000_000)
            .add("body", "test-body")
            .add("in_reply_to", number)
            .build();
    }

    private static Repo repo() throws IOException {
        return new MkGitHub("johnny").randomRepo();
    }

    private static JsonObject pulls(final String body, final String commit,
        final String path, final int position) {
        return Json.createObjectBuilder()
            .add("id", 1_000_000_000)
            .add("body", body)
            .add("commit_id", commit)
            .add("path", path)
            .add("position", position)
            .build();
    }

    private static JsonObject comment(final String bodytext) {
        return Json.createObjectBuilder()
            .add("id", 1)
            .add("body", bodytext)
            .build();
    }
}
