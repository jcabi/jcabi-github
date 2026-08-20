/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link RtGitignores}.
 * @see <a href="https://developer.github.com/v3/gitignore/">Gitignore API</a>
 * @since 0.8
 */
@Immutable
@OAuthScope(OAuthScope.Scope.REPO)
final class RtGitignoresITCase {

    @Test
    void iterateTemplateNames() throws IOException {
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            RtGitignoresITCase.gitignores().iterate(),
            Matchers.hasItem("C++")
        );
    }

    @Test
    void fetchesRawTemplateByName() throws IOException {
        MatcherAssert.assertThat(
            "String does not contain expected value",
            RtGitignoresITCase.gitignores().template("C"),
            Matchers.containsString("#")
        );
    }

    private static Gitignores gitignores() {
        return new RtGitignores(GitHubIT.connect());
    }
}
