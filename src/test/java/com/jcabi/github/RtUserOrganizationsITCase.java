/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtUserOrganizations}.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
@OAuthScope(Scope.READ_ORG)
public final class RtUserOrganizationsITCase {
    /**
     * RtUserOrganizations can iterate all organizations of a user.
     * @throws Exception if any problem inside
     */
    @Test
    public void iterateOrganizations() throws Exception {
        final UserOrganizations orgs = new GithubIT().connect()
            .users().get("yegor256")
            .organizations();
        MatcherAssert.assertThat(
            orgs.iterate().iterator().next(),
            Matchers.notNullValue()
        );
    }

}
