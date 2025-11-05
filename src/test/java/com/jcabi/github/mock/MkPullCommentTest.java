/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
            .randomRepo()
            .pulls()
            .create("hello", "head", "base")
            .comments()
            .post("comment", "commit", "/", 1);
    }
}
