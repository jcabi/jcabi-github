/**
 * Copyright (c) 2013-2014, JCabi.com
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
import javax.validation.constraints.NotNull;

/**
 * Gist Comments.
 *
 * <p>Use this class to get access to all comments in a gist, for example:
 *
 * <pre> gist = // ... get it somewhere
 * GistComments comments = gist.comments();
 * GistComment comment = comments.post("Hi, how are you?");</pre>
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/gists/comments/">Gist Comments API</a>
 */
@Immutable
public interface GistComments {
    /**
     * The gist we're in.
     * @return Issue
     */
    @NotNull(message = "gist is never NULL")
    Gist gist();

    /**
     * Get comment by number.
     * @param number Comment number
     * @return Comment
     * @see <a href="http://developer.github.com/v3/gists/comments/#get-a-single-comment">Get a Single Comment</a>
     */
    @NotNull(message = "comment is never NULL")
    GistComment get(int number);

    /**
     * Iterate them all.
     * @return All comments
     * @see <a href="http://developer.github.com/v3/gists/comments/#list-comments-on-a-gist">List Comments on an Gist</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<GistComment> iterate();

    /**
     * Post new comment.
     * @param text Text of comment to post in Markdown format
     * @return Comment
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/gists/comments/#create-a-comment">Create a Comment</a>
     */
    @NotNull(message = "comment is never NULL")
    GistComment post(@NotNull(message = "text can't be NULL") String text)
        throws IOException;
}
