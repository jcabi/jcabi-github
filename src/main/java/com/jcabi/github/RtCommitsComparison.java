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
import lombok.EqualsAndHashCode;

/**
 * Commits comparison.
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "request")
final class RtCommitsComparison implements CommitsComparison {

    /**
     * RESTful request for the comparison.
     */
    private final transient Request request;

    /**
     * Parent repository.
     */
    private final transient Repo owner;

    /**
     * Ctor.
     * @param req Entry point of API
     * @param repo Repository
     * @param base SHA of a base commit
     * @param head SHA of a head commit
     * @checkstyle ParameterNumber (3 lines)
     */
    RtCommitsComparison(final Request req, final Repo repo,
        final String base, final String head) {
        this(
            repo,
            req.uri()
                .path("/repos")
                .path(repo.coordinates().toString())
                .path("/compare")
                .path(String.format("%s...%s", base, head))
                .back()
        );
    }

    private RtCommitsComparison(final Repo repo, final Request req) {
        this.owner = repo;
        this.request = req;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Iterable<FileChange> files() throws IOException {
        return new FileChanges(this.json().getJsonArray("files"));
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
