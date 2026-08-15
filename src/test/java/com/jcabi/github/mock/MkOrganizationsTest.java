/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.GitHub;
import com.jcabi.github.Organizations;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * GitHub organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
final class MkOrganizationsTest {

    @Test
    void fetchesSingleOrganization() throws IOException {
        MatcherAssert.assertThat(
            "Organization is absent",
            new MkOrganizations(new MkStorage.InFile()).get("orgTestGet"),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesLoginOfOrganization() throws IOException {
        final String login = "orgTestGet";
        MatcherAssert.assertThat(
            "Organization has a wrong login",
            new MkOrganizations(new MkStorage.InFile())
                .get(login).json().getString("login"),
            Matchers.equalTo(login)
        );
    }

    @Test
    void fetchesCreatedAt() throws IOException, InterruptedException {
        final String name = "testCreatedAt";
        final MkOrganizations orgs = new MkOrganizations(
            new MkStorage.InFile()
        );
        final String created = "created_at";
        final Instant early = new GitHub.Time(
            orgs.get(name)
                .json()
                .getString(created)
        ).date();
        TimeUnit.SECONDS.sleep(1L);
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            new GitHub.Time(
                orgs.get(name)
                    .json()
                    .getString(created)
            ).date(),
            Matchers.greaterThanOrEqualTo(early)
        );
    }

    /**
     * MkOrganizations can list the logged-in user's organizations.
     */
    @Test
    void iteratesCurrentUserOrganizations() throws IOException {
        final Organizations orgs = new MkGitHub().organizations();
        orgs.get("orgTestIterate");
        MatcherAssert.assertThat(
            "Collection is not empty",
            orgs.iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
