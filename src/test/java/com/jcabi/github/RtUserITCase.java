/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link RtUser}.
 */
@OAuthScope(OAuthScope.Scope.USER)
public final class RtUserITCase {

    /**
     * RtUser can understand who am I.
     */
    @Test
    public void checksWhoAmI() throws IOException {
        final GitHub github = new GitHubIT().connect();
        final User self = github.users().self();
        MatcherAssert.assertThat(
            "Values are not equal",
            self.login(),
            Matchers.not(Matchers.is(Matchers.emptyString()))
        );
    }

    /**
     * RtUser can read verified public keys.
     */
    @Test
    public void readKeys() {
        MatcherAssert.assertThat(
            "Values are not equal",
            new GitHubIT().connect().users().self().keys().toString(),
            Matchers.equalTo("https://api.github.com/user/keys")
        );
    }

}
