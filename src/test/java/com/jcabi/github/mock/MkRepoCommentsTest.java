/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.RepoComment;
import com.jcabi.github.RepoComments;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkRepoComments}.
 *
 * @since 2.0
 */
final class MkRepoCommentsTest {

    /**
     * SHA of the first commit.
     */
    private static final String FIRST = "a0b1c2d3e4";

    /**
     * SHA of the second commit.
     */
    private static final String SECOND = "f5e6d7c8b9";

    @Test
    void fetchesPostedComment() throws IOException {
        final RepoComments comments = MkRepoCommentsTest.comments();
        MatcherAssert.assertThat(
            "Posted comment is not found",
            new RepoComment.Smart(
                comments.get(
                    comments.post(MkRepoCommentsTest.FIRST, "Well done").number()
                )
            ).body(),
            Matchers.equalTo("Well done")
        );
    }

    @Test
    void postsCommentOnCommit() throws IOException {
        MatcherAssert.assertThat(
            "Comment is posted on a wrong commit",
            new RepoComment.Smart(
                MkRepoCommentsTest.comments()
                    .post(MkRepoCommentsTest.FIRST, "Hi")
            ).commitId(),
            Matchers.equalTo(MkRepoCommentsTest.FIRST)
        );
    }

    @Test
    void postsCommentOnWholeCommit() throws IOException {
        MatcherAssert.assertThat(
            "Comment on the whole commit has a path",
            new RepoComment.Smart(
                MkRepoCommentsTest.comments()
                    .post(MkRepoCommentsTest.FIRST, "General")
            ).hasPath(),
            Matchers.is(false)
        );
    }

    @Test
    void postsInlineCommentWithPath() throws IOException {
        MatcherAssert.assertThat(
            "In-line comment has a wrong path",
            new RepoComment.Smart(
                MkRepoCommentsTest.comments().post(
                    MkRepoCommentsTest.FIRST, "Typo", "src/Main.java", 3
                )
            ).path(),
            Matchers.equalTo("src/Main.java")
        );
    }

    @Test
    void postsInlineCommentWithPosition() throws IOException {
        MatcherAssert.assertThat(
            "In-line comment has a wrong position",
            new RepoComment.Smart(
                MkRepoCommentsTest.comments().post(
                    MkRepoCommentsTest.FIRST, "Typo", "src/Main.java", 3
                )
            ).position(),
            Matchers.equalTo(3)
        );
    }

    @Test
    void exposesNumberInJson() throws IOException {
        final RepoComment comment = MkRepoCommentsTest.comments()
            .post(MkRepoCommentsTest.FIRST, "Number");
        MatcherAssert.assertThat(
            "Comment JSON has a wrong id",
            comment.json().getJsonNumber("id").longValue(),
            Matchers.equalTo(comment.number())
        );
    }

    @Test
    void iteratesAllComments() throws IOException {
        final RepoComments comments = MkRepoCommentsTest.comments();
        comments.post(MkRepoCommentsTest.FIRST, "One");
        comments.post(MkRepoCommentsTest.SECOND, "Two");
        MatcherAssert.assertThat(
            "Not all comments are iterated",
            MkRepoCommentsTest.bodies(comments.iterate()),
            Matchers.contains("One", "Two")
        );
    }

    @Test
    void iteratesCommentsOfCommit() throws IOException {
        final RepoComments comments = MkRepoCommentsTest.comments();
        comments.post(MkRepoCommentsTest.FIRST, "Alpha");
        comments.post(MkRepoCommentsTest.SECOND, "Beta");
        comments.post(MkRepoCommentsTest.FIRST, "Gamma", "pom.xml", 2);
        MatcherAssert.assertThat(
            "Comments of the commit are iterated wrongly",
            MkRepoCommentsTest.bodies(
                comments.iterate(MkRepoCommentsTest.FIRST)
            ),
            Matchers.contains("Alpha", "Gamma")
        );
    }

    @Test
    void removesComment() throws IOException {
        final RepoComments comments = MkRepoCommentsTest.comments();
        comments.post(MkRepoCommentsTest.FIRST, "Keep");
        comments.post(MkRepoCommentsTest.FIRST, "Drop").remove();
        MatcherAssert.assertThat(
            "Comment is not removed",
            MkRepoCommentsTest.bodies(comments.iterate()),
            Matchers.contains("Keep")
        );
    }

    @Test
    void assignsUniqueNumbersAfterRemoval() throws IOException {
        final RepoComments comments = MkRepoCommentsTest.comments();
        final RepoComment first = comments.post(MkRepoCommentsTest.FIRST, "1");
        final RepoComment second = comments.post(MkRepoCommentsTest.FIRST, "2");
        first.remove();
        MatcherAssert.assertThat(
            "Comment number is reused",
            comments.post(MkRepoCommentsTest.FIRST, "3").number(),
            Matchers.greaterThan(second.number())
        );
    }

    @Test
    void updatesBody() throws IOException {
        final RepoComments comments = MkRepoCommentsTest.comments();
        final RepoComment.Smart comment = new RepoComment.Smart(
            comments.post(MkRepoCommentsTest.FIRST, "Before")
        );
        comment.body("After");
        MatcherAssert.assertThat(
            "Body is not updated",
            new RepoComment.Smart(comments.get(comment.number())).body(),
            Matchers.equalTo("After")
        );
    }

    @Test
    void keepsCommentsOfRepositoriesApart() throws IOException {
        final MkGitHub github = new MkGitHub("jeff");
        github.randomRepo().comments().post(MkRepoCommentsTest.FIRST, "Mine");
        MatcherAssert.assertThat(
            "Comments of another repository are iterated",
            github.randomRepo().comments().iterate(),
            Matchers.emptyIterable()
        );
    }

    private static RepoComments comments() throws IOException {
        return new MkGitHub().randomRepo().comments();
    }

    private static List<String> bodies(final Iterable<RepoComment> comments)
        throws IOException {
        final List<String> bodies = new ArrayList<>(0);
        for (final RepoComment comment : comments) {
            bodies.add(new RepoComment.Smart(comment).body());
        }
        return bodies;
    }
}
