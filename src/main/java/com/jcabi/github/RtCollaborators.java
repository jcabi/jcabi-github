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
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * Implementation of Collaborators.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
@SuppressWarnings("PMD.SingularField")
final class RtCollaborators implements Collaborators {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param repo Repo
     * @param req Request
     */
    RtCollaborators(final Request req, final Repo repo) {
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/collaborators")
            .back();
        this.owner = repo;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public boolean isCollaborator(
        final String user)
        throws IOException {
        return this.request
            .method(Request.GET)
            .uri().path(user).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(
                Matchers.is(
                    Matchers.oneOf(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        HttpURLConnection.HTTP_NOT_FOUND
                    )
                )
            ).status() == HttpURLConnection.HTTP_NO_CONTENT;
    }

    @Override
    public void add(
        final String user)
        throws IOException {
        this.request.method(Request.PUT)
            .uri().path(user).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(
                Matchers.is(
                    Matchers.oneOf(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        HttpURLConnection.HTTP_CREATED
                    )
                )
            );
    }

    @Override
    public void addWithPermission(
        final String user, final Collaborators.Permission permission
    ) throws IOException {
        final JsonObject obj = Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (1 line)
            .add("permission", permission.toString().toLowerCase())
            .build();
        this.request.method(Request.PUT)
            .body().set(obj).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_CREATED);
    }

    @Override
    public String permission(final String user) throws IOException {
        return this.request
            .method(Request.GET)
            .uri().path(user).path("permission").back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readObject().getString("permission");
    }

    @Override
    public void remove(
        final String user
    )
        throws IOException {
        this.request.method(Request.DELETE)
            .uri().path(user).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Iterable<User> iterate() {
        return new RtPagination<>(
            this.request,
            object -> this.owner.github().users()
                .get(object.getString("login"))
        );
    }
}
