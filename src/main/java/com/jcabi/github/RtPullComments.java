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
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import lombok.EqualsAndHashCode;

/**
 * Github pull comment.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner" })
public final class RtPullComments implements PullComments {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Owner of comments.
     */
    private final transient Pull owner;

    /**
     * Public ctor.
     * @param req Request
     * @param pull Pull
     */
    RtPullComments(final Request req, final Pull pull) {
        this.entry = req;
        final Coordinates coords = pull.repo().coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/pulls")
            .path("/comments")
            .back();
        this.owner = pull;
    }

    @Override
    public Pull pull() {
        return this.owner;
    }

    @Override
    public PullComment get(final int number) {
        return new RtPullComment(this.entry, this.owner, number);
    }

    @Override
    public Iterable<PullComment> iterate(final Map<String, String> params) {
        //@checkstyle MultipleStringLiteralsCheck (1 line)
        throw new UnsupportedOperationException("Iterate not yet implemented.");
    }

    @Override
    public Iterable<PullComment> iterate(final int number,
        final Map<String, String> params) {
        throw new UnsupportedOperationException("Iterate not yet implemented.");
    }

    // @checkstyle ParameterNumberCheck (3 lines)
    @Override
    public PullComment post(final String body, final String commit,
        final String path, final int position) throws IOException {
        throw new UnsupportedOperationException("Post not yet implemented.");
    }

    @Override
    public PullComment reply(final String text,
        final int comment) throws IOException {
        throw new UnsupportedOperationException("Reply not yet implemented.");
    }

    @Override
    public void remove(final int number) throws IOException {
        this.request.uri().path(String.valueOf(number)).back()
            .method(Request.DELETE)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
}
