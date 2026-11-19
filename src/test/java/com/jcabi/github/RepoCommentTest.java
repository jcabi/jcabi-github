/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.time.Instant;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RepoComment}.
 *
 * @since 2.0
 */
final class RepoCommentTest {

    @Test
    void fetchesBody() throws IOException {
        MatcherAssert.assertThat(
            "Body is wrong",
            new RepoComment.Smart(
                RepoCommentTest.comment(
                    Json.createObjectBuilder().add("body", "Looks good").build()
                )
            ).body(),
            Matchers.equalTo("Looks good")
        );
    }

    @Test
    void updatesBody() throws IOException {
        final RepoComment comment = Mockito.mock(RepoComment.class);
        new RepoComment.Smart(comment).body("Fixed");
        Mockito.verify(comment).patch(
            Json.createObjectBuilder().add("body", "Fixed").build()
        );
    }

    @Test
    void fetchesCommit() throws IOException {
        MatcherAssert.assertThat(
            "Commit SHA is wrong",
            new RepoComment.Smart(
                RepoCommentTest.comment(
                    Json.createObjectBuilder()
                        .add("commit_id", "6dcb09b5b5")
                        .build()
                )
            ).commitId(),
            Matchers.equalTo("6dcb09b5b5")
        );
    }

    @Test
    void fetchesPath() throws IOException {
        MatcherAssert.assertThat(
            "Path is wrong",
            new RepoComment.Smart(
                RepoCommentTest.comment(
                    Json.createObjectBuilder().add("path", "README.md").build()
                )
            ).path(),
            Matchers.equalTo("README.md")
        );
    }

    @Test
    void detectsInlineComment() throws IOException {
        MatcherAssert.assertThat(
            "In-line comment is not recognized",
            new RepoComment.Smart(
                RepoCommentTest.comment(
                    Json.createObjectBuilder()
                        .add("path", "README.md")
                        .add("position", 1)
                        .build()
                )
            ).hasPath(),
            Matchers.is(true)
        );
    }

    @Test
    void detectsCommentOnWholeCommit() throws IOException {
        MatcherAssert.assertThat(
            "Comment on the whole commit is taken for an in-line one",
            new RepoComment.Smart(
                RepoCommentTest.comment(
                    Json.createObjectBuilder()
                        .addNull("path")
                        .addNull("position")
                        .build()
                )
            ).hasPath(),
            Matchers.is(false)
        );
    }

    @Test
    void fetchesPosition() throws IOException {
        MatcherAssert.assertThat(
            "Position is wrong",
            new RepoComment.Smart(
                RepoCommentTest.comment(
                    Json.createObjectBuilder().add("position", 7).build()
                )
            ).position(),
            Matchers.equalTo(7)
        );
    }

    @Test
    void detectsMissingPosition() throws IOException {
        MatcherAssert.assertThat(
            "Missing position is not recognized",
            new RepoComment.Smart(
                RepoCommentTest.comment(
                    Json.createObjectBuilder().addNull("position").build()
                )
            ).hasPosition(),
            Matchers.is(false)
        );
    }

    @Test
    void fetchesUrl() throws IOException {
        MatcherAssert.assertThat(
            "URL is wrong",
            new RepoComment.Smart(
                RepoCommentTest.comment(
                    Json.createObjectBuilder()
                        .add("url", "https://api.github.com/repos/o/r/comments/1")
                        .build()
                )
            ).url().toString(),
            Matchers.equalTo("https://api.github.com/repos/o/r/comments/1")
        );
    }

    @Test
    void fetchesCreationTime() throws IOException {
        MatcherAssert.assertThat(
            "Creation time is wrong",
            new RepoComment.Smart(
                RepoCommentTest.comment(
                    Json.createObjectBuilder()
                        .add("created_at", "2014-04-14T02:15:15Z")
                        .build()
                )
            ).createdAt(),
            Matchers.equalTo(Instant.parse("2014-04-14T02:15:15Z"))
        );
    }

    @Test
    void fetchesUpdateTime() throws IOException {
        MatcherAssert.assertThat(
            "Update time is wrong",
            new RepoComment.Smart(
                RepoCommentTest.comment(
                    Json.createObjectBuilder()
                        .add("updated_at", "2015-05-15T03:16:16Z")
                        .build()
                )
            ).updatedAt(),
            Matchers.equalTo(Instant.parse("2015-05-15T03:16:16Z"))
        );
    }

    @Test
    void fetchesAuthor() throws IOException {
        MatcherAssert.assertThat(
            "Author is wrong",
            new RepoComment.Smart(
                new MkGitHub("jeff").randomRepo().comments()
                    .post("6dcb09b5b5", "Thanks")
            ).author().login(),
            Matchers.equalTo("jeff")
        );
    }

    private static RepoComment comment(final JsonObject json)
        throws IOException {
        final RepoComment comment = Mockito.mock(RepoComment.class);
        Mockito.doReturn(json).when(comment).json();
        return comment;
    }
}
