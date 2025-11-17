/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link RtTrees}.
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
@OAuthScope(Scope.REPO)
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
     * @throws Exception If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final Github github = new GithubIT().connect();
        repos = github.repos();
        repo = rule.repo(repos);
    }

    /**
     * Tear down test fixtures.
     * @throws Exception If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if (repos != null && repo != null) {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtTags creates a tag.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsAndObtainsTree() throws Exception {
        final Trees trees = repo.git().trees();
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
            trees.get(tree.json().getString("sha")),
            Matchers.is(tree)
        );
    }
}
