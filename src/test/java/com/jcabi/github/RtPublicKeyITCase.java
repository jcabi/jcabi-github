/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtPublicKey}.
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.READ_PUBLIC_KEY)
final class RtPublicKeyITCase {
    @Test
    void retrievesUri() {
        MatcherAssert.assertThat(
            "String does not end with expected value",
            GitHubIT.connect().users().self().keys().get(1).toString(),
            Matchers.endsWith("/keys/1")
        );
    }
}
