/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
            new MkStorage.InFile()
        ).get(RandomStringUtils.randomAlphanumeric(Tv.TWENTY));
    }
}
