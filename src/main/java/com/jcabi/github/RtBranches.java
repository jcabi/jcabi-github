/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.util.stream.StreamSupport;
import lombok.EqualsAndHashCode;

/**
 * Git branches.
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = {"entry", "request", "owner" })
@SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
final class RtBranches implements Branches {
    /**
     * RESTful API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request for the branches.
     */
    private final transient Request request;

    /**
     * Parent repository.
     */
    private final transient Repo owner;

    /**
     * Public ctor.
     * @param req Entry point of API
     * @param repo Repository
     */
    RtBranches(final Request req, final Repo repo) {
        this.entry = req;
        this.owner = repo;
        final Coordinates coords = repo.coordinates();
        this.request = this.entry.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/branches")
            .back();
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Iterable<Branch> iterate() {
        return new RtPagination<>(
            this.request,
            object -> new RtBranch(
                this.entry,
                this.owner,
                object.getString("name"),
                object.getJsonObject("commit").getString("sha")
            )
        );
    }

    @Override
    public Branch find(final String name) {
        return StreamSupport.stream(this.iterate().spliterator(), false).filter(
            item -> item.name().equals(name)
        ).findFirst().orElse(null);
    }
}
