/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import jakarta.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github limit rate.
 *
 * @since 0.6
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "entry", "res" })
final class RtLimit implements Limit {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * Name of resource.
     */
    private final transient String res;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param name Name of resource
     */
    RtLimit(final Github github, final Request req, final String name) {
        this.entry = req;
        this.ghub = github;
        this.res = name;
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public JsonObject json() throws IOException {
        final JsonObject json = new RtJson(this.entry)
            .fetch()
            .getJsonObject("resources");
        if (!json.containsKey(this.res)) {
            throw new IllegalStateException(
                String.format(
                    "'%s' is absent in JSON: %s", this.res, json
                )
            );
        }
        return json.getJsonObject(this.res);
    }
}
