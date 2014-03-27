/**
 * Copyright (c) 2013-2014, JCabi.com
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
import com.jcabi.github.Organization;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Github organizations.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.7
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class MkOrganizationsTest {

    /**
     * MkOrganizations can list organizations.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesOrganizations() throws Exception {
        final MkOrganizations orgs = new MkOrganizations(
            new MkStorage.InFile(),
            "orgTestIterate"
        );
        orgs.get("orgTestIterate");
        MatcherAssert.assertThat(
            orgs.iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * MkOrganizations can list user organizations.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesUserOrganizations() throws Exception {
        final String login = "orgTestIterate";
        final MkOrganizations orgs = new MkOrganizations(
            new MkStorage.InFile(),
            login
        );
        final Organization org = orgs.get(login);
        MatcherAssert.assertThat(
            orgs.iterate(org.login()),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * MkOrganizations can get specific organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void getSingleOrganization() throws Exception {
        final MkOrganizations orgs = new MkOrganizations(
            new MkStorage.InFile(),
            "orgTestGet"
        );
        MatcherAssert.assertThat(
            orgs.get("orgTestGet"),
            Matchers.notNullValue()
        );
    }

    /**
     * Organization created_at field should be variable.
     * @throws Exception If some problem inside
     */
    @Test
    public void testCreatedAt() throws Exception {
        final MkOrganizations orgs = new MkOrganizations(
            new MkStorage.InFile(), "testCreatedAt"
        );
        final String created = "created_at";
        final Date early = new Github.Time(
            orgs.get("testCreatedAt")
                .json()
                .getString(created)
        ).date();
        TimeUnit.SECONDS.sleep(1L);
        final Date later = new Github.Time(
            orgs.get("testCreatedAt")
                .json()
                .getString(created)
        ).date();
        MatcherAssert.assertThat(later, Matchers.greaterThanOrEqualTo(early));
    }

}
