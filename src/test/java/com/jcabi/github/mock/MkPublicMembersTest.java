/**
 * Copyright (c) 2013-2015, jcabi.com
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

import com.jcabi.aspects.Tv;
import com.jcabi.github.Organization;
import com.jcabi.github.PublicMembers;
import com.jcabi.github.User;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkPublicMembers}.
 *
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
 */
public final class MkPublicMembersTest {
    /**
     * MkPublicMembers can fetch its organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesOrg() throws Exception {
        final Organization org = organization();
        MatcherAssert.assertThat(
            org.publicMembers().org().login(),
            Matchers.equalTo(org.login())
        );
    }

    /**
     * MkPublicMembers can publicize/conceal a member's membership.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesPublicityOfMembershipOfUsers() throws Exception {
        final MkOrganization org = organization();
        final PublicMembers members = org.publicMembers();
        final User user = org.github().users().get("johnny5");
        org.addMember(user);
        MatcherAssert.assertThat(
            "Newly-added user is not a public member",
            !members.contains(user)
        );
        members.publicize(user);
        MatcherAssert.assertThat(
            "User has been made a public member",
            members.contains(user)
        );
        members.conceal(user);
        MatcherAssert.assertThat(
            "Concealed user is not a public member",
            !members.contains(user)
        );
        members.publicize(user);
        MatcherAssert.assertThat(
            "User has been made a public member again",
            members.contains(user)
        );
    }

    /**
     * MkPublicMembers can check whether a user is a public member.
     * @throws Exception If some problem inside
     */
    @Test
    public void checkPublicMembership() throws Exception {
        final MkOrganization org = organization();
        final PublicMembers members = org.publicMembers();
        final User user = org.github().users().get("agent99");
        MatcherAssert.assertThat(
            members.iterate(),
            Matchers.emptyIterableOf(User.class)
        );
        org.addMember(user);
        MatcherAssert.assertThat(
            "The newly-added user is not a public member",
            !members.contains(user)
        );
        members.publicize(user);
        MatcherAssert.assertThat(
            "The user has been made a public member",
            members.contains(user)
        );
        MatcherAssert.assertThat(members.iterate(), Matchers.hasItem(user));
        members.conceal(user);
        MatcherAssert.assertThat(
            "The concealed user is not a public member",
            !members.contains(user)
        );
    }

    /**
     * MkPublicMembers can iterate over all public members.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesPublicMembers() throws Exception {
        final MkOrganization org = organization();
        final PublicMembers members = org.publicMembers();
        final User user = org.github().users().get("jasmine");
        MatcherAssert.assertThat(
            members.iterate(),
            Matchers.emptyIterableOf(User.class)
        );
        org.addMember(user);
        MatcherAssert.assertThat(
            members.iterate(),
            Matchers.emptyIterableOf(User.class)
        );
        members.publicize(user);
        MatcherAssert.assertThat(
            members.iterate(),
            Matchers.<User>iterableWithSize(1)
        );
        MatcherAssert.assertThat(members.iterate(), Matchers.hasItem(user));
        members.conceal(user);
        MatcherAssert.assertThat(
            members.iterate(),
            Matchers.emptyIterableOf(User.class)
        );
    }

    /**
     * Get test organization.
     * @return Organization
     * @throws IOException If there is an I/O problem
     */
    private static MkOrganization organization() throws IOException {
        return (MkOrganization) new MkOrganizations(
            new MkStorage.InFile(),
            "maxwell"
        ).get(RandomStringUtils.randomAlphanumeric(Tv.TWENTY));
    }
}
