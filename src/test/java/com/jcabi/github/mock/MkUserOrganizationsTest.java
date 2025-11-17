/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Github;
import com.jcabi.github.Organization;
import com.jcabi.github.UserOrganizations;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Github user organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class MkUserOrganizationsTest {
    /**
     * MkUserOrganizations can list user organizations.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesUserOrganizations() throws Exception {
        final String login = "orgTestIterate";
        final Github github = new MkGithub(login);
        final UserOrganizations userOrgs = github.users().get(login)
            .organizations();
        github.organizations().get(login);
        MatcherAssert.assertThat(
            userOrgs.iterate(),
            Matchers.<Organization>iterableWithSize(1)
        );
    }
}
