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
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPullComment}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtPullCommentTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void comparesSmallerComment() throws IOException {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(new MkGitHub().randomRepo()).when(pull).repo();
        MatcherAssert.assertThat(
            "Comment is not less than the greater one",
            new RtPullComment(new FakeRequest(), pull, 1).compareTo(
                new RtPullComment(new FakeRequest(), pull, 2)
            ),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerComment() throws IOException {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(new MkGitHub().randomRepo()).when(pull).repo();
        MatcherAssert.assertThat(
            "Comment is not greater than the smaller one",
            new RtPullComment(new FakeRequest(), pull, 2).compareTo(
                new RtPullComment(new FakeRequest(), pull, 1)
            ),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void comparesEqualComments() throws IOException {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(new MkGitHub().randomRepo()).when(pull).repo();
        MatcherAssert.assertThat(
            "Equal comments are not the same",
            new RtPullComment(new FakeRequest(), pull, 1).compareTo(
                new RtPullComment(new FakeRequest(), pull, 1)
            ),
            Matchers.equalTo(0)
        );
    }

    @Test
    void canDescribeAsJson() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK, "{\"body\":\"test\"}"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Comment has a wrong body",
                RtPullCommentTest.comment(container, 1)
                    .json().getString("body"),
                Matchers.is("test")
            );
        }
    }

    @Test
    void fetchesJsonFromCorrectUri() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK, "{\"body\":\"test\"}"
                )
            ).start(RandomPort.port())
        ) {
            RtPullCommentTest.comment(container, 1).json();
            MatcherAssert.assertThat(
                "JSON of the comment is fetched from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/repos/joe/blueharvest/pulls/comments/1")
            );
        }
    }

    @Test
    void patchesCommentThroughPatchMethod() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            RtPullCommentTest.patch(container);
            MatcherAssert.assertThat(
                "Comment is not patched through PATCH",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
        }
    }

    @Test
    void sendsPatchInRequestBody() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            RtPullCommentTest.patch(container);
            MatcherAssert.assertThat(
                "Patch is not sent in the request body",
                container.take().body(),
                Matchers.containsString("{\"body\":\"test comment\"}")
            );
        }
    }

    @Test
    void patchesCommentAtCorrectUri() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            RtPullCommentTest.patch(container);
            MatcherAssert.assertThat(
                "Comment is patched at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/repos/joe/blueharvest/pulls/comments/2")
            );
        }
    }

    @Test
    @Disabled
    void reacts() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            final RtPullComment comment = new RtPullComment(
                new ApacheRequest(container.home()),
                new MkGitHub().randomRepo().pulls().create(
                    "Reaction adding test",
                    "This is a test for adding a reaction",
                    "Base"
                ), 2
            );
            comment.react(new Reaction.Simple(Reaction.HEART));
            MatcherAssert.assertThat(
                "Pull comment was unable to react",
                comment.reactions(),
                new IsCollectionWithSize<>(
                    new IsEqual<>(1)
                )
            );
        }
    }

    /**
     * Comment served by the given container.
     * @param container Container to serve the comment
     * @param number Number of the comment
     * @return The comment
     * @throws IOException If there is any I/O problem
     */
    private static RtPullComment comment(
        final MkContainer container,
        final int number
    ) throws IOException {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(RtPullCommentTest.repo()).when(pull).repo();
        return new RtPullComment(
            new ApacheRequest(container.home()), pull, number
        );
    }

    /**
     * Patch the comment served by the given container.
     * @param container Container to serve the comment
     * @throws IOException If there is any I/O problem
     */
    private static void patch(final MkContainer container) throws IOException {
        RtPullCommentTest.comment(container, 2).patch(
            Json.createObjectBuilder().add("body", "test comment").build()
        );
    }

    /**
     * This method returns a Repo for testing.
     * @return Repo - a repo to be used for test
     */
    private static Repo repo() throws IOException {
        return new MkGitHub("joe").repos().create(
            new Repos.RepoCreate("blueharvest", false)
        );
    }
}
