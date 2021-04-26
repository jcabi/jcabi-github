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

import com.jcabi.http.request.FakeRequest;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CommitsComparison}.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (75 lines)
 */
final class CommitsComparisonTest {

    /**
     * CommitsComparison.Smart can fetch commits.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesCommits() throws Exception {
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db50";
        final CommitsComparison.Smart comparison = new CommitsComparison.Smart(
            new RtCommitsComparison(
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add("base_commit", Json.createObjectBuilder())
                        .add(
                            "commits",
                            Json.createArrayBuilder()
                                .add(Json.createObjectBuilder().add("sha", sha))
                        )
                        .add("files", Json.createArrayBuilder())
                        .build().toString()
                ),
                CommitsComparisonTest.repo(),
                "6dcb09b5b57875f334f61aebed695e2e4193db51",
                "6dcb09b5b57875f334f61aebed695e2e4193db52"
            )
        );
        MatcherAssert.assertThat(
            comparison.commits().iterator().next().sha(), Matchers.equalTo(sha)
        );
    }

    /**
     * CommitsComparison.Smart can fetch files.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesFiles() throws Exception {
        final String filename = "file.txt";
        final CommitsComparison.Smart comparison = new CommitsComparison.Smart(
            new RtCommitsComparison(
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add("base_commit", Json.createObjectBuilder())
                        .add("commits", Json.createArrayBuilder())
                        .add(
                            "files",
                            Json.createArrayBuilder().add(
                                Json.createObjectBuilder()
                                    .add("filename", filename)
                            )
                        )
                        .build().toString()
                ),
                CommitsComparisonTest.repo(),
                "6dcb09b5b57875f334f61aebed695e2e4193db53",
                "6dcb09b5b57875f334f61aebed695e2e4193db54"
            )
        );
        MatcherAssert.assertThat(
            new FileChange.Smart(
                comparison.files().iterator().next()
            ).filename(),
            Matchers.equalTo(filename)
        );
    }

    /**
     * Return repo for tests.
     * @return Repository
     */
    private static Repo repo() {
        return new RtGithub().repos()
            .get(new Coordinates.Simple("user", "repo"));
    }

}
