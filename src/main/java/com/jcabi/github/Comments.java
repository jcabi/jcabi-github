/**
 * Copyright (c) 2013-2017, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Github comments.
 *
 * <p>Use this class to get access to all comments in an issue, for example:
 *
 * <pre> issue = // ... get it somewhere
 * Comments comments = issue.comments();
 * Comment comment = comments.post("Hi, how are you?");</pre>
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/comments/">Issue Comments API</a>
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
     * @see <a href="http://developer.github.com/v3/issues/comments/#get-a-single-comment">Get a Single Comment</a>
     */
    Comment get(int number);

    /**
     * Iterate them all.
     * @return All comments
     * @see <a href="http://developer.github.com/v3/issues/comments/#list-comments-on-an-issue">List Comments on an Issue</a>
     */
    Iterable<Comment> iterate();

    /**
     * Post new comment.
     * @param text Text of comment to post in Markdown format
     * @return Comment
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/comments/#create-a-comment">Create a Comment</a>
     */
    Comment post(String text) throws IOException;

}
