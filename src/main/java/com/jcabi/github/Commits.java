/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import jakarta.json.JsonObject;
import java.io.IOException;

/**
 * GitHub Git Data Commits.
 * @see <a href="https://developer.github.com/v3/git/commits/">Commits API</a>
 * @since 0.8
 */
@Immutable
public interface Commits {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Create a Commit object.
     * @param params The input for creating the Tag.
     * @return Commit
     * @throws IOException - If anything goes wrong.
     */
    Commit create(JsonObject params) throws IOException;

    /**
     * Return a Commit by its SHA.
     * @param sha The sha of the Commit.
     * @return Commit
     */
    Commit get(String sha);

    /**
     * Return a Statuses object for a given ref (sha, branch name, etc).
     * @param ref The ref of the Commit
     * @return Status
     * @since 0.23
     */
    Statuses statuses(String ref);
}
