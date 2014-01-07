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
package com.jcabi.github.mock;

import com.jcabi.github.Organization;
import com.jcabi.github.Organizations;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Github organizations.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.7
 */
public class MkOrganizationsTest {
    /**
     * MkOrganizations can list organizations.
     * @throws Exception If some problem inside
     */
    @Test
    public final void iteratesOrganizations() throws Exception {
        final Organizations orgs = getOrganizations();
        MatcherAssert.assertThat(orgs, Matchers.notNullValue());
    }

    /**
     * MkOrganizations can get specific organization.
     * @throws Exception If some problem inside
     */
    @Test
    public final void getSingleOrganization() throws Exception {
        final Organizations orgs = getOrganizations();
        final Organization org = orgs.iterate().iterator().next();
        Assume.assumeThat(org, Matchers.notNullValue());
        MatcherAssert.assertThat(org.orgId(), Matchers.greaterThan(0));
        final Organization lookup = orgs.get(org.orgId());
        MatcherAssert.assertThat(org.orgId(), Matchers.equalTo(lookup.orgId()));
    }

    /**
     * Convenience method to return the mock organizations.
     * @return A mock organizations object
     * @throws Exception If some problem inside
     */
    private static Organizations getOrganizations() throws Exception {
        final Organizations orgs = new MkOrganizations(
            new MkStorage.InFile(), "login-less"
        );
        final Organization org = orgs.get(1);
        MatcherAssert.assertThat(org, Matchers.notNullValue());
        MatcherAssert.assertThat(
            orgs.iterate(), Matchers.not(Matchers.emptyIterable())
        );
        MatcherAssert.assertThat(orgs.user(), Matchers.notNullValue());
        return orgs;
    }
}
