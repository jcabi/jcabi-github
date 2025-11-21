/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Loggable;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * GitHub pull request ref.
 * @since 0.24
 */
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "github", "jsn" })
final class RtPullRef implements PullRef {
    /**
     * API entry point.
     */
    private final transient GitHub github;

    /**
     * JSON of the pull request ref.
     */
    private final transient JsonObject jsn;

    /**
     * Public ctor.
     * @param gthb GitHub
     * @param json Pull request ref JSON object
     */
    RtPullRef(final GitHub gthb, final JsonObject json) {
        this.github = gthb;
        this.jsn = json;
    }

    @Override
    public JsonObject json() {
        return this.jsn;
    }

    @Override
    public String ref() {
        return this.jsn.getString("ref");
    }

    @Override
    public String sha() {
        return this.jsn.getString("sha");
    }

    @Override
    public Repo repo() {
        return this.github.repos().get(
            new Coordinates.Simple(
                this.jsn.getJsonObject("repo").getString("full_name")
            )
        );
    }
}
