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
 * Test case for {@link MkMarkdown}.
 * @since 0.1
 */
final class MkMarkdownTest {

    @Test
    void canBeRendered() throws IOException {
        final String text = "Hello, **world**!";
        MatcherAssert.assertThat(
            "Values are not equal",
            new MkGitHub().markdown().render(
                Json.createObjectBuilder()
                    .add("text", text)
                    .build()
            ),
            Matchers.equalTo(text)
        );
    }
}
