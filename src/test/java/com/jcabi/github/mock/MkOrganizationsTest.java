/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.GitHub;
import com.jcabi.github.Organizations;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * GitHub organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
public final class MkOrganizationsTest {
    /**
     * MkOrganizations can get specific organization.
     */
    @Test
    public void getSingleOrganization() throws IOException {
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
     */
    @Test
    public void testCreatedAt() throws IOException, ParseException, InterruptedException {
        final String name = "testCreatedAt";
        final MkOrganizations orgs = new MkOrganizations(
            new MkStorage.InFile()
        );
        final String created = "created_at";
        final Date early = new GitHub.Time(
            orgs.get(name)
                .json()
                .getString(created)
        ).date();
        TimeUnit.SECONDS.sleep(1L);
        final Date later = new GitHub.Time(
            orgs.get(name)
                .json()
                .getString(created)
        ).date();
        MatcherAssert.assertThat(later, Matchers.greaterThanOrEqualTo(early));
    }

    /**
     * MkOrganizations can list the logged-in user's organizations.
     */
    @Test
    public void iteratesCurrentUserOrganizations() throws IOException {
        final Organizations orgs = new MkGitHub().organizations();
        orgs.get("orgTestIterate");
        MatcherAssert.assertThat(
            orgs.iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
