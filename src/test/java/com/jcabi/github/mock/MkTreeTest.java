/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import com.jcabi.github.Tree;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for MkTree.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkTreeTest {

    /**
     * MkTree should return its json.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void fetchesContent() throws Exception {
        MatcherAssert.assertThat(
            this.tree().json().getString("message"),
            Matchers.is("\"test tree\"")
        );
    }

    /**
     * Return a Tree for testing.
     * @return Tree
     * @throws Exception If something goes wrong.
     */
    private Tree tree() throws Exception {
        final JsonObject json = Json.createObjectBuilder().add(
            "tree",
            Json.createArrayBuilder().add(
                Json.createObjectBuilder()
                    .add("sha", "abcsha12").add("message", "test tree")
                    .add("name", "v.0.1").build()
            )
        ).build();
        return new MkGithub().randomRepo().git().trees().create(json);
    }

}
