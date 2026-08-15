/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

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
     * MkPublicMembers keeps a new member private.
     * @throws IOException If there is an I/O problem
     */
    @Test
    void hidesNewMember() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        MatcherAssert.assertThat(
            "Newly-added user is a public member",
            org.publicMembers().contains(MkPublicMembersTest.member(org)),
            Matchers.is(false)
        );
    }

    /**
     * MkPublicMembers can publicize a member's membership.
     * @throws IOException If there is an I/O problem
     */
    @Test
    void publicizesMember() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        final PublicMembers members = org.publicMembers();
        final User user = MkPublicMembersTest.member(org);
        members.publicize(user);
        MatcherAssert.assertThat(
            "User is not a public member",
            members.contains(user),
            Matchers.is(true)
        );
    }

    /**
     * MkPublicMembers can conceal a member's membership.
     * @throws IOException If there is an I/O problem
     */
    @Test
    void concealsMember() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        final PublicMembers members = org.publicMembers();
        final User user = MkPublicMembersTest.member(org);
        members.publicize(user);
        members.conceal(user);
        MatcherAssert.assertThat(
            "Concealed user is still a public member",
            members.contains(user),
            Matchers.is(false)
        );
    }

    /**
     * MkPublicMembers can publicize a concealed member again.
     * @throws IOException If there is an I/O problem
     */
    @Test
    void publicizesConcealedMember() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        final PublicMembers members = org.publicMembers();
        final User user = MkPublicMembersTest.member(org);
        members.publicize(user);
        members.conceal(user);
        members.publicize(user);
        MatcherAssert.assertThat(
            "User is not a public member again",
            members.contains(user),
            Matchers.is(true)
        );
    }

    @Test
    void startsWithoutPublicMembers() throws IOException {
        MatcherAssert.assertThat(
            "Collection is not empty",
            MkPublicMembersTest.organization().publicMembers().iterate(),
            Matchers.emptyIterableOf(User.class)
        );
    }

    @Test
    void iteratesOverNoHiddenMembers() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        MkPublicMembersTest.member(org);
        MatcherAssert.assertThat(
            "Collection is not empty",
            org.publicMembers().iterate(),
            Matchers.emptyIterableOf(User.class)
        );
    }

    @Test
    void countsPublicMembers() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        final PublicMembers members = org.publicMembers();
        members.publicize(MkPublicMembersTest.member(org));
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            members.iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void iteratesPublicMembers() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        final PublicMembers members = org.publicMembers();
        final User user = MkPublicMembersTest.member(org);
        members.publicize(user);
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            members.iterate(),
            Matchers.hasItem(user)
        );
    }

    @Test
    void iteratesOverNoConcealedMembers() throws IOException {
        final MkOrganization org = MkPublicMembersTest.organization();
        final PublicMembers members = org.publicMembers();
        final User user = MkPublicMembersTest.member(org);
        members.publicize(user);
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
        ).get(RandomStringUtils.secure().nextAlphanumeric(20));
    }

    /**
     * Add a member to the given organization.
     * @param org Organization to add the member to
     * @return Added member
     * @throws IOException If there is an I/O problem
     */
    private static User member(final MkOrganization org) throws IOException {
        final User user = org.github().users().get("johnny5");
        org.addMember(user);
        return user;
    }
}
