/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Organization}.
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class OrganizationTest {

    @Test
    void fetchesUrl() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", "https://api.github.com/orgs/github")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).url(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesAvatarUrl() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("avatar_url", "https://github.com/images/happy.gif")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).avatarUrl(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesName() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", "github")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).name(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesCompany() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("company", "GitHub")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).company(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesBlog() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("blog", "https://github.com/blog")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).blog(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesLocation() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("location", "https://github.com/location")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).location(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesEmail() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("email", "octocat@github.com")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).email(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesBillingEmail() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("billing_email", "octocat_billing@github.com")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).billingEmail(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesPublicRepos() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("public_repos", 1)
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).publicRepos(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesPublicGists() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("public_gists", 1)
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).publicGists(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesFollowers() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("followers", 1)
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).followers(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesFollowing() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("following", 1)
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).following(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesHtmlUrl() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("html_url", "https://github.com/octocat")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).htmlUrl(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesCreatedAt() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("created_at", "2008-01-14T04:33:35Z")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).createdAt(),
            Matchers.notNullValue()
        );
    }

    @Test
    void fetchesType() throws IOException {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("type", "Organization")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            "Value is null",
            new Organization.Smart(orgn).type(),
            Matchers.notNullValue()
        );
    }
}
