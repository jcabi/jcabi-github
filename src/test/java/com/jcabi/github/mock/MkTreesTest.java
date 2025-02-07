/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import javax.json.Json;
import javax.json.JsonObject;
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
