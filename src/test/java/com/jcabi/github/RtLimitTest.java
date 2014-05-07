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
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.http.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtLimit}.
 *
 * @author Giang Le (giang@vn-smartsolutions.com)
 * @version $Id$
 */
public final class RtLimitTest {

    /**
     * RtLimit can describe as a JSON object.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void describeAsJson() throws Exception {
        final JsonReadable limit = new RtLimit(
            Mockito.mock(Github.class),
            new FakeRequest().withBody(this.body()),
            "core"
        );
        MatcherAssert.assertThat(
            limit.json().toString(),
            Matchers.equalTo(
                "{\"limit\":5000,\"remaining\":4999,\"reset\":1372700873}"
            )
        );
    }

    /**
     * RtLimit can throw exception when resource is absent.
     *
     * @throws Exception if some problem inside
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenResourceIsAbsent() throws Exception {
        final JsonReadable limit = new RtLimit(
            Mockito.mock(Github.class),
            new FakeRequest().withBody(this.body()),
            "absent"
        );
        MatcherAssert.assertThat(
            limit.json().toString(),
            Matchers.equalTo("{}")
        );
    }

    /**
     * Example response from rate API.
     * @return Body string.
     */
    private String body() {
        return new StringBuilder(Tv.HUNDRED)
            .append("{\"resources\":{\"core\":{\"limit\":5000,")
            .append("\"remaining\":4999,\"reset\":1372700873},")
            .append("\"search\":{\"limit\":20,\"remaining\":18,")
            .append("\"reset\":1372697452}},\"rate\":{\"limit\":5000,")
            .append("\"remaining\":4999,\"reset\":1372700873}}")
            .toString();
    }
}
