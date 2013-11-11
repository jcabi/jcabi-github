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
 * Github get.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString(of = { "owner", "num" })
@EqualsAndHashCode(of = { "header", "owner", "num" })
@SuppressWarnings("PMD.TooManyMethods")
final class GhIssue implements Issue {

    /**
     * Authentication header.
     */
    private final transient String header;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Issue number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param hdr Authentication header
     * @param repo Repository
     * @param number Number of the get
     */
    GhIssue(final String hdr, final Repo repo, final int number) {
        this.header = hdr;
        this.owner = repo;
        this.num = number;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public int number() {
        return this.num;
    }

    @Override
    public String state() {
        return this.json().getString("state");
    }

    @Override
    public String title() {
        return this.json().getString("title");
    }

    @Override
    public String body() {
        return this.json().getString("body");
    }

    @Override
    public void state(final String text) {
        final Coordinates coords = this.owner.coordinates();
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{login}/{repo}/issues/{number}")
            .build(coords.user(), coords.repo(), this.num);
        final StringWriter post = new StringWriter();
        Json.createGenerator(post)
            .writeStartObject()
            .write("state", text)
            .writeEnd()
            .close();
        RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .patch("change state of Github issue", post.toString())
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public void title(final String text) {
        final Coordinates coords = this.owner.coordinates();
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{owner}/{repo}/issues/{id}")
            .build(coords.user(), coords.repo(), this.num);
        final StringWriter post = new StringWriter();
        Json.createGenerator(post)
            .writeStartObject()
            .write("title", text)
            .writeEnd()
            .close();
        RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .patch("change title of Github issue", post.toString())
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public void body(final String text) {
        final Coordinates coords = this.owner.coordinates();
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{user}/{repo}/issues/{num}")
            .build(coords.user(), coords.repo(), this.num);
        final StringWriter post = new StringWriter();
        Json.createGenerator(post)
            .writeStartObject()
            .write("body", text)
            .writeEnd()
            .close();
        RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .patch("change body of Github issue", post.toString())
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public Comments comments() {
        return new GhComments(this.header, this);
    }

    @Override
    public Labels labels() {
        return new GhIssueLabels(this.header, this);
    }

    @Override
    public JsonObject json() {
        final Coordinates coords = this.owner.coordinates();
        final URI uri = Github.ENTRY.clone()
            .path("/repos/{user}/{repo}/issues/{number}")
            .build(coords.user(), coords.repo(), this.num);
        return RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .get("get details of Github issue")
            .assertStatus(HttpURLConnection.HTTP_OK)
            .getJson().readObject();
    }

    @Override
    public int compareTo(final Issue issue) {
        return new Integer(this.number()).compareTo(issue.number());
    }

}
