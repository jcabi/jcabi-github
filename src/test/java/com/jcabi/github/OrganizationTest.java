/**
 * Copyright (c) 2012-2013, JCabi.com
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
public final class OrganizationTest {

    /**
     * Organization.Smart can fetch key properties of an Issue.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesProperties() throws Exception {
        final int defaultint = 21;
        final Organization orgn = Mockito.mock(Organization.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", "https://api.github.com/orgs/github")
                .add("avatar_url", "https://github.com/images/happy.gif")
                .add("name", "github")
                .add("company", "GitHub")
                .add("blog", "https://github.com/blog")
                .add("location", "San Francisco")
                .add("email", "octocat@github.com")
                .add("public_repos", defaultint)
                .add("public_gists", defaultint)
                .add("followers", defaultint)
                .add("following", defaultint)
                .add("html_url", "https://github.com/octocat")
                .add("created_at", "2008-01-14T04:33:35Z")
                .add("type", "Organization")
                .build()
        ).when(orgn).json();
        final Organization.Smart smart = new Organization.Smart(orgn);
        MatcherAssert.assertThat(
            smart.url(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.avatarUrl(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.name(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.company(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.blog(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.location(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.email(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.publicRepos(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.publicGists(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.following(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.htmlUrl(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            smart.type(),
            Matchers.notNullValue()
        );
    }
}
