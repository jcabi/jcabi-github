/**
 * Copyright (c) 2013-2020, jcabi.com
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

import com.jcabi.github.Limit.Throttled;
import com.jcabi.http.request.FakeRequest;
import java.util.Date;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Limit}.
 *
 * @author Tomas Colombo (tomas.colombo@rollasolution.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
final class LimitTest {

    /**
     * Limit can throw exception when resource is absent.
     * @throws Exception if some problem inside
     */
    @Test
    void throwsWhenResourceIsAbsent() throws Exception {
        final Limit limit = Mockito.mock(Limit.class);
        final Throttled throttled = new Throttled(limit, 23);
        Mockito.when(limit.json()).thenReturn(
            Json.createObjectBuilder().add("absent", "absentValue").build()
        );
        Assertions.assertThrows(
            IllegalStateException.class,
            throttled::json
        );
    }

    /**
     * Limit reset() method properly converts time.
     * GitHub reset property is in seconds, but java.util.Date
     * constructor assumes miliseconds.
     *
     * @throws Exception if some problem inside
     */
    @Test
    void timeIsCreatedForReset() throws Exception {
        // @checkstyle MagicNumberCheck (21 lines)
        final RtLimit limit = new RtLimit(
            Mockito.mock(Github.class),
            new FakeRequest().withBody(
                Json.createObjectBuilder().add(
                    "rate", Json.createObjectBuilder()
                        .add("limit", 5000)
                        .add("remaining", 4999)
                        .add("reset", new Integer(1372700873))
                        .build()
                ).add(
                    "resources", Json.createObjectBuilder().add(
                        "core", Json.createObjectBuilder()
                            .add("limit", 5000)
                            .add("remaining", 4999)
                            .add("reset", new Integer(1372700873))
                            .build()
                    ).add(
                        "search", Json.createObjectBuilder()
                            .add("limit", 5000)
                            .add("remaining", 4999)
                            .add("reset", new Integer(1372700873))
                            .build()
                    ).build()
                ).build().toString()
            ),
            "core"
        );
        final RtLimit.Smart smart = new RtLimit.Smart(limit);
        // @checkstyle MagicNumberCheck (3 lines)
        MatcherAssert.assertThat(
            smart.reset(),
            Matchers.equalTo(new Date(new Long(1372700873000L)))
        );
    }

}
