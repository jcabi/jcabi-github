/**
 * Copyright (c) 2013-2017, jcabi.com
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

import com.jcabi.aspects.Tv;
import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import java.util.Collections;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPullComments}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
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
     */
    @Test
    public void iteratesRepoPullComments() throws Exception {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(repo()).when(pull).repo();
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(comment("comment 1"))
                    .add(comment("comment 2"))
                    .build().toString()
            )
        ).start();
        try {
            final RtPullComments comments = new RtPullComments(
                new JdkRequest(container.home()), pull
            );
            MatcherAssert.assertThat(
                comments.iterate(Collections.<String, String>emptyMap()),
                Matchers.<PullComment>iterableWithSize(2)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtPullComments can fetch pull comments for a pull request.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void iteratesPullRequestComments() throws Exception {
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(repo()).when(pull).repo();
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(comment("comment 3"))
                    .add(comment("comment 4"))
                    .build().toString()
            )
        ).start();
        try {
            final RtPullComments comments = new RtPullComments(
                new JdkRequest(container.home()), pull
            );
            MatcherAssert.assertThat(
                comments.iterate(1, Collections.<String, String>emptyMap()),
                Matchers.<PullComment>iterableWithSize(2)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtPullComments can post a new a pull comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsPullComment() throws Exception {
        // @checkstyle MultipleStringLiterals (3 line)
        final String body = "test-body";
        final String commit = "test-commit-id";
        final String path = "test-path";
        final int position = 4;
        final String response = pulls(body, commit, path, position).toString();
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, response)
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response))
            .start();
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(repo()).when(pull).repo();
        final RtPullComments pullComments = new RtPullComments(
            new ApacheRequest(container.home()),
                pull
        );
        try {
            final PullComment pullComment = pullComments.post(
                body, commit, path, position
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                new PullComment.Smart(pullComment).commitId(),
                Matchers.equalTo(commit)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtPullComments can reply to an existing pull comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsPullCommentReply() throws Exception {
        final String body = "test-body";
        final int number = 4;
        final String response = Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (2 line)
            .add("id", Tv.BILLION)
            .add("body", body)
            .add("in_reply_to", number)
            .build()
            .toString();
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, response)
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response))
            .start();
        final Pull pull = Mockito.mock(Pull.class);
        Mockito.doReturn(repo()).when(pull).repo();
        final RtPullComments pullComments = new RtPullComments(
            new ApacheRequest(container.home()),
                pull
        );
        try {
            final PullComment pullComment = pullComments.reply(
                body, number
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                new PullComment.Smart(pullComment).reply(),
                Matchers.equalTo(number)
            );
        } finally {
            container.stop();
        }
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
        final Repo repository = repo();
        Mockito.doReturn(repository).when(pull).repo();
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
                Matchers.endsWith(
                    String.format(
                        "/repos/johnny/%s/pulls/0/comments/2",
                        repository.coordinates().repo()
                    )
                )
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
        return new MkGithub("johnny").randomRepo();
    }

    /**
     * Create and return JsonObject to test.
     * @param body The body
     * @param commit Commit
     * @param path Path
     * @param position Position
     * @return JsonObject
     * @throws Exception - if anything goes wrong
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    private static JsonObject pulls(final String body, final String commit,
        final String path, final int position) throws Exception {
        return Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (2 line)
            .add("id", Tv.BILLION)
            .add("body", body)
            .add("commit_id", commit)
            .add("path", path)
            .add("position", position)
            .build();
    }

    /**
     * Create and return JsonObject to test.
     * @param bodytext Body of the comment
     * @return JsonObject
     * @throws Exception If something goes wrong.
     */
    private static JsonObject comment(final String bodytext)
        throws Exception {
        return Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (2 line)
            .add("id", 1)
            .add("body", bodytext)
            .build();
    }

}
