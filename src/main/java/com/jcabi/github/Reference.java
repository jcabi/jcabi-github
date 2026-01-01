/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import jakarta.json.JsonObject;
import java.io.IOException;

/**
 * GitHub Git Data Reference.
 * @since 0.6
 */
@Immutable
public interface Reference {
    /**
     * Return its owner repo.
     * @return Repo
     */
    Repo repo();

    /**
     * Return its name.
     * @return String
     */
    String ref();

    /**
     * Return its Json.
     * @return JsonObject
     * @throws IOException - If something goes wrong.
     */
    JsonObject json() throws IOException;

    /**
     * Patch using this JSON object.
     * @param json JSON object
     * @throws IOException If there is any I/O problem
     */
    void patch(JsonObject json)
        throws IOException;

}
