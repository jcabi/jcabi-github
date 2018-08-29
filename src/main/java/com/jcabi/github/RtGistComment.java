/**
 * Copyright (c) 2013-2018, jcabi.com
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
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Gist comment.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
final class RtGistComment implements GistComment {
    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Gist we're in.
     */
    private final transient Gist owner;

    /**
     * Comment number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param req RESTful request
     * @param gist Gist of this comment
     * @param number Number of the get
     */
    RtGistComment(final Request req, final Gist gist, final int number) {
        this.request = req.uri()
            .path("/gists")
            .path(new Gist.Smart(gist).identifier())
            .path("/comments")
            .path(Integer.toString(number))
            .back();
        this.owner = gist;
        this.num = number;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Gist gist() {
        return this.owner;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public void remove() throws IOException {
        this.request.method(Request.DELETE).fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public int compareTo(
        final GistComment comment
    ) {
        return this.number() - comment.number();
    }

    @Override
    public void patch(
        final JsonObject json
    ) throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
