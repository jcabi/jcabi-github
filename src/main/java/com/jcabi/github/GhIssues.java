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
 * Github issues.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = "coords")
@EqualsAndHashCode(of = { "header", "coords" })
final class GhIssues implements Issues {

    /**
     * Authentication header.
     */
    private final transient String header;

    /**
     * Repository coordinate.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param hdr Authentication header
     * @param crd Repository coords
     */
    GhIssues(final String hdr, final Coordinates crd) {
        this.header = hdr;
        this.coords = crd;
    }

    @Override
    public Issue get(final int number) {
        return new GhIssue(this.header, this.coords, number);
    }

    @Override
    public Issue create(final String title, final String body)
        throws IOException {
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{owner}/{repo}/issues")
            .build(this.coords.user(), this.coords.repo());
        final StringWriter post = new StringWriter();
        Json.createGenerator(post)
            .writeStartObject()
            .write("title", title)
            .write("body", body)
            .writeEnd()
            .close();
        return this.get(
            RestTester.start(uri)
                .header(HttpHeaders.AUTHORIZATION, this.header)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .post("create new Github get", post.toString())
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .getJson().readObject().getInt("number")
        );
    }

    @Override
    public Iterator<Issue> iterator() {
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{user}/{repo}/issues")
            .build(this.coords.user(), this.coords.repo());
        final JsonArray array = RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .get("list issues in Github repository")
            .assertStatus(HttpURLConnection.HTTP_OK)
            .getJson().readArray();
        final Collection<Issue> issues = new ArrayList<Issue>(array.size());
        for (final JsonValue item : array) {
            issues.add(this.get(JsonObject.class.cast(item).getInt("id")));
        }
        return issues.iterator();
    }

}
