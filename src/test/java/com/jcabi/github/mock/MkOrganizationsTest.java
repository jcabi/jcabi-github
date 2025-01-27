/**
 * Copyright (c) 2013-2025, jcabi.com
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
