/**
 * Copyright (c) 2013-2025, jcabi.com
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
import java.util.Map;

/**
 * Github pull comments.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
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
