/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Test case for {@link RtTrees}.
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtTreesITCase {

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
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    private static RepoRule rule = new RepoRule();

    /**
     * Set up test fixtures.
     */
    @BeforeAll
    public static void setUp() throws IOException {
        final GitHub github = new GitHubIT().connect();
        RtTreesITCase.repos = github.repos();
        RtTreesITCase.repo = RtTreesITCase.rule.repo(RtTreesITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    public static void tearDown() throws IOException {
        if (RtTreesITCase.repos != null && RtTreesITCase.repo != null) {
            RtTreesITCase.repos.remove(RtTreesITCase.repo.coordinates());
        }
    }

    @Test
    public void createsAndObtainsTree() throws IOException {
        final Trees trees = RtTreesITCase.repo.git().trees();
        final JsonObject json = Json.createObjectBuilder().add(
            "tree",
            Json.createArrayBuilder().add(
                Json.createObjectBuilder()
                    .add("path", "test.txt")
                    .add("mode", "100644")
                    .add("type", "blob")
                    .add("content", "hello").build()
            ).build()
        ).build();
        final Tree tree = trees.create(json);
        MatcherAssert.assertThat(
            "Values are not equal",
            trees.get(tree.json().getString("sha")),
            Matchers.is(tree)
        );
    }
}
