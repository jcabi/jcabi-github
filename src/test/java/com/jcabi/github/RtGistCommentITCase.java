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

import java.util.Collections;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration test for {@link RtGistComment}. This test requires OAuth scope
 * "gist".
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/gists/comments/">Gist Comments API</a>
 * @since 0.8
 */
public final class RtGistCommentITCase {

    /**
     * RtGistComment can remove itself.
     * @throws Exception if some problem inside
     */
    @Test
    public void removeItself() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final String body = "comment body";
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post(body);
        MatcherAssert.assertThat(
            comments.iterate(),
            Matchers.hasItem(comment)
        );
        comment.remove();
        MatcherAssert.assertThat(
            comments.iterate(),
            Matchers.not(Matchers.hasItem(comment))
        );
        gist.github().gists().remove(gist.identifier());
    }

    /**
     * RtGistComment can fetch as JSON object.
     * @throws Exception if some problem inside
     */
    @Test
    public void fetchAsJSON() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post("comment");
        MatcherAssert.assertThat(
            comment.json().getInt("id"),
            Matchers.equalTo(comment.number())
        );
        comment.remove();
        gist.github().gists().remove(gist.identifier());
    }

    /**
     * RtGistComment can execute patch request.
     * @throws Exception if some problem inside
     */
    @Test
    public void executePatchRequest() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post("test comment");
        MatcherAssert.assertThat(
            new GistComment.Smart(comment).body(),
            Matchers.startsWith("test")
        );
        comment.patch(Json.createObjectBuilder().add("body", "hi!").build());
        MatcherAssert.assertThat(
            new GistComment.Smart(comment).body(),
            Matchers.startsWith("hi")
        );
        comment.remove();
        gist.github().gists().remove(gist.identifier());
    }

    /**
     * RtGistComment can change comment body.
     * @throws Exception if some problem inside
     */
    @Test
    public void changeCommentBody() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post("hi there");
        MatcherAssert.assertThat(
            new GistComment.Smart(comment).body(),
            Matchers.endsWith("there")
        );
        new GistComment.Smart(comment).body("hello there");
        MatcherAssert.assertThat(
            new GistComment.Smart(comment).body(),
            Matchers.startsWith("hello")
        );
        comment.remove();
        gist.github().gists().remove(gist.identifier());
    }

    /**
     * Return gist to test.
     * @return Gist
     * @throws Exception If some problem inside
     */
    private static Gist gist() throws Exception {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key)
            .gists()
            .create(
                Collections.singletonMap("file.txt", "file content"), false
            );
    }
}
