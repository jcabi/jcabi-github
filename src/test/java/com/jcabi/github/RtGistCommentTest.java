/**
 * Copyright (c) 2013-2015, jcabi.com
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

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtGistComment}.
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public class RtGistCommentTest {

    /**
     * The rule for skipping test if there's BindException.
     *  and make MkGrizzlyContainers use port() given by this resource to avoid
     *  tests fail with BindException.
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
        final MkContainer container =
            new MkGrizzlyContainer().next(first).next(second).next(third)
                .start(this.resource.port());
        final MkContainer gistContainer = new MkGrizzlyContainer()
                .start(this.resource.port());
        final RtGist gist =
            new RtGist(
                new MkGithub(),
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
            comment.json().getString(bodyString),
            Matchers.equalTo(patchedBody)
        );
        container.stop();
        gistContainer.stop();
    }

    /**
     * RtGistComment can remove comment.
     * @throws IOException if has some problems with json parsing.
     */
    @Test
    public final void removeGistComment() throws IOException {
        final int identifier = 1;
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
        ).start(this.resource.port());
        final RtGist gist = new RtGist(
            new MkGithub(),
            new FakeRequest().withStatus(HttpURLConnection.HTTP_NO_CONTENT),
            "gistName"
        );
        final RtGistComment comment = new RtGistComment(
            new ApacheRequest(container.home()), gist, identifier
        );
        comment.remove();
        MatcherAssert.assertThat(
            container.take().method(),
            Matchers.equalTo(Request.DELETE)
        );
        container.stop();
    }
}
