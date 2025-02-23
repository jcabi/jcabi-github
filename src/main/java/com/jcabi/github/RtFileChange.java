/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Loggable;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * File change.
 * @since 0.24
 */
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "jsn" })
@ToString
final class RtFileChange implements FileChange {
    /**
     * Encapsulated JSON object.
     */
    private final transient JsonObject jsn;

    /**
     * Public ctor.
     * @param obj File change JSON object
     */
    public RtFileChange(
        final JsonObject obj
    ) {
        this.jsn = obj;
    }

    @Override
    public JsonObject json() {
        return this.jsn;
    }
}
