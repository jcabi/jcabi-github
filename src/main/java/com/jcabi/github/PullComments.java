/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;

/**
 * Github pull comments.
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/pulls/comments/">Review Comments API</a>
 */
@Immutable
public interface PullComments {

    /**
     * Pull we're in.
     *
     * @return Pull
     */
    Pull pull();

    /**
     * Get specific pull comment by number.
     *
     * @param number Pull comment number
     * @return Pull comment
     * @see <a href="https://developer.github.com/v3/pulls/comments/#get-a-single-comment">Get a single comment</a>
     */
    PullComment get(int number);

    /**
     * Iterate all pull comments for this repo.
     *
     * @param params Iterating parameters, as specified by API
     * @return Iterable of pull comments
     * @see <a href="https://developer.github.com/v3/pulls/comments/#list-comments-in-a-repository">List comments in a repository</a>
     */
    Iterable<PullComment> iterate(
        Map<String, String> params);

    /**
     * Iterate all pull comments for a pull request.
     *
     * @param number Pull comment number
     * @param params Iterating parameters, as specified by API
     * @return Iterable of pull comments
     * @see <a href="https://developer.github.com/v3/pulls/comments/#list-comments-on-a-pull-request">List comments on a pull request</a>
     */
    Iterable<PullComment> iterate(int number,
        Map<String, String> params);

    /**
     * Post a new pull comment.
     *
     * @param body Body of it
     * @param commit Commit ID (SHA) of it
     * @param path Path of the file to comment on
     * @param position Line index in the diff to comment on
     * @return PullComment just created
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/pulls/comments/#create-a-comment">Create a comment</a>
     * @checkstyle ParameterNumberCheck (7 lines)
     */
    PullComment post(
        String body,
        String commit,
        String path,
        int position)
        throws IOException;

    /**
     * Create a new comment as a reply to an existing pull comment.
     *
     * @param body Body of it
     * @param comment Commit ID (SHA) of it
     * @return PullComment just created
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/pulls/comments/#create-a-comment">Create a comment</a>
     */
    PullComment reply(
        String body,
        int comment)
        throws IOException;

    /**
     * Removes a pull comment by ID.
     *
     * @param number The ID of the pull comment to delete.
     * @throws IOException If there is any I/O problem.
     */
    void remove(int number) throws IOException;

}
