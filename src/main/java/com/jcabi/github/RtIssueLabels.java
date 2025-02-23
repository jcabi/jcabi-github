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
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonStructure;
import lombok.EqualsAndHashCode;

/**
 * Github get labels.
 *
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "entry")
final class RtIssueLabels implements IssueLabels {

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * RESTful entry.
     */
    private final transient Request request;

    /**
     * Which issue we belong to.
     */
    private final transient Issue owner;

    /**
     * Public ctor.
     * @param req Request
     * @param issue Issue we're in
     */
    RtIssueLabels(final Request req, final Issue issue) {
        this.owner = issue;
        final Coordinates coords = issue.repo().coordinates();
        this.entry = req;
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/issues")
            .path(Integer.toString(issue.number()))
            .path("/labels")
            .back();
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Issue issue() {
        return this.owner;
    }

    @Override
    public void add(final Iterable<String> labels) throws IOException {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (final String label : labels) {
            builder = builder.add(label);
        }
        final JsonStructure json = builder.build();
        this.request.method(Request.POST)
            .body().set(json).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readArray();
    }

    @Override
    public void replace(final Iterable<String> labels) throws IOException {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (final String label : labels) {
            builder = builder.add(label);
        }
        final JsonStructure json = builder.build();
        this.request.method(Request.PUT)
            .body().set(json).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readArray();
    }

    @Override
    public void remove(final String name) throws IOException {
        this.request.method(Request.DELETE)
            .uri().path(name).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public void clear() throws IOException {
        this.request.method(Request.DELETE)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Iterable<Label> iterate() {
        return new RtPagination<>(
            this.request,
            object -> new RtLabel(
                this.entry,
                this.owner.repo(),
                object.getString("name")
            )
        );
    }

}
