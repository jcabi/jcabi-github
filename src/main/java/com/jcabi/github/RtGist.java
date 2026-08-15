/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * GitHub gist.
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "request" })
final class RtGist implements Gist {

    /**
     * RESTful request for the gist.
     */
    private final transient Request request;

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

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
     * @param github GitHub
     * @param req Request
     * @param name Name of gist
     */
    RtGist(final GitHub github, final Request req, final String name) {
        this(github, req, name, req.uri().path("/gists").path(name).back());
    }

    private RtGist(
        final GitHub ghub,
        final Request entry,
        final String gist,
        final Request request
    ) {
        this.ghub = ghub;
        this.entry = entry;
        this.gist = gist;
        this.request = request;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public GitHub github() {
        return this.ghub;
    }

    @Override
    public String identifier() {
        return this.gist;
    }

    @Override
    public String read(final String file) throws IOException {
        final Response response = this.request.fetch();
        return response
            .as(RestResponse.class).jump(
                URI.create(
                    response
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK)
                        .as(JsonResponse.class)
                        .json().readObject().getJsonObject("files")
                        .getJsonObject(file).getString("raw_url")
                )
            )
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .body();
    }

    @Override
    public void write(
        final String file,
        final String content) throws IOException {
        this.patch(
            Json.createObjectBuilder().add(
                "files",
                Json.createObjectBuilder().add(
                    file,
                    Json.createObjectBuilder().add("content", content)
                )
            ).build()
        );
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
            .as(RestResponse.class).assertStatus(
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
