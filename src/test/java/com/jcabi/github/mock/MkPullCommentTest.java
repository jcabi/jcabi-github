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
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPullComment}.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 */
public final class MkPullCommentTest {
    /**
     * MkPullComment can be represented as JSON.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void retrieveAsJson() throws Exception {
        final PullComment comment = MkPullCommentTest.comment();
        MatcherAssert.assertThat(
            comment.json().getString("url"),
            Matchers.startsWith("http://")
        );
    }

    /**
     * MkPullComment can accept a PATCH request.
     *
     * @throws Exception If a problem occurs.
     */
    @Test
    public void executePatchRequest() throws Exception {
        final String path = "/path/to/file.txt";
        final PullComment comment = MkPullCommentTest.comment();
        comment.patch(Json.createObjectBuilder().add("path", path).build());
        MatcherAssert.assertThat(
            comment.json().toString(),
            Matchers.containsString(path)
        );
    }

    /**
     * Create and return pull comment to test.
     * @return PullComment
     * @throws Exception if any error inside
     */
    private static PullComment comment() throws Exception {
        return new MkGithub()
            .repos()
            .create(Json.createObjectBuilder().add("name", "test").build())
            .pulls()
            .create("hello", "", "")
            .comments()
            .post("comment", "commit", "/", 1);
    }
}
