/**
 * Copyright (c) 2013-2014, jcabi.com
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
package com.jcabi.github.mock;

import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPullComments}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
public final class MkPullCommentsTest {

    /**
     * MkPullComments can fetch a single comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void fetchesPullComment() throws Exception {
        final PullComments comments = this.comments();
        final PullComment comment = comments.post("comment", "commit", "/", 1);
        MatcherAssert.assertThat(
            comments.get(comment.number()).number(),
            Matchers.equalTo(comment.number())
        );
    }

    /**
     * MkPullComments can fetch all pull comments for a repo.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void iteratesRepoPullComments() throws Exception {
        final PullComments comments = comments();
        comments.pull()
            .repo()
            .pulls()
            .create("new", "", "")
            .comments()
            .post("new pull comment", "new commit", "/p", 1);
        comments.post("test 1", "tesst 1", "/test1", 1);
        MatcherAssert.assertThat(
            comments.iterate(
                comments.pull().number(),
                Collections.<String, String>emptyMap()
            ),
            Matchers.<PullComment>iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            comments.iterate(
                Collections.<String, String>emptyMap()
            ),
            Matchers.<PullComment>iterableWithSize(2)
        );
    }

    /**
     * MkPullComments can fetch pull comments for a pull request.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void iteratesPullRequestComments() throws Exception {
        final PullComments comments = comments();
        comments.post("comment 1", "commit 1", "/commit1", 1);
        comments.post("comment 2", "commit 2", "/commit2", 2);
        MatcherAssert.assertThat(
            comments.iterate(
                comments.pull().number(),
                Collections.<String, String>emptyMap()
            ),
            Matchers.<PullComment>iterableWithSize(2)
        );
    }

    /**
     * MkPullComments can create a pull comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void postsPullComment() throws Exception {
        final MkStorage storage = new MkStorage.InFile();
        final String commit = "commit_id";
        final String path = "path";
        final String bodytext = "some text as a body";
        MkPullCommentsTest.repo(storage).pulls()
            .create("pullrequest1", "head", "base").comments()
            .post(bodytext, commit, path, 1);
        final String[] fields = {commit, path};
        for (final String element : fields) {
            MkPullCommentsTest.assertFieldContains(storage, element);
        }
        final List<String> position = storage.xml().xpath(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='test/test']/pulls/pull/comments/comment/position/text()"
        );
        MatcherAssert.assertThat(
            position.get(0), Matchers.notNullValue()
        );
        final List<String> body = storage.xml().xpath(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='test/test']/pulls/pull/comments/comment/body/text()"
        );
        MatcherAssert.assertThat(body.get(0), Matchers.equalTo(bodytext));
    }

    /**
     * MkPullComments can reply to an existing pull comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsPullCommentReply() throws Exception {
        final PullComments comments = this.comments();
        final int orig = comments.post(
            "Orig Comment",
            "6dcb09b5b57875f334f61aebed695e2e4193db5e",
            "file1.txt",
            1
        ).number();
        final String body = "Reply Comment";
        final JsonObject reply = comments.reply(body, orig).json();
        MatcherAssert.assertThat(
            reply.getString("body"),
            Matchers.is(body)
        );
        MatcherAssert.assertThat(
            reply.getString("original_position"),
            Matchers.is(Integer.toString(orig))
        );
    }

    /**
     * MkPullComments can remove a pull comment.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void removesPullComment() throws Exception {
        final PullComments comments = this.comments();
        final int orig = comments.post(
            "Origg Comment",
            "6dcb09b5b57875f334f61aebed695e2e4193db5d",
            "file2.txt",
            1
        ).number();
        MatcherAssert.assertThat(
            comments.iterate(
                orig, Collections.<String, String>emptyMap()
            ),
            Matchers.<PullComment>iterableWithSize(1)
        );
        comments.remove(orig);
        MatcherAssert.assertThat(
            comments.iterate(
                orig, Collections.<String, String>emptyMap()
            ),
            Matchers.<PullComment>iterableWithSize(0)
        );
    }

    /**
     * Generate pull comments for test.
     * @return The pull comments
     * @throws IOException If an IO Exception occurs
     */
    private PullComments comments() throws IOException {
        return new MkGithub().repos().create(
            // @checkstyle MultipleStringLiteralsCheck (3 lines)
            Json.createObjectBuilder().add("name", "test").build()
        ).pulls().create("hello", "", "").comments();
    }

    /**
     * Create a test repo.
     * @param storage The storage
     * @return Test repo
     * @throws IOException If any I/O error occurs.
     */
    private static Repo repo(
        final MkStorage storage) throws IOException {
        final String login = "test";
        return new MkGithub(storage, login).repos().create(
            Json.createObjectBuilder().add("name", login).build()
        );
    }

    /**
     * Assert if fields doesn't contain value.
     * @param storage The storage
     * @param element The element to be tested and the value.
     * @throws IOException If any I/O error occurs.
     */
    private static void assertFieldContains(final MkStorage storage,
        final String element) throws IOException {
        final String xpath = String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='test/test']/pulls/pull/comments/comment/%s/text()",
            element
        );
        MatcherAssert.assertThat(
            storage.xml().xpath(xpath).get(0),
            Matchers.is(element)
        );
    }

}
