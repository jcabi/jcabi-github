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
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtGistComment}.
 * @checkstyle ClassDataAbstractionCouplingCheck (150 lines)
 */
public class RtGistCommentTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtGistComment can patch comment and return new json.
     * @throws IOException if has some problems with json parsing.
     */
    @Test
    public final void patchAndCheckJsonGistComment() throws IOException {
        final int identifier = 1;
        final String idString = "id";
        final String bodyString = "body";
        final String body = "somebody";
        final String patchedBody = "some patchedbody";
        final MkAnswer first = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add(bodyString, body)
                .add(idString, identifier)
                .build().toString()
        );
        final MkAnswer second = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add(bodyString, patchedBody)
                .add(idString, identifier)
                .build().toString()
        );
        final MkAnswer third = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createObjectBuilder()
                .add(bodyString, body)
                .add(idString, identifier)
                .build().toString()
        );
        try (
            final MkContainer container =
                new MkGrizzlyContainer().next(first).next(second).next(third)
                    .start(this.resource.port());
            final MkContainer gistContainer = new MkGrizzlyContainer()
                .start(this.resource.port())) {
            final RtGist gist =
                new RtGist(
                    new MkGitHub(),
                    new ApacheRequest(gistContainer.home()), "someName"
                );
            final RtGistComment comment = new RtGistComment(
                new ApacheRequest(container.home()), gist, identifier
            );
            comment.patch(Json.createObjectBuilder()
                .add(bodyString, patchedBody)
                .add(idString, identifier)
                .build()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                comment.json().getString(bodyString),
                Matchers.equalTo(patchedBody)
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
    public final void removeGistComment() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(this.resource.port())) {
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
