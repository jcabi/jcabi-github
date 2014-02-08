/**
 * Copyright (c) 2012-2013, JCabi.com
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
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPullComments}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class RtPullCommentsTest {

    /**
     * RtPullComments can fetch a single comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void fetchesPullComment() throws Exception {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(repo()).when(pull).repo();
        final RtPullComments comments =
            new RtPullComments(new FakeRequest(), pull);
        MatcherAssert.assertThat(
            comments.get(1),
            Matchers.notNullValue()
        );
    }

    /**
     * RtPullComments can fetch all pull comments for a repo.
     *
     * @throws Exception If something goes wrong.
     * @todo #416 RtPullComments should be able to fetch all pull comments of a
     *  repo. Implement {@link RtPullComments#iterate(java.util.Map)}
     *  and don't forget to include a test here. When done, remove this puzzle
     *  and the Ignore annotation of this test method.
     */
    @Test
    @Ignore
    public void iteratesRepoPullComments() throws Exception {
        // To be implemented.
    }

    /**
     * RtPullComments can fetch pull comments for a pull request.
     *
     * @throws Exception If something goes wrong.
     * @todo #416 RtPullComments should be able to fetch all comments of a pull
     *  request. Implement {@link RtPullComments#iterate(int, java.util.Map)}
     *  and don't forget to include a test here. When done, remove this puzzle
     *  and the Ignore annotation of this test method.
     */
    @Test
    @Ignore
    public void iteratesPullRequestComments() throws Exception {
        // To be implemented.
    }

    /**
     * RtPullComments can post a new a pull comment.
     *
     * @throws Exception If something goes wrong.
     * @todo #416 RtPullComments should be able to create a new pull comment.
     *  Implement {@link RtPullComments#post(String, String, String, int)}
     *  and don't forget to include a test here. When done, remove this puzzle
     *  and the Ignore annotation of this test method.
     */
    @Test
    @Ignore
    public void createsPullComment() throws Exception {
        // To be implemented.
    }

    /**
     * RtPullComments can reply to an existing pull comment.
     *
     * @throws Exception If something goes wrong.
     * @todo #416 RtPullComments should be able to fetch all pull comments of a
     *  repo. Implement {@link RtPullComments#reply(String, int))}
     *  and don't forget to include a test here. When done, remove this puzzle
     *  and the Ignore annotation of this test method.
     */
    @Test
    @Ignore
    public void createsPullCommentReply() throws Exception {
        // To be implemented.
    }

    /**
     * RtPullComments can remove a pull comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void removesPullComment() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
        ).start();
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(repo()).when(pull).repo();
        final RtPullComments comments =
            new RtPullComments(new ApacheRequest(container.home()), pull);
        try {
            comments.remove(2);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(), Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith("/repos/johnny/test/pulls/comments/2")
            );
        } finally {
            container.stop();
        }
    }

    /**
     * This method returns a Repo for testing.
     * @return Repo - a repo to be used for test.
     * @throws Exception - if anything goes wrong.
     */
    private static Repo repo() throws Exception {
        return new MkGithub("johnny").repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
    }

}
