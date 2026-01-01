/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link RtMarkdown}.
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtMarkdownITCase {

    @Test
    void rendersMarkdown() throws IOException {
        final GitHub github = GitHubIT.connect();
        MatcherAssert.assertThat(
            "Values are not equal",
            github.markdown().render(
                Json.createObjectBuilder()
                    .add("text", "Hello, **world**!")
                    .build()
            ),
            Matchers.equalTo("<p>Hello, <strong>world</strong>!</p>\n")
        );
    }

    @Test
    void rendersRawMarkdown() throws IOException {
        final GitHub github = GitHubIT.connect();
        MatcherAssert.assertThat(
            "Values are not equal",
            github.markdown().raw(
                "Hey, **world**!"
            ),
            Matchers.equalTo("<p>Hey, <strong>world</strong>!</p>\n")
        );
    }

}
