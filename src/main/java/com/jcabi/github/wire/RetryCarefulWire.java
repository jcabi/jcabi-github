/**
 * Copyright (c) 2013-2015, jcabi.com
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
import com.jcabi.http.wire.RetryWire;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import javax.validation.constraints.NotNull;
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
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
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
    public RetryCarefulWire(@NotNull(message = "wire can't be NULL")
    final Wire wire, final int threshold) {
        this.real = new RetryWire(new CarefulWire(wire, threshold));
    }

    @Override
    @NotNull(message = "response can't be NULL")
    // @checkstyle ParameterNumber (8 lines)
    public Response send(
        @NotNull(message = "req can't be NULL") final Request req,
        @NotNull(message = "home can't be NULL") final String home,
        @NotNull(message = "method can't be NULL") final String method,
        @NotNull(message = "headers can't be NULL")
        final Collection<Map.Entry<String, String>> headers,
        @NotNull(message = "content can't be NULL")
        final InputStream content,
        final int connect, final int read
    ) throws IOException {
        return this.real.send(
            req, home, method, headers, content, connect, read
        );
    }

}
