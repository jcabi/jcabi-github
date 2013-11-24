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
import com.rexsl.test.Request;
import com.rexsl.test.Response;
import com.rexsl.test.response.JsonResponse;
import com.rexsl.test.response.RestResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(of = { "ghub", "entry" })
final class GhGist implements Gist {

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param name Name of gist
     */
    GhGist(final Github github, final Request req, final String name) {
        this.ghub = github;
        this.entry = req.uri().path("/gists").path(name).back();
    }

    @Override
    public String toString() {
        return this.entry.uri().get().toString();
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public String read(@NotNull(message = "file name can't be NULL")
        final String file) throws IOException {
        final Response response = this.entry.fetch();
        final String url = response
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readObject().getJsonObject("files")
            .getJsonObject(file).getString("raw_url");
        return response
            .as(RestResponse.class)
            .jump(URI.create(url))
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .body();
    }

    @Override
    public void write(
        @NotNull(message = "file name can't be NULL") final String file,
        @NotNull(message = "file content can't be NULL") final String content)
        throws IOException {
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
        this.entry.method(Request.PATCH)
            .body().set(post.toString()).back().fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public JsonObject json() throws IOException {
        return this.entry.fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readObject();
    }

}
