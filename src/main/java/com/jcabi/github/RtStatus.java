/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.StringReader;
import lombok.EqualsAndHashCode;

/**
 * Github commit status.
 * @since 0.23
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "cmmt", "jsn" })
public final class RtStatus implements Status {
    /**
     * Associated commit.
     */
    private final transient Commit cmmt;
    /**
     * Encapsulated status JSON.
     */
    private final transient String jsn;

    /**
     * Public ctor.
     * @param cmt Associated commit
     * @param obj Status JSON object
     */
    public RtStatus(
        final Commit cmt,
        final JsonObject obj
    ) {
        this.cmmt = cmt;
        this.jsn = obj.toString();
    }

    @Override
    public JsonObject json() {
        return Json.createReader(new StringReader(this.jsn)).readObject();
    }

    @Override
    public int identifier() {
        return this.json().getInt("id");
    }

    @Override
    public String url() {
        return this.json().getString("url");
    }

    @Override
    public Commit commit() {
        return this.cmmt;
    }
}
