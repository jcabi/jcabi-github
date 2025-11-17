/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github label.
 *
 * @since 0.6
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "txt" })
final class RtLabel implements Label {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Name of the label.
     */
    private final transient String txt;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     * @param name Name of it
     */
    RtLabel(final Request req, final Repo repo, final String name) {
        final Coordinates coords = repo.coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/labels")
            .path(name)
            .back();
        this.owner = repo;
        this.txt = name;
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
    public String name() {
        return this.txt;
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
        final Label label
    ) {
        return label.name().compareTo(label.name());
    }

}
