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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Integration test for {@link RtGistComments}.
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/gists/comments/">Gist Comments API</a>
 * @since 0.8
 */
public final class RtGistCommentsITCase {
    /**
     * RtGistComments can create a comment.
     * @throws Exception if some problem inside
     */
    @Test
    public void createComment() throws Exception {
        final Gist gist = RtGistCommentsITCase.gist();
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post("gist comment");
        MatcherAssert.assertThat(
            new GistComment.Smart(comment).body(),
            Matchers.startsWith("gist")
        );
        comment.remove();
        gist.github().gists().remove(gist.identifier());
    }

    /**
     * RtGistComments can get a comment.
     * @throws Exception if some problem inside
     */
    @Test
    public void getComment() throws Exception {
        final Gist gist = RtGistCommentsITCase.gist();
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post("test comment");
        MatcherAssert.assertThat(
            comments.get(comment.number()),
            Matchers.equalTo(comment)
        );
        comment.remove();
        gist.github().gists().remove(gist.identifier());
    }

    /**
     * RtGistComments can iterate all gist comments.
     * @throws Exception if some problem inside
     */
    @Test
    public void iterateComments() throws Exception {
        final Gist gist = RtGistCommentsITCase.gist();
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post("comment");
        MatcherAssert.assertThat(
            comments.iterate(),
            Matchers.hasItem(comment)
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
                Collections.singletonMap("file.txt",  "file content"), false
            );
    }
}
