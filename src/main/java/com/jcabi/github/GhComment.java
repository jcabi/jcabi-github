/**
 * Copyright (c) 2012-2013, JCabi.com
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
import com.rexsl.test.JsonResponse;
import com.rexsl.test.Request;
import com.rexsl.test.RestResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github comment.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = { "owner", "num" })
@EqualsAndHashCode(of = { "request", "owner", "num" })
final class GhComment implements Comment {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Issue we're in.
     */
    private final transient Issue owner;

    /**
     * Comment number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param req RESTful request
     * @param issue Owner of this comment
     * @param number Number of the get
     */
    GhComment(final Request req, final Issue issue, final int number) {
        final Coordinates coords = issue.repo().coordinates();
        this.entry = req;
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/issues")
            .path("/comments")
            .path(Integer.toString(number))
            .back();
        this.owner = issue;
        this.num = number;
    }

    @Override
    public Issue issue() {
        return this.owner;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public User author() throws IOException {
        return new GhUser(
            this.entry,
            this.json().getJsonObject("user").getString("login")
        );
    }

    @Override
    public void remove() throws IOException {
        this.request.method(Request.DELETE).fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public JsonObject json() throws IOException {
        return this.request.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readObject();
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        final StringWriter post = new StringWriter();
        Json.createWriter(post).writeObject(json);
        this.request.method(Request.PATCH)
            .body().set(post.toString()).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public int compareTo(final Comment comment) {
        return new Integer(this.number()).compareTo(comment.number());
    }
}
