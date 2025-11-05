/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import javax.json.Json;
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
     * @throws Exception if some problem inside
     */
    @Test
    public final void canBeRendered() throws Exception {
        final String text = "Hello, **world**!";
        MatcherAssert.assertThat(
            new MkGithub().markdown().render(
                Json.createObjectBuilder()
                    .add("text", text)
                    .build()
            ),
            Matchers.equalTo(text)
        );
    }
}
