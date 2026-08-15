/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * GitHub get labels.
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
        this(
            issue,
            req,
            req.uri()
                .path("/repos")
                .path(issue.repo().coordinates().user())
                .path(issue.repo().coordinates().repo())
                .path("/issues")
                .path(Integer.toString(issue.number()))
                .path("/labels")
                .back()
        );
    }

    private RtIssueLabels(final Issue owner, final Request entry, final Request request) {
        this.owner = owner;
        this.entry = entry;
        this.request = request;
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
        this.request.method(Request.POST)
            .body().set(builder.build()).back()
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
        this.request.method(Request.PUT)
            .body().set(builder.build()).back()
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
