/**
 * Copyright (c) 2013-2014, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github.wire;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.Wire;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;
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
 * <a href="http://developer.github.com/v3/#rate-limiting">API
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
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "origin", "threshold" })
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
     *  requests are blocked util reset
     */
    public CarefulWire(@NotNull(message = "wire can't be NULL")
        final Wire wire, final int thrshld) {
        this.origin = wire;
        this.threshold = thrshld;
    }

    /**
     * {@inheritDoc}
     * @checkstyle ParameterNumber (6 lines)
     */
    @Override
    @NotNull(message = "response can't be NULL")
    public Response send(
        @NotNull(message = "req can't be NULL") final Request req,
        @NotNull(message = "home can't be NULL") final String home,
        @NotNull(message = "method can't be NULL")final String method,
        @NotNull(message = "headers can't be NULL")
        final Collection<Map.Entry<String, String>> headers,
        @NotNull(message = "content can't be NULL") final byte[] content
    ) throws IOException {
        final Response resp = this.origin
            .send(req, home, method, headers, content);
        final int remaining = Integer.parseInt(
            resp.headers().get("X-RateLimit-Remaining").get(0)
        );
        if (remaining < this.threshold) {
            final long reset = Long.parseLong(
                resp.headers().get("X-RateLimit-Reset").get(0)
            );
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
}
