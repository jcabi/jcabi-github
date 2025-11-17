/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtPullRef}.
 *
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
            Matchers.equalTo(RtPullRefTest.REF)
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
            Matchers.equalTo(RtPullRefTest.SHA)
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
                .add("ref", RtPullRefTest.REF)
                .add("sha", RtPullRefTest.SHA)
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
