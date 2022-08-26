/**
 * Copyright (c) 2013-2022, jcabi.com
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

import com.jcabi.http.request.FakeRequest;
import java.util.Collections;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
            RtRepoCommitsTest.repo()
        );
        MatcherAssert.assertThat(
            commits.iterate(
                Collections.<String, String>emptyMap()
            ).iterator().next().sha(),
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
            RtRepoCommitsTest.repo()
        );
        MatcherAssert.assertThat(commits.get(sha).sha(), Matchers.equalTo(sha));
    }

    /**
     * RtRepoCommits can compare two commits.
     */
    @Test
    public void comparesCommits() {
        final RepoCommits commits = new RtRepoCommits(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("base_commit", Json.createObjectBuilder())
                    .add("commits", Json.createArrayBuilder())
                    .add("files", Json.createArrayBuilder())
                    .build().toString()
            ),
            RtRepoCommitsTest.repo()
        );
        MatcherAssert.assertThat(
            commits.compare(
                "6dcb09b5b57875f334f61aebed695e2e4193db53",
                "6dcb09b5b57875f334f61aebed695e2e4193db54"
            ),
            Matchers.notNullValue(CommitsComparison.class)
        );
    }

    /**
     * RtRepoCommits can compare two commits and present result in diff format.
     * @throws Exception If some problem inside
     */
    @Test
    public void comparesCommitsDiffFormat() throws Exception {
        final RepoCommits commits = new RtRepoCommits(
            new FakeRequest().withBody("diff --git"),
            RtRepoCommitsTest.repo()
        );
        MatcherAssert.assertThat(
            commits.diff(
                "6dcb09b5b57875f334f61aebed695e2e4193db55",
                "6dcb09b5b57875f334f61aebed695e2e4193db56"
            ),
            Matchers.startsWith("diff")
        );
    }

    /**
     * RtRepoCommits can compare two commits and present result in patch format.
     * @throws Exception If some problem inside
     */
    @Test
    public void comparesCommitsPatchFormat() throws Exception {
        final RepoCommits commits = new RtRepoCommits(
            new FakeRequest().withBody(
                "From 6dcb09b5b57875f33"
            ),
            RtRepoCommitsTest.repo()
        );
        MatcherAssert.assertThat(
            commits.patch(
                "6dcb09b5b57875f334f61aebed695e2e4193db57",
                "6dcb09b5b57875f334f61aebed695e2e4193db58"
            ),
            Matchers.startsWith("From")
        );
    }

    /**
     * RtRepoCommits can read correctly URL.
     */
    @Test
    public void readCorrectURL() {
        MatcherAssert.assertThat(
            new RtRepoCommits(new FakeRequest(), repo())
                .compare("base", "head").toString(),
            Matchers.endsWith(
                "/see-FakeRequest-class/repos/user/repo/compare/base...head"
            )
        );
    }

    /**
     * Create repository for tests.
     * @return Repository
     */
    private static Repo repo() {
        return new RtGithub().repos()
            .get(new Coordinates.Simple("user", "repo"));
    }

}
