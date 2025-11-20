/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import jakarta.json.JsonObject;
import java.io.IOException;

/**
 * GitHub Git Data Tags.
 * @see <a href="https://developer.github.com/v3/git/tags/">Tags API</a>
 * @since 0.8
 */
@Immutable
public interface Tags {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Create a Tag object.
     * @param params The input for creating the Tag.
     * @return Tag
     * @throws IOException - If anything goes wrong.
     */
    Tag create(
        JsonObject params
    ) throws IOException;

    /**
     * Return a Tag by its SHA.
     * @param sha The sha of the Tag.
     * @return Tag
     */
    Tag get(String sha);

}
