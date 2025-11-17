/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Loggable;
import com.jcabi.github.FileChange;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock file change.
 * @since 0.24
 */
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "jsn")
@ToString
public final class MkFileChange implements FileChange {
    /**
     * Encapsulated file change JSON object.
     */
    private final transient JsonObject jsn;

    /**
     * Public ctor.
     * @param obj File change JSON object
     */
    public MkFileChange(
        final JsonObject obj
    ) {
        this.jsn = obj;
    }

    @Override
    public JsonObject json() {
        return this.jsn;
    }
}
