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
import java.util.Map;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonStructure;
import lombok.EqualsAndHashCode;

/**
 * Github hooks.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "owner", "request" })
final class RtHooks implements Hooks {
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
    public RtHooks(final Request req, final Repo repo) {
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/hooks")
            .back();
        this.owner = repo;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Iterable<Hook> iterate() {
        return new RtPagination<>(
            this.request,
            object -> {
                // @checkstyle MultipleStringLiterals (1 line)
                return this.get(object.getInt("id"));
            }
        );
    }

    @Override
    public void remove(final int number) throws IOException {
        this.request.method(Request.DELETE)
            .uri().path(Integer.toString(number)).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Hook get(final int number) {
        return new RtHook(this.entry, this.owner, number);
    }

    // @checkstyle ParameterNumberCheck (2 lines)
    @Override
    public Hook create(
        final String name,
        final Map<String, String> config,
        final Iterable<Event> events,
        final boolean active
    ) throws IOException {
        final JsonObjectBuilder configs = Json.createObjectBuilder();
        for (final Map.Entry<String, String> entr : config.entrySet()) {
            configs.add(entr.getKey(), entr.getValue());
        }
        final JsonArrayBuilder evnts = Json.createArrayBuilder();
        for (final Event event : events) {
            evnts.add(event.toString());
        }
        final JsonStructure json = Json.createObjectBuilder()
            .add("name", name)
            .add("config", configs)
            .add("active", active)
            .add("events", evnts)
            .build();
        return this.get(
            this.request.method(Request.POST)
                .body().set(json).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("id")
        );
    }
}
