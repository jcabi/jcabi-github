/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * Github reference.
 *
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "name" })
final class RtReference implements Reference {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * Name of the reference.
     */
    private final transient String name;

    /**
     * Public constructor.
     * @param req RESTful request.
     * @param repo Owner of this reference.
     * @param ref The name of the reference.
     */
    RtReference(final Request req, final Repo repo, final String ref) {
        this.request = req.uri()
            .path("/repos").path(repo.coordinates().user())
            .path(repo.coordinates().repo()).path("/git").path(ref).back();
        this.owner = repo;
        this.name = ref;
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public String ref() {
        return this.name;
    }

    @Override
    public void patch(
        final JsonObject json) throws IOException {
        new RtJson(this.request).patch(json);
    }
}
