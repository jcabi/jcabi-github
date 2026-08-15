/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtTrees}.
 * @since 0.1
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtTreesITCase {

    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * RepoRule.
     */
    private static RepoRule rule = new RepoRule();

    /**
     * Set up test fixtures.
     */
    @BeforeAll
    static void setUp() throws IOException {
        final GitHub github = GitHubIT.connect();
        RtTreesITCase.repos = github.repos();
        RtTreesITCase.repo = RtTreesITCase.rule.repo(RtTreesITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtTreesITCase.repos != null && RtTreesITCase.repo != null) {
            RtTreesITCase.repos.remove(RtTreesITCase.repo.coordinates());
        }
    }

    @Test
    void createsAndObtainsTree() throws IOException {
        final Trees trees = RtTreesITCase.repo.git().trees();
        final Tree tree = trees.create(
            Json.createObjectBuilder().add(
                "tree",
                Json.createArrayBuilder().add(
                    Json.createObjectBuilder()
                        .add("path", "test.txt")
                        .add("mode", "100644")
                        .add("type", "blob")
                        .add("content", "hello").build()
                ).build()
                ).build()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            trees.get(tree.json().getString("sha")),
            Matchers.is(tree)
        );
    }
}
