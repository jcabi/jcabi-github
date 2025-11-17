/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Gist Comments.
 *
 * <p>Use this class to get access to all comments in a gist, for example:
 *
 * <pre> gist = // ... get it somewhere
 * GistComments comments = gist.comments();
 * GistComment comment = comments.post("Hi, how are you?");</pre>
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/gists/comments/">Gist Comments API</a>
 */
@Immutable
public interface GistComments {
    /**
     * The gist we're in.
     * @return Issue
     */
    Gist gist();

    /**
     * Get comment by number.
     * @param number Comment number
     * @return Comment
     * @see <a href="https://developer.github.com/v3/gists/comments/#get-a-single-comment">Get a Single Comment</a>
     */
    GistComment get(int number);

    /**
     * Iterate them all.
     * @return All comments
     * @see <a href="https://developer.github.com/v3/gists/comments/#list-comments-on-a-gist">List Comments on an Gist</a>
     */
    Iterable<GistComment> iterate();

    /**
     * Post new comment.
     * @param text Text of comment to post in Markdown format
     * @return Comment
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/gists/comments/#create-a-comment">Create a Comment</a>
     */
    GistComment post(String text)
        throws IOException;
}
