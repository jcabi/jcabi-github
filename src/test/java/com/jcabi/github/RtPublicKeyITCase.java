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
    /**
     * RtPublicKey can retrieve correctly URI.
     */
    @Test
    public void retrievesURI() {
        MatcherAssert.assertThat(
            new GithubIT().connect().users().self().keys().get(1).toString(),
            Matchers.endsWith("/keys/1")
        );
    }
}
