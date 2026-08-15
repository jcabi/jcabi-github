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
 * GitHub repo commit.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "hash" })
final class RtRepoCommit implements RepoCommit {

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
    RtRepoCommit(final Request req, final Repo repo, final String sha) {
        this(
            req.uri()
                .path("/repos")
                .path(repo.coordinates().user())
                .path(repo.coordinates().repo())
                .path("/commits")
                .path(sha)
                .back(),
            sha,
            repo
        );
    }

    private RtRepoCommit(final Request request, final String hash, final Repo owner) {
        this.request = request;
        this.hash = hash;
        this.owner = owner;
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
    public int compareTo(final RepoCommit commit) {
        return this.sha().compareTo(commit.sha());
    }
}
