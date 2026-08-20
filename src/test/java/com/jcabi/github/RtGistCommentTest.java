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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtGistComment}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtGistCommentTest {

    /**
     * Name of the body property of a comment.
     */
    private static final String BODY = "body";

    /**
     * Name of the identifier property of a comment.
     */
    private static final String ID = "id";

    /**
     * RtGistComment can patch comment and return new json.
     * @throws IOException if has some problems with json parsing.
     */
    @Test
    void patchAndCheckJsonGistComment() throws IOException {
        final String patched = "some patchedbody";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtGistCommentTest.answer("somebody"))
                .next(RtGistCommentTest.answer(patched))
                .next(RtGistCommentTest.answer("somebody"))
                .start(RandomPort.port());
            MkContainer gists = new MkGrizzlyContainer()
                .start(RandomPort.port())
        ) {
            final RtGistComment comment = new RtGistComment(
                new ApacheRequest(container.home()),
                new RtGist(
                    new MkGitHub(),
                    new ApacheRequest(gists.home()), "someName"
                ), 1
            );
            comment.patch(
                Json.createObjectBuilder()
                    .add(RtGistCommentTest.BODY, patched)
                    .add(RtGistCommentTest.ID, 1)
                    .build()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                comment.json().getString(RtGistCommentTest.BODY),
                Matchers.equalTo(patched)
            );
            container.stop();
            gists.stop();
        }
    }

    /**
     * RtGistComment can remove comment.
     * @throws IOException if has some problems with json parsing.
     */
    @Test
    void removeGistComment() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            new RtGistComment(
                new ApacheRequest(container.home()),
                new RtGist(
                    new MkGitHub(),
                    new FakeRequest().withStatus(
                        HttpURLConnection.HTTP_NO_CONTENT
                    ),
                    "gistName"
                ), 1
            ).remove();
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }

    private static MkAnswer answer(final String body) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add(RtGistCommentTest.BODY, body)
                .add(RtGistCommentTest.ID, 1)
                .build().toString()
        );
    }
}
