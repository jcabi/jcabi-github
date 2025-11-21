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
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkPublicMembers}.
 * @since 0.1
 */
final class MkPublicMembersTest {
    @Test
    void fetchesOrg() throws IOException {
        final Organization org = MkPublicMembersTest.organization();
        MatcherAssert.assertThat(
            "Values are not equal",
            org.publicMembers().org().login(),
            Matchers.equalTo(org.login())
        );
    }

    /**
     * MkPublicMembers can publicize/conceal a member's membership.
     */
    @Test
    void changesPublicityOfMembershipOfUsers() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        final PublicMembers members = org.publicMembers();
        final User user = org.github().users().get("johnny5");
        org.addMember(user);
        MatcherAssert.assertThat(
            "Newly-added user is not a public member",
            !members.contains(user),
            Matchers.is(true)
        );
        members.publicize(user);
        MatcherAssert.assertThat(
            "User has been made a public member",
            members.contains(user),
            Matchers.is(true)
        );
        members.conceal(user);
        MatcherAssert.assertThat(
            "Concealed user is not a public member",
            !members.contains(user),
            Matchers.is(true)
        );
        members.publicize(user);
        MatcherAssert.assertThat(
            "User has been made a public member again",
            members.contains(user),
            Matchers.is(true)
        );
    }

    @Test
    void checkPublicMembership() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        final PublicMembers members = org.publicMembers();
        final User user = org.github().users().get("agent99");
        MatcherAssert.assertThat(
            "Collection is not empty",
            members.iterate(),
            Matchers.emptyIterableOf(User.class)
        );
        org.addMember(user);
        MatcherAssert.assertThat(
            "The newly-added user is not a public member",
            !members.contains(user),
            Matchers.is(true)
        );
        members.publicize(user);
        MatcherAssert.assertThat(
            "The user has been made a public member",
            members.contains(user),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            members.iterate(),
            Matchers.hasItem(user)
        );
        members.conceal(user);
        MatcherAssert.assertThat(
            "The concealed user is not a public member",
            !members.contains(user),
            Matchers.is(true)
        );
    }

    @Test
    void iteratesPublicMembers() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        final PublicMembers members = org.publicMembers();
        final User user = org.github().users().get("jasmine");
        MatcherAssert.assertThat(
            "Collection is not empty",
            members.iterate(),
            Matchers.emptyIterableOf(User.class)
        );
        org.addMember(user);
        MatcherAssert.assertThat(
            "Collection is not empty",
            members.iterate(),
            Matchers.emptyIterableOf(User.class)
        );
        members.publicize(user);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            members.iterate(),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            members.iterate(),
            Matchers.hasItem(user)
        );
        members.conceal(user);
        MatcherAssert.assertThat(
            "Collection is not empty",
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
