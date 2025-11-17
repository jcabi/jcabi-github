/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import com.jcabi.github.Commit;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for MkTags.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class MkCommitsTest {

    /**
     * MkCommits can create commits.
     */
    @Test
    public final void createsMkCommit() throws IOException {
        final JsonObject author = Json.createObjectBuilder()
            .add("name", "Scott").add("email", "Scott@gmail.com")
            .add("date", "2008-07-09T16:13:30+12:00").build();
        final JsonArray tree = Json.createArrayBuilder()
            .add("xyzsha12").build();
        final Commit newCommit = new MkGitHub().randomRepo()
            .git().commits().create(
                Json.createObjectBuilder().add("message", "my commit message")
                    .add("sha", "12ahscba")
                    .add("tree", "abcsha12")
                    .add("parents", tree)
                    .add("author", author).build()
            );
        MatcherAssert.assertThat(
            newCommit,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            newCommit.sha(),
            Matchers.equalTo("12ahscba")
        );
    }
}
