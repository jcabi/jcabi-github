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
import org.hamcrest.collection.IsIterableWithSize;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtComment}.
 * @since 0.7
 */
@ExtendWith(RandomPort.class)
final class RtCommentTest {

    @Test
    void comparesSmallerComment() throws IOException {
        final Issue issue = new MkGitHub().randomRepo()
            .issues().create("title", "body");
        MatcherAssert.assertThat(
            "Comment is not less than the greater one",
            new RtComment(new FakeRequest(), issue, 1).compareTo(
                new RtComment(new FakeRequest(), issue, 2)
            ),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerComment() throws IOException {
        final Issue issue = new MkGitHub().randomRepo()
            .issues().create("title", "body");
        MatcherAssert.assertThat(
            "Comment is not greater than the smaller one",
            new RtComment(new FakeRequest(), issue, 2).compareTo(
                new RtComment(new FakeRequest(), issue, 1)
            ),
            Matchers.greaterThan(0)
        );
    }

    /**
     * RtComment can return its issue (owner).
     */
    @Test
    void returnsItsIssue() throws IOException {
        final Issue issue = new MkGitHub().randomRepo()
            .issues().create("testing1", "issue1");
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtComment(new FakeRequest(), issue, 1).issue(),
            Matchers.is(issue)
        );
    }

    @Test
    void returnsItsNumber() throws IOException {
        final long num = 10L;
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtComment(
                new FakeRequest(),
                new MkGitHub().randomRepo()
                    .issues().create("testing2", "issue2"),
                num
            ).number(),
            Matchers.is(num)
        );
    }

    /**
     * This tests that the remove() method is working fine.
     */
    @Test
    void removesComment() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            RtCommentTest.comment(container).remove();
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void returnsItsJSon() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "{\"body\":\"test5\"}")
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Values are not equal",
                RtCommentTest.comment(container).json().getString("body"),
                Matchers.is("test5")
            );
        }
    }

    @Test
    void patchesComment() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            RtCommentTest.comment(container).patch(
                Json.createObjectBuilder()
                    .add("title", "test comment").build()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(), Matchers.equalTo(Request.PATCH)
            );
        }
    }

    @Test
    void reacts() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            RtCommentTest.comment(container)
                .react(new Reaction.Simple(Reaction.HEART));
            MatcherAssert.assertThat(
                "Assertion failed",
                container.take().method(),
                new IsEqual<>(Request.POST)
            );
        }
    }

    @Test
    void reactions() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder().add(
                        Json.createObjectBuilder()
                        .add("id", "1")
                        .add("content", "heart")
                        .build()
                    ).build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Assertion failed",
                RtCommentTest.comment(container).reactions(),
                new IsIterableWithSize<>(new IsEqual<>(1))
            );
        }
    }

    /**
     * This tests that the toString() method is not empty.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void givesNotEmptyToString() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Text of the comment is empty",
                RtCommentTest.comment(container).toString(),
                Matchers.not(Matchers.is(Matchers.emptyOrNullString()))
            );
        }
    }

    /**
     * This tests that the toString() method ends with the number.
     * @throws IOException If there is any I/O problem
     */
    @Test
    void givesToStringWithNumber() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Text of the comment does not end with its number",
                RtCommentTest.comment(container).toString(),
                Matchers.endsWith("10")
            );
        }
    }

    /**
     * Comment served by the given container.
     * @param container Container to serve the comment
     * @return Comment
     * @throws IOException If there is any I/O problem
     */
    private static RtComment comment(final MkContainer container)
        throws IOException {
        return new RtComment(
            new ApacheRequest(container.home()),
            new MkGitHub().randomRepo().issues().create("testing6", "issue6"),
            10
        );
    }
}
