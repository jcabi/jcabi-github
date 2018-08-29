/**
 * Copyright (c) 2013-2018, jcabi.com
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
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtPullRef}.
 *
 * @author Chris Rebert (github@rebertia.com)
 * @version $Id$
 * @since 0.24
 */
public final class RtPullRefTest {
    /**
     * Test commit SHA.
     */
    private static final String SHA =
        "7a1f68e743e8a81e158136c8661011fb55abd703";
    /**
     * Test ref.
     */
    private static final String REF = "some-branch";

    /**
     * RtPullRef can fetch its repo.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesRepo() throws IOException {
        final Repo repo = new MkGithub().randomRepo();
        MatcherAssert.assertThat(
            RtPullRefTest.pullRef(repo).repo().coordinates(),
            Matchers.equalTo(repo.coordinates())
        );
    }

    /**
     * RtPullRef can fetch its ref.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesRef() throws IOException {
        MatcherAssert.assertThat(
            RtPullRefTest.pullRef().ref(),
            Matchers.equalTo(REF)
        );
    }

    /**
     * RtPullRef can fetch its commit SHA.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesSha() throws IOException {
        MatcherAssert.assertThat(
            RtPullRefTest.pullRef().sha(),
            Matchers.equalTo(SHA)
        );
    }

    /**
     * Returns an RtPullRef for testing.
     * @return Test RtPullRef
     * @throws IOException If there is an I/O problem.
     */
    private static PullRef pullRef() throws IOException {
        return RtPullRefTest.pullRef(new MkGithub().randomRepo());
    }

    /**
     * Returns an RtPullRef in the given repo for testing.
     * @param repo Repo to create the pull request ref in
     * @return Test RtPullRef
     */
    private static PullRef pullRef(final Repo repo) {
        final Coordinates coords = repo.coordinates();
        final JsonObject user = Json.createObjectBuilder()
            .add("login", coords.user())
            .build();
        return new RtPullRef(
            repo.github(),
            Json.createObjectBuilder()
                .add("ref", REF)
                .add("sha", SHA)
                .add("user", user)
                .add(
                    "repo",
                    Json.createObjectBuilder()
                        .add("owner", user)
                        .add("name", coords.repo())
                        .add("full_name", coords.toString())
                        .build()
                ).build()
        );
    }
}
