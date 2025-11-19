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
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * GitHub public keys.
 * @see <a href="https://developer.github.com/v3/users/keys/">Public Keys API</a>
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "request", "owner" })
final class RtPublicKeys implements PublicKeys {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * User we're in.
     */
    private final transient User owner;

    /**
     * Public ctor.
     *
     * @param req Request
     * @param user User
     */
    RtPublicKeys(final Request req, final User user) {
        this.entry = req;
        this.owner = user;
        this.request = this.entry.uri().path("/user").path("/keys").back();
    }

    @Override
    public User user() {
        return this.owner;
    }

    @Override
    public Iterable<PublicKey> iterate() {
        return new RtPagination<>(
            this.request,
            object -> this.get(object.getInt("id"))
        );
    }

    @Override
    public PublicKey create(
        final String title,
        final String key
    ) throws IOException {
        return this.get(
            this.request.method(Request.POST)
                .body().set(
                    Json.createObjectBuilder()
                        .add("title", title)
                        .add("key", key)
                        .build()
                ).back()
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_CREATED)
                .as(JsonResponse.class)
                .json().readObject().getInt("id")
        );
    }

    @Override
    public PublicKey get(final int number) {
        return new RtPublicKey(this.entry, this.owner, number);
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
    public String toString() {
        return this.request.uri().get().toString();
    }

}
