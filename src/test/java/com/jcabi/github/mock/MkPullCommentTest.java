/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.PullComment;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkPullComment}.
 * @since 0.1
 */
final class MkPullCommentTest {
    /**
     * MkPullComment can be represented as JSON.
     * @throws Exception If a problem occurs.
     */
    @Test
    void retrieveAsJson() throws Exception {
        final PullComment comment = MkPullCommentTest.comment();
        MatcherAssert.assertThat(
            "String does not start with expected value",
            comment.json().getString("url"),
            Matchers.startsWith("http://")
        );
    }

    /**
     * MkPullComment can accept a PATCH request.
     * @throws Exception If a problem occurs.
     */
    @Test
    void executePatchRequest() throws Exception {
        final String path = "/path/to/file.txt";
        final PullComment comment = MkPullCommentTest.comment();
        comment.patch(Json.createObjectBuilder().add("path", path).build());
        MatcherAssert.assertThat(
            "String does not contain expected value",
            comment.json().toString(),
            Matchers.containsString(path)
        );
    }

    /**
     * Create and return pull comment to test.
     * @return PullComment
     */
    private static PullComment comment() throws IOException {
        return new MkGitHub()
            .randomRepo()
            .pulls()
            .create("hello", "head", "base")
            .comments()
            .post("comment", "commit", "/", 1);
    }
}
