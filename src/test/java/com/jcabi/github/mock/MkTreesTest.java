/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for MkTrees.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MkTreesTest {

    /**
     * MkTrees can create trees.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsMkTree() throws Exception {
        final JsonObject tree = Json.createObjectBuilder()
            .add("base_tree", "base_tree_sha")
            .add(
                "tree",
                Json.createArrayBuilder().add(
                    Json.createObjectBuilder()
                        .add("path", "dir/File.java")
                        .add("mode", "100644")
                        .add("type", "blob")
                        .add("sha", "sha-test")
                )
            ).build();
        MatcherAssert.assertThat(
            new MkGithub().randomRepo().git().trees().create(tree),
            Matchers.notNullValue()
        );
    }

    /**
     * MkTrees can get tree recursively.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void getTreeRec() throws Exception {
        final String sha = "0abcd89jcabitest";
        final JsonObject json = Json.createObjectBuilder().add(
            "tree",
            Json.createArrayBuilder().add(
                Json.createObjectBuilder()
                    .add("path", "test.txt")
                    .add("mode", "100644")
                    .add("sha", sha).add("name", "tree rec")
                    .add("type", "blob")
                    .add("content", "hello")
                    .build()
            ).build()
        ).build();
        final Repo repo = new MkGithub().randomRepo();
        repo.git().trees().create(json);
        MatcherAssert.assertThat(
            repo.git().trees().getRec(sha).json().getString("sha"),
            Matchers.containsString(sha)
        );
    }
}
