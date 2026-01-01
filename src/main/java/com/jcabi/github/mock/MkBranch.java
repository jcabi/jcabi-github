/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Branch;
import com.jcabi.github.Commit;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;

/**
 * Mock Git branch.
 *
 * @since 0.24
 */
public final class MkBranch implements Branch {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo name.
     */
    private final transient Coordinates coords;

    /**
     * Branch name.
     */
    private final transient String branch;

    /**
     * Commit sha.
     */
    private final transient String hash;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param nom Branch name
     * @param sha Commit sha
     * @todo #1085:30m Refactor this to reduce number of arguments to avoid
     *  ParameterNumberCheck warning.
     * @checkstyle ParameterNumberCheck (7 lines)
     */
    MkBranch(
        final MkStorage stg,
        final String login,
        final Coordinates rep,
        final String nom,
        final String sha
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.branch = nom;
        this.hash = sha;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public String name() {
        return this.branch;
    }

    @Override
    public Commit commit() {
        return new MkCommit(this.storage, this.self, this.coords, this.hash);
    }
}
