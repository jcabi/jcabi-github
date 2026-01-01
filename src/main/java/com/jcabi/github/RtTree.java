/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import jakarta.json.JsonObject;
import java.io.IOException;

/**
 * GitHub tree.
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
final class RtTree implements Tree {

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Repo we're in.
     */
    private final transient Repo owner;

    /**
     * Commit SHA hash.
     */
    private final transient String hash;

    /**
     * Public ctor.
     * @param req RESTful request
     * @param repo Owner of this commit
     * @param sha Number of the get
     */
    RtTree(
        final Request req,
        final Repo repo,
        final String sha
    ) {
        final Coordinates coords = repo.coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/git")
            .path("/trees")
            .path(sha)
            .back();
        this.owner = repo;
        this.hash = sha;
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
    public String sha() {
        return this.hash;
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean result;
        if (this == obj) {
            result = true;
        } else if (obj == null || this.getClass() != obj.getClass()) {
            result = false;
        } else {
            final RtTree other = (RtTree) obj;
            result = this.request.equals(other.request)
                && this.owner.equals(other.owner)
                && this.hash.equals(other.hash);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = this.request.hashCode();
        result = 31 * result + this.owner.hashCode();
        result = 31 * result + this.hash.hashCode();
        return result;
    }
}
