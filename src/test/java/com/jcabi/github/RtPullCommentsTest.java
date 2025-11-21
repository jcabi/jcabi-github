/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
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
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
@ExtendWith(RandomPort.class)
final class RtPullCommentsTest {

    /**
     * RtPullComments can fetch a single comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    void fetchesPullComment() throws Exception {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(RtPullCommentsTest.repo()).when(pull).repo();
        final RtPullComments comments =
            new RtPullComments(new FakeRequest(), pull);
        MatcherAssert.assertThat(
            "Value is null",
            comments.get(1),
            Matchers.notNullValue()
        );
    }

    /**
     * RtPullComments can fetch all pull comments for a repo.
     *
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
            final RtPullComments comments = new RtPullComments(
                new JdkRequest(container.home()), pull
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                comments.iterate(Collections.emptyMap()),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtPullComments can fetch pull comments for a pull request.
     *
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
            final RtPullComments comments = new RtPullComments(
                new JdkRequest(container.home()), pull
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                comments.iterate(1, Collections.emptyMap()),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtPullComments can post a new a pull comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    void createsPullComment() throws Exception {
        // @checkstyle MultipleStringLiterals (3 line)
        final String body = "test-body";
        final String commit = "test-commit-id";
        final String path = "test-path";
        final int position = 4;
        final String response = RtPullCommentsTest.pulls(body, commit, path, position).toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, response)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response))
                .start(RandomPort.port())
        ) {
            final Pull pull = Mockito.mock(Pull.class);
            Mockito.doReturn(RtPullCommentsTest.repo()).when(pull).repo();
            final RtPullComments comments = new RtPullComments(
                new ApacheRequest(container.home()),
                    pull
            );
            final PullComment comment = comments.post(
                body, commit, path, position
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                new PullComment.Smart(comment).commitId(),
                Matchers.equalTo(commit)
            );
            container.stop();
        }
    }

    /**
     * RtPullComments can reply to an existing pull comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    void createsPullCommentReply() throws Exception {
        final String body = "test-body";
        final int number = 4;
        final String response = Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (2 line)
            .add("id", Tv.BILLION)
            .add("body", body)
            .add("in_reply_to", number)
            .build()
            .toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, response)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response))
                .start(RandomPort.port())
        ) {
            final Pull pull = Mockito.mock(Pull.class);
            Mockito.doReturn(RtPullCommentsTest.repo()).when(pull).repo();
            final RtPullComments comments = new RtPullComments(
                new ApacheRequest(container.home()),
                    pull
            );
            final PullComment comment = comments.reply(
                body, number
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                new PullComment.Smart(comment).reply(),
                Matchers.equalTo(number)
            );
            container.stop();
        }
    }

    /**
     * RtPullComments can remove a pull comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    void removesPullComment() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            final Pull pull = Mockito.mock(Pull.class);
            final Repo repository = RtPullCommentsTest.repo();
            Mockito.doReturn(repository).when(pull).repo();
            final RtPullComments comments =
                new RtPullComments(new ApacheRequest(container.home()), pull);
            comments.remove(2);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(), Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                query.uri().toString(),
                Matchers.endsWith(
                    String.format(
                        "/repos/johnny/%s/pulls/0/comments/2",
                        repository.coordinates().repo()
                    )
                )
            );
            container.stop();
        }
    }

    /**
     * This method returns a Repo for testing.
     * @return Repo - a repo to be used for test.
     */
    private static Repo repo() throws IOException {
        return new MkGitHub("johnny").randomRepo();
    }

    /**
     * Create and return JsonObject to test.
     * @param body The body
     * @param commit Commit
     * @param path Path
     * @param position Position
     * @return JsonObject
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    private static JsonObject pulls(final String body, final String commit,
        final String path, final int position) {
        return Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (2 line)
            .add("id", Tv.BILLION)
            .add("body", body)
            .add("commit_id", commit)
            .add("path", path)
            .add("position", position)
            .build();
    }

    /**
     * Create and return JsonObject to test.
     * @param bodytext Body of the comment
     * @return JsonObject
     */
    private static JsonObject comment(final String bodytext) {
        return Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (2 line)
            .add("id", 1)
            .add("body", bodytext)
            .build();
    }

}
