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
 * Integration case for {@link RtLimits}.
 * @since 0.1
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtLimitsITCase {

    @Test
    void checksRemainingRequests() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new Limit.Smart(GitHubIT.connect().limits().get("core")).remaining(),
            Matchers.notNullValue()
        );
    }
}
