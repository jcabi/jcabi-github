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
 * Test case for {@link RtUserOrganizations}.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
@OAuthScope(OAuthScope.Scope.READ_ORG)
public final class RtUserOrganizationsITCase {
    @Test
    public void iterateOrganizations() throws IOException {
        final UserOrganizations orgs = GitHubIT.connect()
            .users().get("yegor256")
            .organizations();
        MatcherAssert.assertThat(
            "Value is null",
            orgs.iterate().iterator().next(),
            Matchers.notNullValue()
        );
    }

}
