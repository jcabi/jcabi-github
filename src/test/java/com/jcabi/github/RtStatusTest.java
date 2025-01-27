/**
 * Copyright (c) 2013-2025, jcabi.com
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

import com.jcabi.github.mock.MkGithub;
import java.io.IOException;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtStatus}.
 * @since 0.24
 */
public final class RtStatusTest {
    /**
     * RtStatus can fetch its ID number.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesId() throws IOException {
        final int ident = 666;
        MatcherAssert.assertThat(
            new RtStatus(
                RtStatusTest.commit(),
                Json.createObjectBuilder().add("id", ident).build()
            ).identifier(),
            Matchers.equalTo(ident)
        );
    }

    /**
     * RtStatus can fetch its URL.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesUrl() throws IOException {
        final String url = "http://api.jcabi-github.invalid/whatever";
        MatcherAssert.assertThat(
            new RtStatus(
                RtStatusTest.commit(),
                Json.createObjectBuilder().add("url", url).build()
            ).url(),
            Matchers.equalTo(url)
        );
    }

    /**
     * RtStatus can fetch its associated commit.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesCommit() throws IOException {
        final Commit cmmt = RtStatusTest.commit();
        MatcherAssert.assertThat(
            new RtStatus(cmmt, Json.createObjectBuilder().build()).commit(),
            Matchers.equalTo(cmmt)
        );
    }

    /**
     * Returns a test commit to work with.
     * @return Commit
     * @throws IOException If there is an I/O problem.
     */
    private static Commit commit() throws IOException {
        return new MkGithub().randomRepo().git().commits()
            .get("d288364af5028c72e2a2c91c29343bae11fffcbe");
    }
}
