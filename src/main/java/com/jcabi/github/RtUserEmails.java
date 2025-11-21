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
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.EqualsAndHashCode;

/**
 * GitHub user's emails.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "request")
final class RtUserEmails implements UserEmails {

    /**
     * RESTful API request for the emails.
     */
    private final transient Request request;

    /**
     * Ctor.
     * @param req RESTful API entry point
     */
    RtUserEmails(final Request req) {
        this.request = req.header("Accept", "application/vnd.github.v3")
            .uri().path("/user/emails").back();
    }

    @Override
    public Iterable<String> iterate() throws IOException {
        final List<JsonObject> array = this.request.method(Request.GET)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(JsonResponse.class)
            .json().readArray().getValuesAs(JsonObject.class);
        final Collection<String> emails = new ArrayList<>(array.size());
        for (final JsonObject obj : array) {
            // @checkstyle MultipleStringLiterals (1 line)
            emails.add(obj.getString("email"));
        }
        return emails;
    }

    @Override
    public Iterable<String> add(
        final Iterable<String> emails
    ) throws IOException {
        final JsonArrayBuilder json = Json.createArrayBuilder();
        for (final String email : emails) {
            json.add(email);
        }
        final List<JsonObject> array = this.request.method(Request.POST)
            .body().set(json.build()).back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_CREATED)
            .as(JsonResponse.class)
            .json().readArray().getValuesAs(JsonObject.class);
        final Collection<String> result = new ArrayList<>(array.size());
        for (final JsonObject obj : array) {
            result.add(obj.getString("email"));
        }
        return result;
    }

    @Override
    public void remove(final Iterable<String> emails) throws IOException {
        final JsonArrayBuilder json = Json.createArrayBuilder();
        for (final String email : emails) {
            json.add(email);
        }
        this.request.method(Request.DELETE)
            .body().set(json.build()).back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

}
