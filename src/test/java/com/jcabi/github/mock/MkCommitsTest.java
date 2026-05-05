/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Commit;
import com.jcabi.github.Commits;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Testcase for MkTags.
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
final class MkCommitsTest {

    @Test
    void createsMkCommit() throws IOException {
        final JsonObject author = Json.createObjectBuilder()
            .add("name", "Scott").add("email", "Scott@gmail.com")
            .add("date", "2008-07-09T16:13:30+12:00").build();
        final JsonArray tree = Json.createArrayBuilder()
            .add("xyzsha12").build();
        final Commit commit = new MkGitHub().randomRepo()
            .git().commits().create(
                Json.createObjectBuilder().add("message", "my commit message")
                    .add("sha", "12ahscba")
                    .add("tree", "abcsha12")
                    .add("parents", tree)
                    .add("author", author).build()
            );
        MatcherAssert.assertThat(
            "Value is null",
            commit,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            commit.sha(),
            Matchers.equalTo("12ahscba")
        );
    }

    /**
     * MkCommits.create() must persist the new commit's sha in storage so
     * that subsequent calls (e.g. json(), get()) can find the commit.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void persistsCreatedCommitSha() throws IOException {
        final String sha = "9e1f3c7b8d4a5e6f7c2d1b0a9e8f7d6c5b4a3210";
        final Commits commits = new MkGitHub().randomRepo().git().commits();
        commits.create(
            Json.createObjectBuilder()
                .add("message", "persisted commit message")
                .add("sha", sha)
                .add("tree", "treesha9e1f3c7b8d4a")
                .add(
                    "parents",
                    Json.createArrayBuilder().add("parentsha0123456789").build()
                )
                .add(
                    "author",
                    Json.createObjectBuilder()
                        .add("name", "Alice")
                        .add("email", "alice@example.com")
                        .add("date", "2024-01-15T12:30:00+00:00")
                        .build()
                )
                .build()
        );
        MatcherAssert.assertThat(
            "sha must be persisted in storage",
            commits.get(sha).json().getString("sha"),
            Matchers.equalTo(sha)
        );
    }

    /**
     * MkCommits.create() must persist the commit message in storage.
     * @throws IOException if there is any I/O problem
     */
    @Test
    void persistsCreatedCommitMessage() throws IOException {
        final String sha = "abcdef0123456789abcdef0123456789abcdef01";
        final String message = "another persisted message";
        final Commits commits = new MkGitHub().randomRepo().git().commits();
        commits.create(
            Json.createObjectBuilder()
                .add("message", message)
                .add("sha", sha)
                .add("tree", "treesha-abcdef-0123")
                .build()
        );
        MatcherAssert.assertThat(
            "message must be persisted in storage",
            commits.get(sha).json().getString("message"),
            Matchers.equalTo(message)
        );
    }
}
