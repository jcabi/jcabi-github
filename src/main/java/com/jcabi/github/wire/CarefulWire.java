/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.wire;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.Wire;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Wire that waits if number of remaining request per hour is less than
 * a given threshold.
 *
 * <p>Github sets following headers in each response:
 * {@code X-RateLimit-Limit}, {@code X-RateLimit-Remaining}, and
 * {@code X-RateLimit-Reset}. If {@code X-RateLimit-Remaining} is
 * less than a given threshold, {@code CarefulWire} will sleep until a time
 * specified in the {@code X-RateLimit-Reset} header. For further information
 * about the Github rate limiting see
 * <a href="https://developer.github.com/v3/#rate-limiting">API
 * documentation</a>.
 *
 * <p>You can use {@code CarefulWire} with a {@link com.jcabi.github.Github}
 * object:
 * <pre>
 * {@code
 * Github github = new RtGithub(
 *     new RtGithub().entry().through(CarefulWire.class, 50)
 * );
 * }
 * </pre>
 *
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "origin", "threshold" })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class CarefulWire implements Wire {

    /**
     * Original wire.
     */
    private final transient Wire origin;

    /**
     * Threshold of number of remaining requests, below which requests are
     * blocked until reset.
     */
    private final transient int threshold;

    /**
     * Public ctor.
     *
     * @param wire Original wire
     * @param thrshld Threshold of number of remaining requests, below which
     *  requests are blocked until reset
     */
    public CarefulWire(final Wire wire, final int thrshld) {
        this.origin = wire;
        this.threshold = thrshld;
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
        final Response resp = this.origin
            .send(req, home, method, headers, content, connect, read);
        final int remaining = this.remainingHeader(resp);
        if (remaining < this.threshold) {
            final long reset = this.resetHeader(resp);
            final long now = TimeUnit.MILLISECONDS
                .toSeconds(System.currentTimeMillis());
            if (reset > now) {
                final long length = reset - now;
                Logger.info(
                    this,
                    // @checkstyle LineLength (1 line)
                    "Remaining number of requests per hour is less than %d. Waiting for %d seconds.",
                    this.threshold, length
                );
                try {
                    TimeUnit.SECONDS.sleep(length);
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(ex);
                }
            }
        }
        return resp;
    }

    /**
     * Get the header with the given name from the response.
     * If there is no such header, returns null.
     * @param resp Response to get header from
     * @param headername Name of header to get
     * @return The value of the first header with the given name, or null.
     */
    private String headerOrNull(
        final Response resp,
        final String headername) {
        final List<String> values = resp.headers().get(headername);
        String value = null;
        if (values != null && !values.isEmpty()) {
            value = values.get(0);
        }
        return value;
    }

    /**
     * Returns the value of the X-RateLimit-Remaining header.
     * If there is no such header, returns Integer.MAX_VALUE (no rate limit).
     * @param resp Response to get header from
     * @return Number of requests remaining before the rate limit will be hit
     */
    private int remainingHeader(
        final Response resp) {
        final String remainingstr = this.headerOrNull(
            resp,
            "X-RateLimit-Remaining"
        );
        int remaining = Integer.MAX_VALUE;
        if (remainingstr != null) {
            remaining = Integer.parseInt(remainingstr);
        }
        return remaining;
    }

    /**
     * Returns the value of the X-RateLimit-Reset header.
     * If there is no such header, returns 0 (reset immediately).
     * @param resp Response to get header from
     * @return Timestamp (in seconds) at which the rate limit will reset
     */
    private long resetHeader(
        final Response resp) {
        final String resetstr = this.headerOrNull(resp, "X-RateLimit-Reset");
        long reset = 0;
        if (resetstr != null) {
            reset = Long.parseLong(resetstr);
        }
        return reset;
    }

}
