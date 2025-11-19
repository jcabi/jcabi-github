/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link PullRef}.
 * @since 0.24
 */
public final class PullRefTest {
    /**
     * Test ref.
     */
    private static final String REF = "the-ref";

    /**
     * Test commit SHA.
     * @checkstyle LineLength (2 lines)
     */
    private static final String SHA = "7a1f68e743e8a81e158136c8661011fb55abd703";

    /**
     * Test pull request ref label.
     */
    private static final String LABEL = "pr-ref-label";

    /**
     * PullRef.Smart can fetch its repo.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesRepo() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Values are not equal",
            PullRefTest.pullRef(repo).repo().coordinates(),
            Matchers.equalTo(repo.coordinates())
        );
    }

    /**
     * PullRef.Smart can fetch its ref name.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesRef() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            PullRefTest.pullRef().ref(),
            Matchers.equalTo(PullRefTest.REF)
        );
    }

    /**
     * PullRef.Smart can fetch its commit sha.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesSha() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            PullRefTest.pullRef().sha(),
            Matchers.equalTo(PullRefTest.SHA)
        );
    }

    /**
     * PullRef.Smart can fetch its label.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesLabel() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            PullRefTest.pullRef().label(),
            Matchers.equalTo(PullRefTest.LABEL)
        );
    }

    /**
     * PullRef.Smart can fetch its commit.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesCommit() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Commit commit = PullRefTest.pullRef(repo).commit();
        MatcherAssert.assertThat(
            "Values are not equal",
            commit.repo().coordinates(),
            Matchers.equalTo(repo.coordinates())
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            commit.sha(),
            Matchers.equalTo(PullRefTest.SHA)
        );
    }

    /**
     * PullRef.Smart can fetch its user.
     * @throws IOException If there is an I/O problem.
     */
    @Test
    public void fetchesUser() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MatcherAssert.assertThat(
            "Values are not equal",
            PullRefTest.pullRef(repo).user().login(),
            Matchers.equalTo(repo.coordinates().user())
        );
    }

    /**
     * Returns a smart pull request ref in the given repo for testing.
     * @param repo Repo to create the pull request ref in
     * @return PullRef.Smart
     */
    private static PullRef.Smart pullRef(final Repo repo) {
        final Coordinates coords = repo.coordinates();
        final JsonObject user = Json.createObjectBuilder()
            .add("login", coords.user())
            .build();
        return new PullRef.Smart(
            new RtPullRef(
                repo.github(),
                Json.createObjectBuilder()
                    .add("label", PullRefTest.LABEL)
                    .add("ref", PullRefTest.REF)
                    .add("sha", PullRefTest.SHA)
                    .add("user", user)
                    .add(
                        "repo",
                        Json.createObjectBuilder()
                            .add("name", coords.repo())
                            .add("full_name", coords.toString())
                            .add("owner", user)
                            .build()
                    ).build()
            )
        );
    }

    /**
     * Returns a smart pull request ref for testing.
     * @return PullRef.Smart
     * @throws IOException If there is an I/O problem.
     */
    private static PullRef.Smart pullRef() throws IOException {
        return PullRefTest.pullRef(new MkGitHub().randomRepo());
    }
}
