/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import jakarta.json.JsonObject;

/**
 * JSON patchable.
 *
 * @since 0.4
 */
@Immutable
public interface JsonPatchable {

    /**
     * Patch using this JSON object.
     * @param json JSON object
     * @throws IOException If there is any I/O problem
     */
    void patch(JsonObject json) throws IOException;

}
