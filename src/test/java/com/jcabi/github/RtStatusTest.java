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
package com.jcabi.github;

import com.google.common.base.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtStatus}.
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
 */
public final class RtStatusTest {
    /**
     * RtStatus can convert itself to JSON.
     */
    @Test
    public void convertsToJson() {
        final String url = "http://status.jcabi-github.invalid/42";
        final String success = "Everything is awesome";
        final String pending = "Kragle is drying...";
        final String context = "jcabi/github/test";
        MatcherAssert.assertThat(
            new RtStatus(
                Status.State.Success,
                Optional.of(url),
                Optional.of(success),
                Optional.of(context)
            ).json().toString(),
            // @checkstyle LineLength (1 line)
            Matchers.equalTo("{\"state\":\"success\",\"target_url\":\"http://status.jcabi-github.invalid/42\",\"description\":\"Everything is awesome\",\"context\":\"jcabi/github/test\"}")
        );
        MatcherAssert.assertThat(
            new RtStatus(
                Status.State.Success,
                Optional.<String>absent(),
                Optional.of(success),
                Optional.of(context)
            ).json().toString(),
            // @checkstyle LineLength (1 line)
            Matchers.equalTo("{\"state\":\"success\",\"description\":\"Everything is awesome\",\"context\":\"jcabi/github/test\"}")
        );
        MatcherAssert.assertThat(
            new RtStatus(
                Status.State.Failure,
                Optional.of(url),
                Optional.<String>absent(),
                Optional.of(context)
            ).json().toString(),
            // @checkstyle LineLength (1 line)
            Matchers.equalTo("{\"state\":\"failure\",\"target_url\":\"http://status.jcabi-github.invalid/42\",\"context\":\"jcabi/github/test\"}")
        );
        MatcherAssert.assertThat(
            new RtStatus(
                Status.State.Pending,
                Optional.of(url),
                Optional.of(pending),
                Optional.<String>absent()
            ).json().toString(),
            // @checkstyle LineLength (1 line)
            Matchers.equalTo("{\"state\":\"pending\",\"target_url\":\"http://status.jcabi-github.invalid/42\",\"description\":\"Kragle is drying...\"}")
        );
    }
}
