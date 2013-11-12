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
import com.rexsl.test.RestTester;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
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
@EqualsAndHashCode(of = { "header", "owner", "num" })
final class GhComment implements Comment {

    /**
     * Authentication header.
     */
    private final transient String header;

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
     * @param hdr Authentication header
     * @param issue Owner of this comment
     * @param number Number of the get
     */
    GhComment(final String hdr, final Issue issue, final int number) {
        this.header = hdr;
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
    public User author() {
        return new GhUser(
            this.header,
            this.json().getJsonObject("user").getString("login")
        );
    }

    @Override
    public void remove() {
        final Coordinates coords = this.owner.repo().coordinates();
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{owner}/{repo}/issues/comments/{id}")
            .build(coords.user(), coords.repo(), this.num);
        RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .delete("removing Github comment")
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public JsonObject json() {
        final Coordinates coords = this.owner.repo().coordinates();
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{user}/{repo}/issues/comments/{id}")
            .build(coords.user(), coords.repo(), this.num);
        return RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .get("get author of Github comment")
            .assertStatus(HttpURLConnection.HTTP_OK)
            .getJson().readObject();
    }

    @Override
    public void patch(final JsonObject json) {
        final Coordinates coords = this.owner.repo().coordinates();
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{user}/{repo}/issues/comments/{number}")
            .build(coords.user(), coords.repo(), this.num);
        final StringWriter post = new StringWriter();
        Json.createWriter(post).writeObject(json);
        RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .patch("patch Github comment", post.toString())
            .assertStatus(HttpURLConnection.HTTP_OK);
    }
}
