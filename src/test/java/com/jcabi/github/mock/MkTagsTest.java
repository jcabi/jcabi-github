/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Testcase for MkTags.
 * @since 0.6
 * @checkstyle MultipleStringLiterals (500 lines)
 */
final class MkTagsTest {

    @Test
    void createsMkTag() throws IOException {
        final JsonObject tagger = Json.createObjectBuilder()
            .add("name", "Scott").add("email", "Scott@gmail.com").build();
        MatcherAssert.assertThat(
            "Value is null",
            new MkGitHub().randomRepo().git().tags().create(
                Json.createObjectBuilder().add("name", "v.0.1")
                    .add("message", "test tag").add("sha", "abcsha12")
                    .add("tagger", tagger).build()
            ),
            Matchers.notNullValue()
        );
    }
}
