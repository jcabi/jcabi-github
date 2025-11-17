/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Organization}.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class OrganizationTest {

    /**
     * Organization.Smart can fetch url from an Organization.
     */
    @Test
    public void fetchesUrl() throws IOException {
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

    /**
     * Organization.Smart can fetch avatar_url from an Organization.
     */
    @Test
    public void fetchesAvatarUrl() throws IOException {
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

    /**
     * Organization.Smart can fetch name from an Organization.
     */
    @Test
    public void fetchesName() throws IOException {
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

    /**
     * Organization.Smart can fetch company from an Organization.
     */
    @Test
    public void fetchesCompany() throws IOException {
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

    /**
     * Organization.Smart can fetch blog from an Organization.
     */
    @Test
    public void fetchesBlog() throws IOException {
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

    /**
     * Organization.Smart can fetch location from an Organization.
     */
    @Test
    public void fetchesLocation() throws IOException {
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

    /**
     * Organization.Smart can fetch email from an Organization.
     */
    @Test
    public void fetchesEmail() throws IOException {
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

    /**
     * Organization.Smart can fetch billing_email from an Organization.
     */
    @Test
    public void fetchesBillingEmail() throws IOException {
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

    /**
     * Organization.Smart can fetch public_repos from an Organization.
     */
    @Test
    public void fetchesPublicRepos() throws IOException {
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

    /**
     * Organization.Smart can fetch public_gists from an Organization.
     */
    @Test
    public void fetchesPublicGists() throws IOException {
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

    /**
     * Organization.Smart can fetch followers from an Organization.
     */
    @Test
    public void fetchesFollowers() throws IOException {
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

    /**
     * Organization.Smart can fetch following from an Organization.
     */
    @Test
    public void fetchesFollowing() throws IOException {
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

    /**
     * Organization.Smart can fetch html_url from an Organization.
     */
    @Test
    public void fetchesHtmlUrl() throws IOException {
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

    /**
     * Organization.Smart can fetch created_at from an Organization.
     */
    @Test
    public void fetchesCreatedAt() throws IOException {
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

    /**
     * Organization.Smart can fetch type from an Organization.
     */
    @Test
    public void fetchesType() throws IOException {
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
