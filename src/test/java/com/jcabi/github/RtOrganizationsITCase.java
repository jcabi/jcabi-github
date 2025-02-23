/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtOrganizations}.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
public final class RtOrganizationsITCase {
    /**
     * RtOrganizations can get an organization.
     */
    @Test
    public void getOrganization() {
        final String login = "github";
        final Organization org = new GithubIT().connect()
            .organizations().get(login);
        MatcherAssert.assertThat(org.login(), Matchers.equalTo(login));
    }

}
