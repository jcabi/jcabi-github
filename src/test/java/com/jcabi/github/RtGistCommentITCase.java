/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link RtGistComment}.
 * @see <a href="https://developer.github.com/v3/gists/comments/">Gist Comments API</a>
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.GIST)
final class RtGistCommentITCase {

    /**
     * RtGistComment can remove itself.
     * @throws Exception if some problem inside
     */
    @Test
    void removeItself() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final String body = "comment body";
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post(body);
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            comments.iterate(),
            Matchers.hasItem(comment)
        );
        comment.remove();
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
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
    void fetchAsJson() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post("comment");
        MatcherAssert.assertThat(
            "Values are not equal",
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
    void executePatchRequest() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post("test comment");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new GistComment.Smart(comment).body(),
            Matchers.startsWith("test")
        );
        comment.patch(Json.createObjectBuilder().add("body", "hi!").build());
        MatcherAssert.assertThat(
            "String does not start with expected value",
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
    void changeCommentBody() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post("hi there");
        MatcherAssert.assertThat(
            "String does not end with expected value",
            new GistComment.Smart(comment).body(),
            Matchers.endsWith("there")
        );
        new GistComment.Smart(comment).body("hello there");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new GistComment.Smart(comment).body(),
            Matchers.startsWith("hello")
        );
        comment.remove();
        gist.github().gists().remove(gist.identifier());
    }

    /**
     * Return gist to test.
     * @return Gist
     */
    private static Gist gist() throws IOException {
        return GitHubIT
            .connect()
            .gists()
            .create(
                Collections.singletonMap("file.txt", "file content"), false
            );
    }
}
