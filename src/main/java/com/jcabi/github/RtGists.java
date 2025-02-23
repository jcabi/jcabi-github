/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import lombok.EqualsAndHashCode;

/**
 * Github gists.
 *
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "request" })
final class RtGists implements Gists {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     */
    RtGists(final Github github, final Request req) {
        this.entry = req;
        this.ghub = github;
        this.request = this.entry.uri().path("/gists").back();
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
    public Gist create(final Map<String, String> files, final boolean visible
    ) throws IOException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (final Map.Entry<String, String> file : files.entrySet()) {
            builder = builder.add(
                file.getKey(),
                Json.createObjectBuilder().add("content", file.getValue())
            );
        }
        final JsonStructure json = Json.createObjectBuilder()
            .add("files", builder)
            .add("public", visible)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getString("id")
        );
    }

    @Override
    public Gist get(final String name) {
        return new RtGist(this.ghub, this.entry, name);
    }

    @Override
    public Iterable<Gist> iterate() {
        return new RtPagination<>(
            this.request,
            object -> this.get(object.getString("id"))
        );
    }

    @Override
    public void remove(
        final String identifier
    ) throws IOException {
        this.request.method(Request.DELETE)
            .uri().path(identifier).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }
}
