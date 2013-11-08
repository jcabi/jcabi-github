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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github gist.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "ghub", "header", "label" })
final class GhGist implements Gist {

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * Authentication header.
     */
    private final transient String header;

    /**
     * Gist name.
     */
    private final transient String label;

    /**
     * Public ctor.
     * @param github Github
     * @param hdr Authentication header
     * @param name Name of it
     */
    GhGist(final Github github, final String hdr, final String name) {
        this.ghub = github;
        this.header = hdr;
        this.label = name;
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public String read(final String file) {
        final URI uri = Github.ENTRY.clone()
            .path("/gists/{id}")
            .build(this.label);
        final String url = RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .get("read gist of Github")
            .assertStatus(HttpURLConnection.HTTP_OK)
            .getJson().readObject().getJsonObject("files")
            .getJsonObject(file).getString("raw_url");
        return RestTester.start(URI.create(url))
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .get("read gist content of Github")
            .assertStatus(HttpURLConnection.HTTP_OK)
            .getBody();
    }

    @Override
    public void write(final String file, final String content) {
        final URI uri = Github.ENTRY.clone()
            .path("/gists/{name}")
            .build(this.label);
        final StringWriter post = new StringWriter();
        Json.createGenerator(post)
            .writeStartObject()
            .writeStartObject("files")
            .writeStartObject(file)
            .write("content", content)
            .writeEnd()
            .writeEnd()
            .writeEnd()
            .close();
        RestTester.start(uri)
            .header(HttpHeaders.AUTHORIZATION, this.header)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .patch("save file to gist of Github", post.toString())
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

}
