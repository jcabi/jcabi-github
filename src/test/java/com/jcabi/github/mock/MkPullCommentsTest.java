/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkPullComments}.
 * @since 0.1
 */
final class MkPullCommentsTest {

    /**
     * Commit of the posted comment.
     */
    private static final String COMMIT = "commit_id";

    /**
     * Path of the posted comment.
     */
    private static final String PATH = "path";

    /**
     * Body of the posted comment.
     */
    private static final String BODY = "some text as a body";

    @Test
    void fetchesPullComment() throws IOException {
        final PullComments comments = MkPullCommentsTest.comments();
        final PullComment comment = comments.post("comment", "commit", "/", 1);
        MatcherAssert.assertThat(
            "Values are not equal",
            comments.get(comment.number()).number(),
            Matchers.equalTo(comment.number())
        );
    }

    @Test
    void iteratesPullComments() throws IOException {
        final PullComments comments = MkPullCommentsTest.crowded();
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            comments.iterate(
                comments.pull().number(),
                Collections.emptyMap()
            ),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void iteratesRepoPullComments() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            MkPullCommentsTest.crowded().iterate(Collections.emptyMap()),
            Matchers.iterableWithSize(2)
        );
    }

    @Test
    void iteratesPullRequestComments() throws IOException {
        final PullComments comments = MkPullCommentsTest.comments();
        comments.post("comment 1", "commit 1", "/commit1", 1);
        comments.post("comment 2", "commit 2", "/commit2", 2);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            comments.iterate(
                comments.pull().number(),
                Collections.emptyMap()
            ),
            Matchers.iterableWithSize(2)
        );
    }

    @Test
    void postsPullComment() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final Repo repo = MkPullCommentsTest.posted(storage);
        final String[] fields = {
            MkPullCommentsTest.COMMIT, MkPullCommentsTest.PATH,
        };
        for (final String element : fields) {
            MkPullCommentsTest.assertFieldContains(storage, repo, element);
        }
    }

    @Test
    void postsPullCommentWithPosition() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        MatcherAssert.assertThat(
            "Position of the comment is absent",
            MkPullCommentsTest.field(
                storage, MkPullCommentsTest.posted(storage), "position"
            ),
            Matchers.notNullValue()
        );
    }

    @Test
    void postsPullCommentWithBody() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        MatcherAssert.assertThat(
            "Body of the comment is wrong",
            MkPullCommentsTest.field(
                storage, MkPullCommentsTest.posted(storage), "body"
            ),
            Matchers.equalTo(MkPullCommentsTest.BODY)
        );
    }

    @Test
    void createsPullCommentReply() throws IOException {
        final PullComments comments = MkPullCommentsTest.comments();
        final String body = "Reply Comment";
        MatcherAssert.assertThat(
            "Reply has a wrong body",
            comments.reply(
                body, MkPullCommentsTest.original(comments)
            ).json().getString("body"),
            Matchers.is(body)
        );
    }

    @Test
    void pointsPullCommentReplyToOriginal() throws IOException {
        final PullComments comments = MkPullCommentsTest.comments();
        final int orig = MkPullCommentsTest.original(comments);
        MatcherAssert.assertThat(
            "Reply points to a wrong comment",
            comments.reply("Reply Comment", orig)
                .json().getString("original_position"),
            Matchers.is(Integer.toString(orig))
        );
    }

    @Test
    void iteratesPostedPullComment() throws IOException {
        final PullComments comments = MkPullCommentsTest.comments();
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            comments.iterate(
                MkPullCommentsTest.original(comments),
                Collections.emptyMap()
            ),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void removesPullComment() throws IOException {
        final PullComments comments = MkPullCommentsTest.comments();
        final int orig = MkPullCommentsTest.original(comments);
        comments.remove(orig);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            comments.iterate(
                orig, Collections.emptyMap()
            ),
            Matchers.iterableWithSize(0)
        );
    }

    /**
     * Generate pull comments for test.
     * @return The pull comments
     * @throws IOException If an IO Exception occurs
     */
    private static PullComments comments() throws IOException {
        return new MkGitHub().randomRepo().pulls()
            .create("hello", "awesome-head", "awesome-base")
            .comments();
    }

    /**
     * Generate pull comments of a repo with two commented pull requests.
     * @return The pull comments of the first pull request
     * @throws IOException If an IO Exception occurs
     */
    private static PullComments crowded() throws IOException {
        final PullComments comments = MkPullCommentsTest.comments();
        comments.pull()
            .repo()
            .pulls()
            .create("new", "head-branch", "base-branch")
            .comments()
            .post("new pull comment", "new commit", "/p", 1);
        comments.post("test 1", "tesst 1", "/test1", 1);
        return comments;
    }

    /**
     * Post a comment to be replied to.
     * @param comments Comments to post to
     * @return Number of the posted comment
     * @throws IOException If an IO Exception occurs
     */
    private static int original(final PullComments comments)
        throws IOException {
        return comments.post(
            "Orig Comment",
            "6dcb09b5b57875f334f61aebed695e2e4193db5e",
            "file1.txt",
            1
        ).number();
    }

    /**
     * Create a repo with one commented pull request in it.
     * @param storage The storage
     * @return The repo
     * @throws IOException If any I/O error occurs.
     */
    private static Repo posted(final MkStorage storage) throws IOException {
        final Repo repo = new MkGitHub(storage, "jamie").repos().create(
            new Repos.RepoCreate("incredible", false)
        );
        repo.pulls()
            .create("pullrequest1", "head", "base").comments().post(
                MkPullCommentsTest.BODY,
                MkPullCommentsTest.COMMIT,
                MkPullCommentsTest.PATH,
                1
            );
        return repo;
    }

    /**
     * Assert if fields doesn't contain value.
     * @param storage The storage
     * @param repo The repo
     * @param element The element to be tested and the value
     * @throws IOException If any I/O error occurs.
     */
    private static void assertFieldContains(
        final MkStorage storage,
        final Repo repo,
        final String element) throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            MkPullCommentsTest.field(storage, repo, element),
            Matchers.is(element)
        );
    }

    /**
     * Value of one field of the only comment in the storage.
     * @param storage The storage
     * @param repo The repo
     * @param element Name of the field
     * @return Value of the field
     * @throws IOException If any I/O error occurs.
     */
    private static String field(
        final MkStorage storage,
        final Repo repo,
        final String element) throws IOException {
        return storage.xml().xpath(
            String.format(
                "/github/repos/repo[@coords='%s/%s']/pulls/pull/comments/comment/%s/text()",
                repo.coordinates().user(),
                repo.coordinates().repo(),
                element
            )
        ).get(0);
    }
}
