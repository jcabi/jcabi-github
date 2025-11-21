/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Testcase for MkTrees.
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MkTreesTest {

    @Test
    public void createsMkTree() throws IOException {
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
            "Value is null",
            new MkGitHub().randomRepo().git().trees().create(tree),
            Matchers.notNullValue()
        );
    }

    @Test
    public void getTreeRec() throws IOException {
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
        final Repo repo = new MkGitHub().randomRepo();
        repo.git().trees().create(json);
        MatcherAssert.assertThat(
            "String does not contain expected value",
            repo.git().trees().getRec(sha).json().getString("sha"),
            Matchers.containsString(sha)
        );
    }
}
