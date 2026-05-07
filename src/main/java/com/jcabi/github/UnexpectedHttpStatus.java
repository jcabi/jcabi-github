/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;

/**
 * Thrown when a GitHub API response has an unexpected HTTP status code.
 * <p>This is a subclass of {@link IOException} that wraps the
 * {@link AssertionError} produced by
 * {@code com.jcabi.http.response.RestResponse#assertStatus(int)}, so that
 * callers can recover from non-success HTTP responses (e.g. {@code 404}) using
 * a normal exception rather than an {@link Error}.
 *
 * @since 2.0
 */
public final class UnexpectedHttpStatus extends IOException {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = -3286948164128771947L;

    /**
     * Ctor.
     * @param cause The original assertion error
     */
    UnexpectedHttpStatus(final AssertionError cause) {
        super(cause.getMessage(), cause);
    }
}
