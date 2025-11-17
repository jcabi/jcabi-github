/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import jakarta.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link RtMarkdown}.
 */
@OAuthScope(Scope.REPO)
public final class RtMarkdownITCase {

    /**
     * RtMarkdown can render markdown.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersMarkdown() throws Exception {
        final Github github = new GithubIT().connect();
        MatcherAssert.assertThat(
            github.markdown().render(
                Json.createObjectBuilder()
                    .add("text", "Hello, **world**!")
                    .build()
            ),
            Matchers.equalTo("<p>Hello, <strong>world</strong>!</p>\n")
        );
    }

    /**
     * RtMarkdown can render raw markdown.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersRawMarkdown() throws Exception {
        final Github github = new GithubIT().connect();
        MatcherAssert.assertThat(
            github.markdown().raw(
                "Hey, **world**!"
            ),
            Matchers.equalTo("<p>Hey, <strong>world</strong>!</p>\n")
        );
    }

}
