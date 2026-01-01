/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Date;

/**
 * GitHub comments.
 * <p>Use this class to get access to all comments in an issue, for example:
 * <pre> issue = // ... get it somewhere
 * Comments comments = issue.comments();
 * Comment comment = comments.post("Hi, how are you?");</pre>
 * @see <a href="https://developer.github.com/v3/issues/comments/">Issue Comments API</a>
 * @since 0.1
 */
@Immutable
public interface Comments {

    /**
     * The issue we're in.
     * @return Issue
     */
    Issue issue();

    /**
     * Get comment by number.
     * @param number Comment number
     * @return Comment
     * @see <a href="https://developer.github.com/v3/issues/comments/#get-a-single-comment">Get a Single Comment</a>
     */
    Comment get(long number);

    /**
     * Iterate them all.
     * @param since Since when? Just give {@code new Date(0)} if you want
     *  all comments.
     * @return All comments
     * @see <a href="https://developer.github.com/v3/issues/comments/#list-comments-on-an-issue">List Comments on an Issue</a>
     */
    Iterable<Comment> iterate(Date since);

    /**
     * Post new comment.
     * @param text Text of comment to post in Markdown format
     * @return Comment
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/issues/comments/#create-a-comment">Create a Comment</a>
     */
    Comment post(String text) throws IOException;

}
