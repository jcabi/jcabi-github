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

import com.jcabi.github.Pull;
import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import com.jcabi.xml.XML;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import javax.json.Json;

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
     * @todo #416 MkPullComments should be able to fetch a single pull comment.
     *  Implement {@link MkPullComments#get(int)} and don't forget to include a
     *  test here. When done, remove this puzzle and the Ignore annotation of
     *  this test method.
     */
    @Test
    @Ignore
    public void fetchesPullComment() throws Exception {
        // To be implemented.
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
        new MkGithub(storage, "test").repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        ).pulls().create("pullrequest1", "head", "base").comments()
            .post("body", "commit", "path", 1);
        for (final String element : new String[] {"body", "commit", "path"}) {
            MatcherAssert.assertThat(
                    storage.xml().xpath(
                            String.format("/github/repos/repo[@coords='test/test']/pulls/pull/comments/comment/%s/text()", element)).get(0),
                    Matchers.equalTo(element)
            );
        }
        MatcherAssert.assertThat(
            storage.xml().xpath(
                "/github/repos/repo[@coords='test/test']/pulls/pull/comments/comment/position/text()").get(0),
            Matchers.equalTo("1")
        );
    }

    /**
     * MkPullComments can reply to an existing pull comment.
     *
     * @throws Exception If something goes wrong.
     * @todo #416 MkPullComments should be able to fetch all pull comments of a
     *  repo. Implement {@link MkPullComments#reply(String, int))}
     *  and don't forget to include a test here. When done, remove this puzzle
     *  and the Ignore annotation of this test method.
     */
    @Test
    @Ignore
    public void createsPullCommentReply() throws Exception {
        // To be implemented.
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

}
