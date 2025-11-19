/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.PullComment;
import com.jcabi.github.PullComments;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPullComments}.
 * @since 0.1
 */
public final class MkPullCommentsTest {

    /**
     * MkPullComments can fetch a single comment.
     */
    @Test
    public void fetchesPullComment() throws IOException {
        final PullComments comments = this.comments();
        final PullComment comment = comments.post("comment", "commit", "/", 1);
        MatcherAssert.assertThat(
            "Values are not equal",
            comments.get(comment.number()).number(),
            Matchers.equalTo(comment.number())
        );
    }

    /**
     * MkPullComments can fetch all pull comments for a repo.
     */
    @Test
    public void iteratesRepoPullComments() throws IOException {
        final PullComments comments = this.comments();
        comments.pull()
            .repo()
            .pulls()
            .create("new", "head-branch", "base-branch")
            .comments()
            .post("new pull comment", "new commit", "/p", 1);
        comments.post("test 1", "tesst 1", "/test1", 1);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            comments.iterate(
                comments.pull().number(),
                Collections.emptyMap()
            ),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            comments.iterate(
                Collections.emptyMap()
            ),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * MkPullComments can fetch pull comments for a pull request.
     */
    @Test
    public void iteratesPullRequestComments() throws IOException {
        final PullComments comments = this.comments();
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

    /**
     * MkPullComments can create a pull comment.
     */
    @Test
    public void postsPullComment() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final String commit = "commit_id";
        final String path = "path";
        final String bodytext = "some text as a body";
        final String login = "jamie";
        final String reponame = "incredible";
        final Repo repo = new MkGitHub(storage, login).repos().create(
            new Repos.RepoCreate(reponame, false)
        );
        repo.pulls()
            .create("pullrequest1", "head", "base").comments()
            .post(bodytext, commit, path, 1);
        final String[] fields = {commit, path};
        for (final String element : fields) {
            MkPullCommentsTest.assertFieldContains(storage, repo, element);
        }
        final List<String> position = storage.xml().xpath(
            String.format(
                // @checkstyle LineLength (1 line)
                "/github/repos/repo[@coords='%s/%s']/pulls/pull/comments/comment/position/text()",
                repo.coordinates().user(),
                repo.coordinates().repo()
            )
        );
        MatcherAssert.assertThat(
            "Value is null",
            position.get(0),
            Matchers.notNullValue()
        );
        final List<String> body = storage.xml().xpath(
            String.format(
                // @checkstyle LineLength (1 line)
                "/github/repos/repo[@coords='%s/%s']/pulls/pull/comments/comment/body/text()",
                repo.coordinates().user(),
                repo.coordinates().repo()
            )
        );
        MatcherAssert.assertThat(
            "Values are not equal", body.get(0), Matchers.equalTo(bodytext));
    }

    /**
     * MkPullComments can reply to an existing pull comment.
     */
    @Test
    public void createsPullCommentReply() throws IOException {
        final PullComments comments = this.comments();
        final int orig = comments.post(
            "Orig Comment",
            "6dcb09b5b57875f334f61aebed695e2e4193db5e",
            "file1.txt",
            1
        ).number();
        final String body = "Reply Comment";
        final JsonObject reply = comments.reply(body, orig).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            reply.getString("body"),
            Matchers.is(body)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            reply.getString("original_position"),
            Matchers.is(Integer.toString(orig))
        );
    }

    /**
     * MkPullComments can remove a pull comment.
     */
    @Test
    public void removesPullComment() throws IOException {
        final PullComments comments = this.comments();
        final int orig = comments.post(
            "Origg Comment",
            "6dcb09b5b57875f334f61aebed695e2e4193db5d",
            "file2.txt",
            1
        ).number();
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            comments.iterate(
                orig, Collections.emptyMap()
            ),
            Matchers.iterableWithSize(1)
        );
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
    private PullComments comments() throws IOException {
        // @checkstyle MultipleStringLiteralsCheck (1 line)
        return new MkGitHub().randomRepo().pulls()
            .create("hello", "awesome-head", "awesome-base")
            .comments();
    }

    /**
     * Assert if fields doesn't contain value.
     * @param storage The storage
     * @param repo The repo
     * @param element The element to be tested and the value.
     * @throws IOException If any I/O error occurs.
     */
    private static void assertFieldContains(
        final MkStorage storage,
        final Repo repo,
        final String element) throws IOException {
        final String xpath = String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s/%s']/pulls/pull/comments/comment/%s/text()",
            repo.coordinates().user(),
            repo.coordinates().repo(),
            element
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            storage.xml().xpath(xpath).get(0),
            Matchers.is(element)
        );
    }

}
