/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import lombok.EqualsAndHashCode;

/**
 * GitHub limit rate.
 * @since 0.6
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "entry" })
final class RtLimits implements Limits {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

    /**
     * Public ctor.
     * @param github GitHub
     * @param req Request
     */
    RtLimits(final GitHub github, final Request req) {
        this(req.uri().path("rate_limit").back(), github);
    }

    private RtLimits(final Request entry, final GitHub ghub) {
        this.entry = entry;
        this.ghub = ghub;
    }

    @Override
    public GitHub github() {
        return this.ghub;
    }

    @Override
    public Limit get(final String resource) {
        return new RtLimit(this.ghub, this.entry, resource);
    }
}
