/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.wire;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.Wire;
import com.jcabi.http.wire.RetryWire;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Wire that waits if the number of remaining requests per hour is less than
 * a given threshold, and in the event of {@link IOException} retries a few
 * times before giving up and rethrowing the exception.
 *
 * <p>Just a wrapper for a {@link com.jcabi.http.wire.RetryWire} that wraps a
 * {@link com.jcabi.github.wire.CarefulWire} that wraps the underlying wire.
 *
 * <p>You can use {@code RetryCarefulWire} with a
 * {@link com.jcabi.github.Github} object:
 * <pre>
 * {@code
 * Github github = new RtGithub(
 *     new RtGithub().entry().through(RetryCarefulWire.class, 50)
 * );
 * }
 * </pre>
 *
 * @since 0.23
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "real" })
public final class RetryCarefulWire implements Wire {
    /**
     * RetryWire which we're merely wrapping.
     */
    private final transient Wire real;

    /**
     * Public ctor.
     * @param wire Original wire
     * @param threshold Threshold of number of remaining requests, below which
     *  requests are blocked until reset
     */
    public RetryCarefulWire(final Wire wire, final int threshold) {
        this.real = new RetryWire(new CarefulWire(wire, threshold));
    }

    @Override
    // @checkstyle ParameterNumber (8 lines)
    public Response send(
        final Request req,
        final String home,
        final String method,
        final Collection<Map.Entry<String, String>> headers,
        final InputStream content,
        final int connect, final int read
    ) throws IOException {
        return this.real.send(
            req, home, method, headers, content, connect, read
        );
    }

}
