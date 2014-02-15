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
package com.jcabi.github.mock;

import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
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
     * @todo #416 MkPullComments should be able to fetch all pull comments of a
     *  repo. Implement {@link MkPullComments#iterate(java.util.Map)}
     *  and don't forget to include a test here. When done, remove this puzzle
     *  and the Ignore annotation of this test method.
     */
    @Test
    @Ignore
    public void iteratesRepoPullComments() throws Exception {
        // To be implemented.
    }

    /**
     * MkPullComments can fetch pull comments for a pull request.
     *
     * @throws Exception If something goes wrong.
     * @todo #416 MkPullComments should be able to fetch all comments of a pull
     *  request. Implement {@link MkPullComments#iterate(int, java.util.Map)}
     *  and don't forget to include a test here. When done, remove this puzzle
     *  and the Ignore annotation of this test method.
     */
    @Test
    @Ignore
    public void iteratesPullRequestComments() throws Exception {
        // To be implemented.
    }

    /**
     * MkPullComments can create a pull comment.
     *
     * @throws Exception If something goes wrong.
     * @todo #416 MkPullComments should be able to create a new pull comment.
     *  Implement {@link MkPullComments#post(String, String, String, int)}
     *  and don't forget to include a test here. When done, remove this puzzle
     *  and the Ignore annotation of this test method.
     */
    @Test
    public void postsPullComment() throws Exception {
        final MkStorage.InFile storage = new MkStorage.InFile();
        // @checkstyle MultipleStringLiteralsCheck (1 line)
        final String body = "body";
        final String commit = "commit_id";
        final String path = "path";
        MkPullCommentsTest.repo(storage).pulls()
            .create("pullrequest1", "head", "base").comments()
            .post(body, commit, path, 1);
        final String[] fields = {body, commit, path};
        for (final String element : fields) {
            MkPullCommentsTest.assertFieldContains(storage, element);
        }
        final List<String> xpath = storage.xml().xpath(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='test/test']/pulls/pull/comments/comment/position/text()"
        );
        MatcherAssert.assertThat(
            xpath.get(0), Matchers.notNullValue()
        );
    }

    /**
     * Create a test repo.
     * @param storage The storage
     * @return Test repo
     * @throws IOException If any I/O error occurs.
     */
    private static Repo repo(
        final MkStorage storage) throws IOException {
        // @checkstyle MultipleStringLiteralsCheck (3 lines)
        final String test = "test";
        return new MkGithub(storage, test).repos().create(
            Json.createObjectBuilder().add("name", test).build()
        );
    }

    /**
     * Helper methos for passing style check.
     * @param storage The storage
     * @param element The element to be tested
     * @throws IOException If any I/O error occurs.
     */
    private static void assertFieldContains(final MkStorage.InFile storage,
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
     * @todo #416 MkPullComments should be able remove a pull comment.
     *  Implement {@link MkPullComments#iterate(java.util.Map)}
     *  and don't forget to include a test here. When done, remove this puzzle
     *  and the Ignore annotation of this test method.
     */
    @Test
    @Ignore
    public void removesPullComment() throws Exception {
        // To be implemented.
    }

    /**
     * Generate pull comments for test.
     * @return The pull comments
     * @throws IOException If an IO Exception occurs
     */
    private PullComments comments() throws IOException {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        ).pulls().create("hello", "", "").comments();
    }

}
