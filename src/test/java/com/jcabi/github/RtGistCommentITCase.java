/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import java.util.Collections;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration test for {@link RtGistComment}.
 * @see <a href="https://developer.github.com/v3/gists/comments/">Gist Comments API</a>
 * @since 0.8
 */
@OAuthScope(Scope.GIST)
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
        return new GithubIT()
            .connect()
            .gists()
            .create(
                Collections.singletonMap("file.txt", "file content"), false
            );
    }
}
