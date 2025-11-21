/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
public final class RtGistCommentsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    public void getComment() throws IOException {
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
            final RtGistComments comments = new RtGistComments(
                new JdkRequest(container.home()),
                gist
            );
            final GistComment comment = comments.get(1);
            MatcherAssert.assertThat(
                "Values are not equal",
                new GistComment.Smart(comment).body(),
                Matchers.equalTo(body)
            );
        }
    }

    @Test
    public void iterateComments() throws IOException {
        try (MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(RtGistCommentsTest.comment("comment 1"))
                    .add(RtGistCommentsTest.comment("comment 2"))
                    .build().toString()
            )
        ).start(RandomPort.port())) {
            final Gist gist = Mockito.mock(Gist.class);
            Mockito.doReturn("2").when(gist).identifier();
            final RtGistComments comments = new RtGistComments(
                new JdkRequest(container.home()),
                gist
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                comments.iterate(),
                Matchers.iterableWithSize(2)
            );
        }
    }

    @Test
    public void postComment() throws IOException {
        final String body = "new commenting";
        final MkAnswer answer = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtGistCommentsTest.comment(body).toString()
        );
        try (MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                RtGistCommentsTest.comment(body).toString()
            )
        ).next(answer).start(RandomPort.port())
        ) {
            final Gist gist = Mockito.mock(Gist.class);
            Mockito.doReturn("3").when(gist).identifier();
            final RtGistComments comments = new RtGistComments(
                new JdkRequest(container.home()),
                gist
            );
            final GistComment comment = comments.post(body);
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                new GistComment.Smart(comment).body(),
                Matchers.equalTo(body)
            );
        }
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
