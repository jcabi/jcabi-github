/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtGistComments}.
 *
 */
public final class RtGistCommentsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtGistComments can get a single comment.
     * @throws Exception if some problem inside
     */
    @Test
    public void getComment() throws Exception {
        final String body = "Just commenting";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    comment(body).toString()
                )
            ).start(this.resource.port())) {
            final Gist gist = Mockito.mock(Gist.class);
            Mockito.doReturn("1").when(gist).identifier();
            final RtGistComments comments = new RtGistComments(
                new JdkRequest(container.home()),
                gist
            );
            final GistComment comment = comments.get(1);
            MatcherAssert.assertThat(
                new GistComment.Smart(comment).body(),
                Matchers.equalTo(body)
            );
        }
    }

    /**
     * RtGistComments can iterate comments.
     * @throws Exception if there is any error
     */
    @Test
    public void iterateComments() throws Exception {
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(comment("comment 1"))
                    .add(comment("comment 2"))
                    .build().toString()
            )
        ).start(this.resource.port())) {
            final Gist gist = Mockito.mock(Gist.class);
            Mockito.doReturn("2").when(gist).identifier();
            final RtGistComments comments = new RtGistComments(
                new JdkRequest(container.home()),
                gist
            );
            MatcherAssert.assertThat(
                comments.iterate(),
                Matchers.<GistComment>iterableWithSize(2)
            );
        }
    }

    /**
     * RtGistComments can create a comment.
     * @throws Exception if there is any error
     */
    @Test
    public void postComment() throws Exception {
        final String body = "new commenting";
        final MkAnswer answer = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            comment(body).toString()
        );
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                comment(body).toString()
            )
        ).next(answer).start(this.resource.port())) {
            final Gist gist = Mockito.mock(Gist.class);
            Mockito.doReturn("3").when(gist).identifier();
            final RtGistComments comments = new RtGistComments(
                new JdkRequest(container.home()),
                gist
            );
            final GistComment comment = comments.post(body);
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
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
