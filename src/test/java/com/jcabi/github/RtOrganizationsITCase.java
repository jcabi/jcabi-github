/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtOrganizations}.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
final class RtOrganizationsITCase {

    @Test
    void fetchesOrganization() {
        final String login = "github";
        MatcherAssert.assertThat(
            "Values are not equal",
            GitHubIT.connect()
                .organizations().get(login).login(),
            Matchers.equalTo(login)
        );
    }
}
