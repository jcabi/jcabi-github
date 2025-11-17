/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * Github gist.
 *
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
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
    public String read(final String file) throws IOException {
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
        final String file,
        final String content)
        throws IOException {
        final JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("content", content);
        final JsonObject json = Json.createObjectBuilder()
            .add("files", Json.createObjectBuilder().add(file, builder))
            .build();
        this.patch(json);
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
        return this.request.uri().path("star").back()
            .method("GET").fetch()
            .as(RestResponse.class)
            .assertStatus(
                Matchers.is(
                    Matchers.oneOf(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        HttpURLConnection.HTTP_NOT_FOUND
                    )
                )
            )
            .status() == HttpURLConnection.HTTP_NO_CONTENT;
    }

    @Override
    public Gist fork() throws IOException {
        return new RtGist(
            this.ghub, this.entry,
            this.request.uri().path("/forks").back()
                .method(Request.POST)
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getString("id")
        );
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public GistComments comments() {
        return new RtGistComments(this.entry, this);
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        new RtJson(this.request).patch(json);
    }
}
