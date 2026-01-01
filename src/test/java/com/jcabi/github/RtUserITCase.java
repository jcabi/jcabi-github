/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link RtUser}.
 * @since 0.1
 */
@OAuthScope(OAuthScope.Scope.USER)
final class RtUserITCase {

    @Test
    void checksWhoAmI() throws IOException {
        final GitHub github = GitHubIT.connect();
        final User self = github.users().self();
        MatcherAssert.assertThat(
            "Values are not equal",
            self.login(),
            Matchers.not(Matchers.is(Matchers.emptyString()))
        );
    }

    @Test
    void readKeys() {
        MatcherAssert.assertThat(
            "Values are not equal",
            GitHubIT.connect().users().self().keys().toString(),
            Matchers.equalTo("https://api.github.com/user/keys")
        );
    }

}
