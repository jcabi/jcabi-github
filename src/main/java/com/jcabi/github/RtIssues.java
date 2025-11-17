/*
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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import jakarta.json.Json;
import jakarta.json.JsonStructure;
import lombok.EqualsAndHashCode;

/**
 * Github issues.
 *
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
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
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/issues")
            .back();
        this.owner = repo;
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
        final String body)
        throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add("title", title)
            .add("body", body)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("number")
        );
    }

    @Override
    public Iterable<Issue> iterate(
        final Map<String, String> params) {
        return new RtPagination<>(
            this.request.uri().queryParams(params).back(),
            object -> this.get(object.getInt("number"))
        );
    }

    @Override
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    public Iterable<Issue> search(
        final Sort sort,
        final Search.Order direction,
        final EnumMap<Qualifier, String> qualifiers) {
        final Map<String, String> params = new HashMap<>();
        for (final EnumMap.Entry<Qualifier, String> pair : qualifiers
            .entrySet()) {
            params.put(pair.getKey().identifier(), pair.getValue());
        }
        params.put("sort", sort.identifier());
        params.put("direction", direction.identifier());
        return this.iterate(params);
    }
}
