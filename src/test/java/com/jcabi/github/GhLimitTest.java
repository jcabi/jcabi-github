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
package com.jcabi.github;

import com.rexsl.test.Request;
import com.rexsl.test.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test case for {@link GhLimit}.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 */
public final class GhLimitTest {
    /**
     * Rate limit uri.
     */
    private static final String RATE_URI = "/rate_limit";

    /**
     * Describe Limit in a JSON object.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void json() throws Exception {
        final Github github = GhLimitTest.github();
        final Request request = new FakeRequest()
            .withBody(body());
        final GhLimit limit = new GhLimit(github, request, "core");
        MatcherAssert.assertThat(
            limit.json().toString(),
            Matchers.equalTo(
                "{\"limit\":5000,\"remaining\":4999,\"reset\":1372700873}"
            )
        );
    }

    /**
     * Test resources is not absent in JSON.
     *
     * @throws Exception if some problem inside
     */
    @Test(expected = IllegalStateException.class)
    public void absent() throws Exception {
        final Github github = GhLimitTest.github();
        final Request request = new FakeRequest()
            .withBody(body());
        final GhLimit limit = new GhLimit(github, request, "absent");
        MatcherAssert.assertThat(
            limit.json().toString(),
            Matchers.equalTo("{}")
        );
    }

    /**
     * Create and return repo to test.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Github github() throws Exception {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new DefaultGithub(key);
    }

    /**
     * Example response from rate API.
     * @return Body string.
     */
    private String body() {
        final StringBuilder builder = new StringBuilder();
        builder.append("{\"resources\":{\"core\":{\"limit\":5000,");
        builder.append("\"remaining\":4999,\"reset\":1372700873},");
        builder.append("\"search\":{\"limit\":20,\"remaining\":18,");
        builder.append("\"reset\":1372697452}},\"rate\":{\"limit\":5000,");
        builder.append("\"remaining\":4999,\"reset\":1372700873}}");
        return builder.toString();
    }
}
