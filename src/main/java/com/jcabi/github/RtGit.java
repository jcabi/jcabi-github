/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import lombok.EqualsAndHashCode;

/**
 * Github Git.
 *
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "owner" })
final class RtGit implements Git {

    /**
     * Repository.
     */
    private final transient Repo owner;

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     */
    public RtGit(final Request req, final Repo repo) {
        this.entry = req;
        this.owner = repo;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Blobs blobs() {
        return new RtBlobs(this.entry, this.repo());
    }

    @Override
    public Commits commits() {
        return new RtCommits(this.entry, this.owner);
    }

    @Override
    public References references() {
        return new RtReferences(this.entry, this.owner);
    }

    @Override
    public Tags tags() {
        return new RtTags(this.entry, this.owner);
    }

    @Override
    public Trees trees() {
        return new RtTrees(this.entry, this.repo());
    }
}
