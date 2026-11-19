/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * GitHub commit comments of a repository.
 *
 * <p>Use this class to read and post comments on commits, including
 * in-line notes made outside of pull requests, for example:</p>
 *
 * <pre> RepoComments comments = repo.comments();
 * for (RepoComment comment : comments.iterate("6dcb09b5b5")) {
 *     System.out.println(new RepoComment.Smart(comment).body());
 * }
 * comments.post("6dcb09b5b5", "Looks good", "README.md", 1);</pre>
 *
 * @see <a href="https://docs.github.com/en/rest/commits/comments">Commit Comments API</a>
 * @since 2.0
 */
@Immutable
public interface RepoComments {

    /**
     * The repository we're in.
     *
     * @return Repo
     */
    Repo repo();

    /**
     * Get commit comment by its number.
     *
     * @param number Comment number
     * @return Commit comment
     * @see <a href="https://docs.github.com/en/rest/commits/comments#get-a-commit-comment">Get a commit comment</a>
     */
    RepoComment get(long number);

    /**
     * Iterate all commit comments of the repository.
     *
     * @return All commit comments
     * @see <a href="https://docs.github.com/en/rest/commits/comments#list-commit-comments-for-a-repository">List commit comments for a repository</a>
     */
    Iterable<RepoComment> iterate();

    /**
     * Iterate the comments of a single commit.
     *
     * @param sha SHA of the commit
     * @return Comments of the commit
     * @see <a href="https://docs.github.com/en/rest/commits/comments#list-commit-comments">List commit comments</a>
     */
    Iterable<RepoComment> iterate(String sha);

    /**
     * Post a comment on the whole commit.
     *
     * @param sha SHA of the commit
     * @param body Text of the comment in Markdown format
     * @return Commit comment just created
     * @throws IOException If there is any I/O problem
     * @see <a href="https://docs.github.com/en/rest/commits/comments#create-a-commit-comment">Create a commit comment</a>
     */
    RepoComment post(String sha, String body) throws IOException;

    /**
     * Post an in-line comment on a line of the commit diff.
     *
     * @param sha SHA of the commit
     * @param body Text of the comment in Markdown format
     * @param path Relative path of the file to comment on
     * @param position Line index in the diff to comment on
     * @return Commit comment just created
     * @throws IOException If there is any I/O problem
     * @see <a href="https://docs.github.com/en/rest/commits/comments#create-a-commit-comment">Create a commit comment</a>
     * @checkstyle ParameterNumberCheck (2 lines)
     */
    RepoComment post(String sha, String body, String path, int position)
        throws IOException;
}
