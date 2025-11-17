/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.google.common.collect.ImmutableList;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.CommitsComparison;
import com.jcabi.github.Coordinates;
import com.jcabi.github.FileChange;
import com.jcabi.github.Repo;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.ToString;

/**
 * Mock commits' comparison of a Github repository.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
final class MkCommitsComparison implements CommitsComparison {
    /**
     * File change JSON object.
     */
    private static final JsonObject FILE_JSON = Json.createObjectBuilder()
        .add("sha", "bbcd538c8e72b8c175046e27cc8f907076331401")
        .add("filename", "test-file")
        // @checkstyle MultipleStringLiterals (1 lines)
        .add("status", "modified")
        .build();

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo coordinates.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param repo Repository coordinates
     */
    MkCommitsComparison(
        final MkStorage stg,
        final String login,
        final Coordinates repo
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = repo;
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public JsonObject json() {
        return Json.createObjectBuilder()
            // @checkstyle MultipleStringLiterals (3 lines)
            .add("status", "test-status")
            .add("ahead_by", 1)
            .add("behind_by", 2)
            .add(
                "author",
                Json.createObjectBuilder()
                    // @checkstyle MultipleStringLiterals (3 lines)
                    .add("login", "test")
                    .build()
            )
            .add(
                "files",
                Json.createArrayBuilder()
                    .add(MkCommitsComparison.FILE_JSON)
                    .build()
            )
            .add("commits", Json.createArrayBuilder().build())
            .build();
    }

    @Override
    public Iterable<FileChange> files() {
        return ImmutableList.of(
            new MkFileChange(MkCommitsComparison.FILE_JSON)
        );
    }
}
