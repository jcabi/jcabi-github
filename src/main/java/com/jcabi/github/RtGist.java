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
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * Github gist.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 * @todo #114 RtGist.unstar() method as long as unit test have to be
 *  implemented.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "request" })
@SuppressWarnings("PMD.TooManyMethods")
final class RtGist implements Gist {
    /**
     * RESTful request for the gist.
     */
    private final transient Request request;

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * Gist id.
     */
    private final transient String gist;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param name Name of gist
     */
    RtGist(final Github github, final Request req, final String name) {
        this.ghub = github;
        this.entry = req;
        this.gist = name;
        this.request = req.uri().path("/gists").path(name).back();
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public String identifier() {
        return this.gist;
    }

    @Override
    public String read(@NotNull(message = "file name can't be NULL")
        final String file) throws IOException {
        final Response response = this.request.fetch();
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
        final JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("content", content);
        final JsonStructure json = Json.createObjectBuilder()
            .add("files", Json.createObjectBuilder().add(file, builder))
            .build();
        this.request.method(Request.PATCH)
            .body().set(json).back().fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public void star() throws IOException {
        this.request.uri().path("star").back()
            .method("PUT")
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public void unstar() throws IOException {
        this.request.uri().path("star").back()
            .method(Request.DELETE)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public boolean starred() throws IOException {
        final RestResponse response = this.request.uri().path("star").back()
            .method("GET").fetch()
            .as(RestResponse.class).assertStatus(
                Matchers.isOneOf(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    HttpURLConnection.HTTP_NOT_FOUND
            )
        );
        return response.status() == HttpURLConnection.HTTP_NO_CONTENT;
    }

    @Override
    public Gist fork() throws IOException {
        final String name = this.request.uri().path("/forks").back()
            .method(Request.POST)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_CREATED)
            .as(JsonResponse.class)
            .json().readObject().getString("id");
        return new RtGist(this.ghub, this.entry, name);
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public GistComments comments() throws IOException {
        return new RtGistComments(this.entry, this);
    }

}
