/**
 * Copyright (c) 2012-2013, JCabi.com
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

import com.jcabi.log.Logger;
import com.rexsl.test.Request;
import com.rexsl.test.Response;
import com.rexsl.test.Wire;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Wire that waits if number of remaining request per hour is less than
 * a given threshold.
 *
 * <p>Github sets following headers in every response:
 * {@code X-RateLimit-Limit}, {@code X-RateLimit-Remaining} and
 * {@code X-RateLimit-Reset}. If {@code X-RateLimit-Remaining} is
 * less than a given threshold, {@code CarefulWire} will sleep before a time
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
@ToString
@EqualsAndHashCode(of = "origin")
public final class CarefulWire implements Wire {

    /**
     * Original wire.
     */
    private final transient Wire origin;

    /**
     * Threshold of number of remaining requests, below which requests are
     * blocked before reset.
     */
    private final int threshold;

    /**
     * Time of limit resetting. If it's 0, there is no need to block requests.
     */
    private long resetTime;

    /**
     * Monitor for the resetTime.
     */
    private final Object resetTimeMonitor = new Object();

    /**
     * Public ctor.
     *
     * @param wire Original wire
     * @param threshold Threshold of number of remaining requests, below which
     *  requests are blocked before reset
     * @checkstyle HiddenField (3 lines)
     */
    public CarefulWire(@NotNull(message = "wire can't be NULL")
        final Wire wire, final int threshold) {
        this.origin = wire;
        this.threshold = threshold;
    }

    /**
     * {@inheritDoc}
     * @checkstyle ParameterNumber (6 lines)
     */
    @Override
    public Response send(final Request req, final String home,
        final String method,
        final Collection<Map.Entry<String, String>> headers,
        final byte[] content) throws IOException {
        synchronized (this.resetTimeMonitor) {
            if (this.resetTime != 0) {
                // @checkstyle MagicNumber (1 line)
                final long now = System.currentTimeMillis() / 1000L;
                if (this.resetTime > now) {
                    final long length = this.resetTime - now;
                    Logger.info(
                        this,
                        // @checkstyle LineLength (1 line)
                        "Remaining number of requests per hour is less than %d. Waiting for %d seconds.",
                        this.threshold, length
                    );
                    try {
                        // @checkstyle MagicNumber (1 line)
                        Thread.sleep(length * 1000L);
                    } catch (final InterruptedException ex) {
                        throw new IOException(ex);
                    }
                }
                this.resetTime = 0L;
            }
        }
        final Response resp = this.origin
            .send(req, home, method, headers, content);
        final String remainingHeader = "X-RateLimit-Remaining";
        if (resp.headers().containsKey(remainingHeader)) {
            final int remaining = Integer.parseInt(
                resp.headers().get(remainingHeader).get(0)
            );
            if (remaining < this.threshold) {
                synchronized (this.resetTimeMonitor) {
                    this.resetTime = Long.parseLong(
                        resp.headers().get("X-RateLimit-Reset").get(0)
                    );
                }
            }
        }
        return resp;
    }
}
