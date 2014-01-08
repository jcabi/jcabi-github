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

import com.rexsl.test.Request;
import com.rexsl.test.Response;
import com.rexsl.test.Wire;
import com.rexsl.test.request.ApacheRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link CarefulWire}.
 *
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
public final class CarefulWireTest {

    /**
     * CarefulWire can wait before the limit reset.
     * @throws IOException If some problem inside
     */
    @Test
    public void waitBeforeReset() throws IOException {
        final Response resp = Mockito.mock(Response.class);
        // @checkstyle MagicNumber (1 line)
        final long resetTime = System.currentTimeMillis() / 1000L + 5L;
        final Map<String, List<String>> headers =
            new HashMap<String, List<String>>(1);
        final List<String> remainingval = new ArrayList<String>(1);
        remainingval.add("9");
        headers.put("X-RateLimit-Remaining", remainingval);
        final List<String> resetval = new ArrayList<String>(1);
        resetval.add(String.valueOf(resetTime));
        headers.put("X-RateLimit-Reset", resetval);
        Mockito.when(resp.headers()).thenReturn(headers);
        final long[] requestTime = new long[1];
        final Wire origin = new Wire() {
            // @checkstyle ParameterNumber (5 lines)
            @Override
            public Response send(final Request req, final String home,
                final String method,
                final Collection<Map.Entry<String, String>> headers,
                final byte[] content) {
                // @checkstyle MagicNumber (1 line)
                requestTime[0] = System.currentTimeMillis() / 1000L;
                return resp;
            }
        };
        final Wire wire = new CarefulWire(origin, 10);
        final Request req = new ApacheRequest("");
        final Collection<Map.Entry<String, String>> reqHeaders =
            new ArrayList<Map.Entry<String, String>>(0);
        final byte[] content = new byte[1];
        wire.send(req, "", "", reqHeaders, content);
        wire.send(req, "", "", reqHeaders, content);
        MatcherAssert.assertThat(
            requestTime[0], Matchers.greaterThanOrEqualTo(resetTime)
        );
    }
}
