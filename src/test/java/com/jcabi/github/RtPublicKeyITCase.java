/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtPublicKey}.
 *
 */
@OAuthScope(OAuthScope.Scope.READ_PUBLIC_KEY)
public final class RtPublicKeyITCase {
    @Test
    public void retrievesUri() {
        MatcherAssert.assertThat(
            "String does not end with expected value",
            new GitHubIT().connect().users().self().keys().get(1).toString(),
            Matchers.endsWith("/keys/1")
        );
    }
}
