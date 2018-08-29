/**
 * Copyright (c) 2013-2018, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Organization}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class OrganizationTest {

    /**
     * Organization.Smart can fetch url from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesUrl() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", "https://api.github.com/orgs/github")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).url(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch avatar_url from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesAvatarUrl() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("avatar_url", "https://github.com/images/happy.gif")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).avatarUrl(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch name from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesName() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", "github")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).name(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch company from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesCompany() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("company", "GitHub")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).company(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch blog from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesBlog() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("blog", "https://github.com/blog")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).blog(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch location from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesLocation() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("location", "https://github.com/location")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).location(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch email from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesEmail() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("email", "octocat@github.com")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).email(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch billing_email from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesBillingEmail() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("billing_email", "octocat_billing@github.com")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).billingEmail(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch public_repos from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesPublicRepos() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("public_repos", 1)
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).publicRepos(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch public_gists from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesPublicGists() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("public_gists", 1)
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).publicGists(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch followers from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesFollowers() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("followers", 1)
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).followers(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch following from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesFollowing() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("following", 1)
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).following(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch html_url from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesHtmlUrl() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("html_url", "https://github.com/octocat")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).htmlUrl(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch created_at from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesCreatedAt() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("created_at", "2008-01-14T04:33:35Z")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).createdAt(),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization.Smart can fetch type from an Organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesType() throws Exception {
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("type", "Organization")
                .build()
        ).when(orgn).json();
        MatcherAssert.assertThat(
            new Organization.Smart(orgn).type(),
            Matchers.notNullValue()
        );
    }
}
