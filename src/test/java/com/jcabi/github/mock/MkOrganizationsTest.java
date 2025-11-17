/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Github;
import com.jcabi.github.Organizations;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Github organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
public final class MkOrganizationsTest {
    /**
     * MkOrganizations can get specific organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void getSingleOrganization() throws Exception {
        final String login = "orgTestGet";
        final MkOrganizations orgs = new MkOrganizations(
            new MkStorage.InFile()
        );
        MatcherAssert.assertThat(
            orgs.get(login),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            orgs.get(login).json().getString("login"),
            Matchers.equalTo(login)
        );
    }

    /**
     * Organization created_at field should be variable.
     * @throws Exception If some problem inside
     */
    @Test
    public void testCreatedAt() throws Exception {
        final String name = "testCreatedAt";
        final MkOrganizations orgs = new MkOrganizations(
            new MkStorage.InFile()
        );
        final String created = "created_at";
        final Date early = new Github.Time(
            orgs.get(name)
                .json()
                .getString(created)
        ).date();
        TimeUnit.SECONDS.sleep(1L);
        final Date later = new Github.Time(
            orgs.get(name)
                .json()
                .getString(created)
        ).date();
        MatcherAssert.assertThat(later, Matchers.greaterThanOrEqualTo(early));
    }

    /**
     * MkOrganizations can list the logged-in user's organizations.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesCurrentUserOrganizations() throws Exception {
        final Organizations orgs = new MkGithub().organizations();
        orgs.get("orgTestIterate");
        MatcherAssert.assertThat(
            orgs.iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
