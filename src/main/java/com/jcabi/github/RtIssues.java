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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;

/**
 * GitHub issues.
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
final class RtIssues implements Issues {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     */
    RtIssues(final Request req, final Repo repo) {
        this(
            req,
            req.uri()
                .path("/repos")
                .path(repo.coordinates().user())
                .path(repo.coordinates().repo())
                .path("/issues")
                .back(),
            repo
        );
    }

    private RtIssues(final Request entry, final Request request, final Repo owner) {
        this.entry = entry;
        this.request = request;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Issue get(final int number) {
        return new RtIssue(this.entry, this.owner, number);
    }

    @Override
    public Issue create(
        final String title,
        final String body) throws IOException {
        return this.get(
            this.request.method(Request.POST)
                .body().set(
                    Json.createObjectBuilder()
                        .add("title", title)
                        .add("body", body)
                        .build()
                ).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("number")
        );
    }

    @Override
    public Iterable<Issue> iterate(final Map<String, String> params) {
        return new RtPagination<>(
            this.request.uri().queryParams(params).back(),
            object -> this.get(object.getInt("number"))
        );
    }

    @Override
    public Iterable<Issue> search(
        final Issues.Sort sort,
        final Search.Order direction,
        final Map<Issues.Qualifier, String> qualifiers) {
        final Map<String, String> params = new HashMap<>();
        for (final Map.Entry<Issues.Qualifier, String> pair : qualifiers
            .entrySet()) {
            params.put(pair.getKey().identifier(), pair.getValue());
        }
        params.put("sort", sort.identifier());
        params.put("direction", direction.identifier());
        return this.iterate(params);
    }
}
