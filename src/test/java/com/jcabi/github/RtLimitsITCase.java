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
 * Integration case for {@link RtLimits}.
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtLimitsITCase {

    /**
     * RtLimits can check remaining requests.
     */
    @Test
    public void checksRemainingRequests() throws IOException {
        final GitHub github = new GitHubIT().connect();
        MatcherAssert.assertThat(
            "Value is null",
            new Limit.Smart(github.limits().get("core")).remaining(),
            Matchers.notNullValue()
        );
    }

}
