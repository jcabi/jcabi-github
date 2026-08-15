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
 * Test case for {@link RtUserOrganizations}.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
@OAuthScope(OAuthScope.Scope.READ_ORG)
final class RtUserOrganizationsITCase {

    @Test
    void iterateOrganizations() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            GitHubIT.connect()
                .users().get("yegor256")
                .organizations().iterate().iterator().next(),
            Matchers.notNullValue()
        );
    }
}
