/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Branch;
import com.jcabi.github.PullRef;
import com.jcabi.github.Repo;
import java.io.IOException;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock GitHub pull request ref.
 *
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "branch" })
final class MkPullRef implements PullRef {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Branch.
     */
    private final transient Branch branch;

    /**
     * Public ctor.
     * @param stg Storage
     * @param brnch Branch
     */
    MkPullRef(
        final MkStorage stg,
        final Branch brnch
    ) {
        this.storage = stg;
        this.branch = brnch;
    }

    @Override
    public Repo repo() {
        return this.branch.repo();
    }

    @Override
    public String ref() {
        return this.branch.name();
    }

    @Override
    public String sha() {
        return this.branch.commit().sha();
    }

    @Override
    public JsonObject json() throws IOException {
        final String user = this.repo().coordinates().user();
        return Json.createObjectBuilder()
            .add("ref", this.ref())
            .add("sha", this.sha())
            .add("label", String.format("%s:%s", user, this.ref()))
            .add("user", new MkUser(this.storage, user).json())
            .add("repo", this.repo().json())
            .build();
    }
}
