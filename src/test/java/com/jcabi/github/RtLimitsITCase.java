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
 * Integration case for {@link RtLimits}.
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(Scope.REPO)
public final class RtLimitsITCase {

    /**
     * RtLimits can check remaining requests.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksRemainingRequests() throws Exception {
        final Github github = new GithubIT().connect();
        MatcherAssert.assertThat(
            new Limit.Smart(github.limits().get("core")).remaining(),
            Matchers.notNullValue()
        );
    }

}
