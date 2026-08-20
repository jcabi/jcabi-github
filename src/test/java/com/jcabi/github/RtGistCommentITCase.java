/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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

    @Test
    void postsItself() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComments comments = gist.comments();
        MatcherAssert.assertThat(
            "Posted comment is absent",
            comments.iterate(),
            Matchers.hasItem(comments.post("comment body"))
        );
        gist.github().gists().remove(gist.identifier());
    }

    @Test
    void removesItself() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComments comments = gist.comments();
        final GistComment comment = comments.post("comment body");
        comment.remove();
        MatcherAssert.assertThat(
            "Removed comment is still there",
            comments.iterate(),
            Matchers.not(Matchers.hasItem(comment))
        );
        gist.github().gists().remove(gist.identifier());
    }

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

    @Test
    void postsCommentWithBody() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComment comment = gist.comments().post("test comment");
        MatcherAssert.assertThat(
            "Posted comment has a wrong body",
            new GistComment.Smart(comment).body(),
            Matchers.startsWith("test")
        );
        comment.remove();
        gist.github().gists().remove(gist.identifier());
    }

    @Test
    void executePatchRequest() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComment comment = gist.comments().post("test comment");
        comment.patch(Json.createObjectBuilder().add("body", "hi!").build());
        MatcherAssert.assertThat(
            "Patched comment has a wrong body",
            new GistComment.Smart(comment).body(),
            Matchers.startsWith("hi")
        );
        comment.remove();
        gist.github().gists().remove(gist.identifier());
    }

    @Test
    void changeCommentBody() throws Exception {
        final Gist gist = RtGistCommentITCase.gist();
        final GistComment comment = gist.comments().post("hi there");
        new GistComment.Smart(comment).body("hello there");
        MatcherAssert.assertThat(
            "Comment body is not changed",
            new GistComment.Smart(comment).body(),
            Matchers.startsWith("hello")
        );
        comment.remove();
        gist.github().gists().remove(gist.identifier());
    }

    private static Gist gist() throws IOException {
        return GitHubIT
            .connect()
            .gists().create(
                Collections.singletonMap("file.txt", "file content"), false
            );
    }
}
