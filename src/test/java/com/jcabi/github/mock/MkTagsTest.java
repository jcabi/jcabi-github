/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Testcase for MkTags.
 * @since 0.6
 */
final class MkTagsTest {

    @Test
    void createsMkTag() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new MkGitHub().randomRepo().git().tags().create(
                Json.createObjectBuilder().add("name", "v.0.1")
                    .add("message", "test tag").add("sha", "abcsha12").add(
                        "tagger",
                        Json.createObjectBuilder()
                            .add("name", "Scott").add("email", "Scott@gmail.com").build()
                    ).build()
            ),
            Matchers.notNullValue()
        );
    }
}
