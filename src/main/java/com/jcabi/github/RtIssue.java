/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * Github issue.
 *
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
@SuppressWarnings("PMD.TooManyMethods")
final class RtIssue implements Issue {

    /**
     * Content constant.
     */
    private static final String CONTENT = "content";

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
     * Issue number.
     */
    private final transient int num;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     * @param number Number of the get
     */
    RtIssue(final Request req, final Repo repo, final int number) {
        this.entry = req;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/issues")
            .path(Integer.toString(number))
            .back();
        this.owner = repo;
        this.num = number;
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
    public int number() {
        return this.num;
    }

    @Override
    public Comments comments() {
        return new RtComments(this.entry, this);
    }

    @Override
    public IssueLabels labels() {
        return new RtIssueLabels(this.entry, this);
    }

    @Override
    public Iterable<Event> events() {
        return new RtPagination<>(
            this.request.uri().path("/events").back(),
            object -> new RtEvent(
                this.entry,
                this.owner,
                object.getInt("id")
            )
        );
    }

    @Override
    public boolean exists() throws IOException {
        return new Existence(this).check();
    }

    @Override
    public void react(final Reaction reaction) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add(RtIssue.CONTENT, reaction.type())
            .build();
        this.request.method(Request.POST)
            .body().set(json).back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public Iterable<Reaction> reactions() {
        return new RtPagination<>(
            this.request.uri().path("/reactions").back(),
            object -> new Reaction.Simple(object.getString(RtIssue.CONTENT))
        );
    }

    @Override
    public void lock(final String reason) {
        final JsonStructure json = Json.createObjectBuilder()
            .add("lock_reason", reason)
            .build();
        try {
            this.request.method(Request.PUT).uri().path("/lock").back()
                .body().set(json).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
        } catch (final IOException error) {
            throw new IllegalStateException(error);
        }
    }

    @Override
    public void unlock() {
        try {
            this.request.method(Request.DELETE).uri().path("/lock").back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
        } catch (final IOException error) {
            throw new IllegalStateException(error);
        }
    }

    @Override
    public boolean isLocked() {
        boolean locked = false;
        try {
            locked ^=
                this.request.method(Request.PUT).uri().path("/lock").back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_NO_CONTENT).back().body()
                .get().isEmpty();
        } catch (final IOException error) {
            locked = false;
        }
        return locked;
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public int compareTo(
        final Issue issue
    ) {
        return this.number() - issue.number();
    }

}
