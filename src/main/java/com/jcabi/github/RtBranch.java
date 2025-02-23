/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import lombok.EqualsAndHashCode;

/**
 * Git branch implementation.
 *
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "owner", "nam", "hash" })
public final class RtBranch implements Branch {
    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * Repository we're in.
     */
    private final transient Repo owner;

    /**
     * Name of this branch.
     */
    private final transient String nam;

    /**
     * Commit SHA hash.
     */
    private final transient String hash;

    /**
     * Public ctor.
     * @param req Request
     * @param repo Repository
     * @param nom Name of branch
     * @param sha Commit SHA hash
     * @checkstyle ParameterNumberCheck (6 lines)
     */
    RtBranch(
        final Request req,
        final Repo repo,
        final String nom,
        final String sha) {
        this.entry = req;
        this.owner = repo;
        this.nam = nom;
        this.hash = sha;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public String name() {
        return this.nam;
    }

    @Override
    public Commit commit() {
        return new RtCommit(this.entry, this.owner, this.hash);
    }
}
