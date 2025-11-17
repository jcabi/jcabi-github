/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link RtUser}.
 */
@OAuthScope(Scope.USER)
public final class RtUserITCase {

    /**
     * RtUser can understand who am I.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksWhoAmI() throws Exception {
        final Github github = new GithubIT().connect();
        final User self = github.users().self();
        MatcherAssert.assertThat(
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
            new GithubIT().connect().users().self().keys().toString(),
            Matchers.equalTo("https://api.github.com/user/keys")
        );
    }

}
