/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link CommitsComparison}.
 * @since 0.8
 * @checkstyle MultipleStringLiterals (75 lines)
 */
public final class CommitsComparisonTest {

    @Test
    public void fetchesCommits() throws IOException {
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
            "Values are not equal",
            comparison.commits().iterator().next().sha(),
            Matchers.equalTo(sha)
        );
    }

    @Test
    public void fetchesFiles() throws IOException {
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
            "Values are not equal",
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
        return new RtGitHub().repos()
            .get(new Coordinates.Simple("user", "repo"));
    }

}
