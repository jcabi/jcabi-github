/**
 * Copyright (c) 2013-2025, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
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
