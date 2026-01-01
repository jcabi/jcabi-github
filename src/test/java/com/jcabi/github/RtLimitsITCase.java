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
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtLimitsITCase {

    @Test
    void checksRemainingRequests() throws IOException {
        final GitHub github = GitHubIT.connect();
        MatcherAssert.assertThat(
            "Value is null",
            new Limit.Smart(github.limits().get("core")).remaining(),
            Matchers.notNullValue()
        );
    }

}
