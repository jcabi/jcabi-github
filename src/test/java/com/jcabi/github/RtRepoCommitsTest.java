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

import com.rexsl.test.request.FakeRequest;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link RtRepoCommits}.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
public final class RtRepoCommitsTest {

    /**
     * RtRepoCommits can return commits' iterator.
     */
    @Test
    public void returnIterator() {
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db51";
        final RepoCommits commits = new RtRepoCommits(
            new FakeRequest().withBody(
                Json.createArrayBuilder().add(
                    // @checkstyle MultipleStringLiterals (1 line)
                    Json.createObjectBuilder().add("sha", sha)
                ).build().toString()
            ),
            new Coordinates.Simple("testuser1", "testrepo1")
        );
        MatcherAssert.assertThat(
            commits.iterate().iterator().next().sha(),
            Matchers.equalTo(sha)
        );
    }

    /**
     * RtRepoCommits can get commit.
     */
    @Test
    public void getCommit() {
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db52";
        final RepoCommits commits = new RtRepoCommits(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("sha", sha)
                    .build()
                    .toString()
            ),
            new Coordinates.Simple("testuser2", "testrepo2")
        );
        MatcherAssert.assertThat(commits.get(sha).sha(), Matchers.equalTo(sha));
    }
}
