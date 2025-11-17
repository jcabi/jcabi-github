/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkMarkdown}.
 */
public class MkMarkdownTest {

    /**
     * MkMarkdown can be rendered.
     *
     */
    @Test
    public final void canBeRendered() throws IOException {
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
