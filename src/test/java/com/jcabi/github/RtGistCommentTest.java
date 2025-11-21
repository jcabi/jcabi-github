/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
 * @checkstyle ClassDataAbstractionCouplingCheck (150 lines)
 */
@ExtendWith(RandomPort.class)
final class RtGistCommentTest {

    /**
     * RtGistComment can patch comment and return new json.
     * @throws IOException if has some problems with json parsing.
     */
    @Test
    void patchAndCheckJsonGistComment() throws IOException {
        final int identifier = 1;
        final String idprop = "id";
        final String bodyprop = "body";
        final String body = "somebody";
        final String patched = "some patchedbody";
        final MkAnswer first = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add(bodyprop, body)
                .add(idprop, identifier)
                .build().toString()
        );
        final MkAnswer second = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add(bodyprop, patched)
                .add(idprop, identifier)
                .build().toString()
        );
        final MkAnswer third = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add(bodyprop, body)
                .add(idprop, identifier)
                .build().toString()
        );
        try (
            MkContainer container =
                new MkGrizzlyContainer().next(first).next(second).next(third)
                    .start(RandomPort.port());
            MkContainer gistContainer = new MkGrizzlyContainer()
                .start(RandomPort.port())
        ) {
            final RtGist gist =
                new RtGist(
                    new MkGitHub(),
                    new ApacheRequest(gistContainer.home()), "someName"
                );
            final RtGistComment comment = new RtGistComment(
                new ApacheRequest(container.home()), gist, identifier
            );
            comment.patch(Json.createObjectBuilder()
                .add(bodyprop, patched)
                .add(idprop, identifier)
                .build()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                comment.json().getString(bodyprop),
                Matchers.equalTo(patched)
            );
            container.stop();
            gistContainer.stop();
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
            ).start(RandomPort.port())) {
            final RtGist gist = new RtGist(
                new MkGitHub(),
                new FakeRequest().withStatus(HttpURLConnection.HTTP_NO_CONTENT),
                "gistName"
            );
            final int identifier = 1;
            final RtGistComment comment = new RtGistComment(
                new ApacheRequest(container.home()), gist, identifier
            );
            comment.remove();
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }
}
