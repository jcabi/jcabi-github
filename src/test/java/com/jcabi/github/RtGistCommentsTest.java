/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtGistComments}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtGistCommentsTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void fetchesComment() throws IOException {
        final String body = "Just commenting";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtGistCommentsTest.comment(body).toString()
                )
            ).start(RandomPort.port())
        ) {
            final Gist gist = Mockito.mock(Gist.class);
            Mockito.doReturn("1").when(gist).identifier();
            MatcherAssert.assertThat(
                "Values are not equal",
                new GistComment.Smart(
                    new RtGistComments(
                        new JdkRequest(container.home()),
                        gist
                    ).get(1)
                ).body(),
                Matchers.equalTo(body)
            );
        }
    }

    @Test
    void iterateComments() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtGistCommentsTest.comment("comment 1"))
                        .add(RtGistCommentsTest.comment("comment 2"))
                        .build().toString()
                )
                ).start(RandomPort.port())
        ) {
            final Gist gist = Mockito.mock(Gist.class);
            Mockito.doReturn("2").when(gist).identifier();
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtGistComments(
                    new JdkRequest(container.home()),
                    gist
                ).iterate(),
                Matchers.iterableWithSize(2)
            );
        }
    }

    @Test
    void postsCommentWithPost() throws IOException {
        final String body = "new commenting";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtGistCommentsTest.answer(body))
                .start(RandomPort.port())
        ) {
            RtGistCommentsTest.comments(container).post(body);
            MatcherAssert.assertThat(
                "Comment is not posted with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void postsCommentWithBody() throws IOException {
        final String body = "new commenting";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtGistCommentsTest.answer(body))
                .next(RtGistCommentsTest.fetched(body))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Posted comment has a wrong body",
                new GistComment.Smart(
                    RtGistCommentsTest.comments(container).post(body)
                ).body(),
                Matchers.equalTo(body)
            );
        }
    }

    /**
     * Comments served by the given container.
     * @param container Container to serve the comments
     * @return Comments
     * @throws IOException If there is any I/O problem
     */
    private static RtGistComments comments(final MkContainer container)
        throws IOException {
        final Gist gist = Mockito.mock(Gist.class);
        Mockito.doReturn("3").when(gist).identifier();
        return new RtGistComments(new JdkRequest(container.home()), gist);
    }

    /**
     * Answer with a comment of the given body.
     * @param body Body of the comment
     * @return Answer
     */
    private static MkAnswer answer(final String body) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            RtGistCommentsTest.comment(body).toString()
        );
    }

    /**
     * Answer with a fetched comment of the given body.
     * @param body Body of the comment
     * @return Answer
     */
    private static MkAnswer fetched(final String body) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtGistCommentsTest.comment(body).toString()
        );
    }

    /**
     * Create and return JsonObject to test.
     * @param body The body of the comment
     * @return JsonObject
     */
    private static JsonObject comment(final String body) {
        return Json.createObjectBuilder()
            .add("id", 1)
            .add("body", body)
            .build();
    }
}
