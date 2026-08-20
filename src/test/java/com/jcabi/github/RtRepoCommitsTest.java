/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtRepoCommits}.
 * @since 0.1
 */
final class RtRepoCommitsTest {

    /**
     * RtRepoCommits can return commits' iterator.
     */
    @Test
    void returnIterator() {
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db51";
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtRepoCommits(
                new FakeRequest().withBody(
                    Json.createArrayBuilder().add(
                        Json.createObjectBuilder().add("sha", sha)
                    ).build().toString()
                ),
                RtRepoCommitsTest.repo()
            ).iterate(
                Collections.emptyMap()
            ).iterator().next().sha(),
            Matchers.equalTo(sha)
        );
    }

    @Test
    void fetchesCommit() {
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db52";
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtRepoCommits(
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add("sha", sha)
                        .build()
                        .toString()
                ),
                RtRepoCommitsTest.repo()
            ).get(sha).sha(),
            Matchers.equalTo(sha)
        );
    }

    @Test
    void comparesCommits() {
        MatcherAssert.assertThat(
            "Value is null",
            new RtRepoCommits(
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add("base_commit", Json.createObjectBuilder())
                        .add("commits", Json.createArrayBuilder())
                        .add("files", Json.createArrayBuilder())
                        .build().toString()
                ),
                RtRepoCommitsTest.repo()
            ).compare(
                "6dcb09b5b57875f334f61aebed695e2e4193db53",
                "6dcb09b5b57875f334f61aebed695e2e4193db54"
            ),
            Matchers.notNullValue(CommitsComparison.class)
        );
    }

    @Test
    void comparesCommitsDiffFormat() throws IOException {
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new RtRepoCommits(
                new FakeRequest().withBody("diff --git"),
                RtRepoCommitsTest.repo()
            ).diff(
                "6dcb09b5b57875f334f61aebed695e2e4193db55",
                "6dcb09b5b57875f334f61aebed695e2e4193db56"
            ),
            Matchers.startsWith("diff")
        );
    }

    @Test
    void comparesCommitsPatchFormat() throws IOException {
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new RtRepoCommits(
                new FakeRequest().withBody(
                    "From 6dcb09b5b57875f33"
                ),
                RtRepoCommitsTest.repo()
            ).patch(
                "6dcb09b5b57875f334f61aebed695e2e4193db57",
                "6dcb09b5b57875f334f61aebed695e2e4193db58"
            ),
            Matchers.startsWith("From")
        );
    }

    @Test
    void readCorrectUrl() {
        MatcherAssert.assertThat(
            "String does not end with expected value",
            new RtRepoCommits(new FakeRequest(), RtRepoCommitsTest.repo())
                .compare("base", "head").toString(),
            Matchers.endsWith(
                "/see-FakeRequest-class/repos/user/repo/compare/base...head"
            )
        );
    }

    private static Repo repo() {
        return new RtGitHub().repos()
            .get(new Coordinates.Simple("user", "repo"));
    }
}
