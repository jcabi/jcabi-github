/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.GitHub;
import com.jcabi.github.UserOrganizations;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * GitHub user organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
final class MkUserOrganizationsTest {
    @Test
    void iteratesUserOrganizations() throws IOException {
        final String login = "orgTestIterate";
        final GitHub github = new MkGitHub(login);
        final UserOrganizations orgs = github.users().get(login)
            .organizations();
        github.organizations().get(login);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            orgs.iterate(),
            Matchers.iterableWithSize(1)
        );
    }
}
