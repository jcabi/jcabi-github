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
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github issue.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = { "coords", "num" })
@EqualsAndHashCode(of = { "header", "coords", "num" })
final class GhIssue implements Issue {

    /**
     * Authentication header.
     */
    private final transient String header;

    /**
     * Repository coordinate.
     */
    private final transient Coordinates coords;

    /**
     * Issue number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param hdr Authentication header
     * @param crd Repository coord
     * @param number Number of the issue
     */
    GhIssue(final String hdr, final Coordinates crd, final int number) {
        this.header = hdr;
        this.coords = crd;
        this.num = number;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public Comment comment(final int number) {
        return new GhComment(this.header, this.coords, number);
    }

    @Override
    public Comment post(final String text) throws IOException {
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{owner}/{repo}/issues/{number}/comments")
            .build(this.coords.user(), this.coords.repo(), this.num);
        final StringWriter post = new StringWriter();
        Json.createGenerator(post)
            .writeStartObject()
            .write("body", text)
            .writeEnd()
            .close();
        return this.comment(
            RestTester.start(uri)
                .header(HttpHeaders.AUTHORIZATION, this.header)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .post("post new comment to Github issue", post.toString())
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                // @checkstyle MultipleStringLiterals (1 line)
                .getJson().readObject().getInt("id")
        );
    }

    @Override
    public Iterator<Comment> iterator() {
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{user}/{repo}/issues/{number}/comments")
            .build(this.coords.user(), this.coords.repo(), this.num);
        final JsonArray array = RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .get("list comments in Github issue")
            .assertStatus(HttpURLConnection.HTTP_OK)
            .getJson().readArray();
        final Collection<Comment> comments =
            new ArrayList<Comment>(array.size());
        for (final JsonValue item : array) {
            comments.add(
                this.comment(JsonObject.class.cast(item).getInt("id"))
            );
        }
        return comments.iterator();
    }

}
