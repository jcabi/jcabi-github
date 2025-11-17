/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link RtPublicMembers}.
 */
public final class RtPublicMembersITCase {
    /**
     * Test organization name.
     */
    private static final String ORG_NAME = "teamed";

    /**
     * Test organization.
     */
    private static Organization org;

    /**
     * Public member of the test organization.
     */
    private static User member;

    /**
     * Non-member of the test organization.
     */
    private static User nonMember;

    /**
     * Set up test fixtures.
     */
    @BeforeClass
    public static void setUp() {
        final Github github = new GithubIT().connect();
        final Users users = github.users();
        org = github.organizations().get(ORG_NAME);
        member = users.get("yegor256");
        nonMember = users.get("charset");
    }

    /**
     * RtPublicMembers can check whether a user is a public member
     * of an organization.
     * @throws Exception If something goes wrong
     */
    @Test
    public void checksPublicMembership() throws Exception {
        MatcherAssert.assertThat(
            "Check true positive of public membership in an organization",
            org.publicMembers().contains(member)
        );
        MatcherAssert.assertThat(
            "Check true negative of public membership in an organization",
            !org.publicMembers().contains(nonMember)
        );
    }

    /**
     * RtPublicMembers can list the public members of an organization.
     */
    @Test
    public void listsPublicMembers() {
        MatcherAssert.assertThat(
            org.publicMembers().iterate(),
            Matchers.<User>iterableWithSize(Matchers.greaterThanOrEqualTo(1))
        );
        MatcherAssert.assertThat(
            org.publicMembers().iterate(),
            Matchers.hasItem(member)
        );
        MatcherAssert.assertThat(
            org.publicMembers().iterate(),
            Matchers.not(Matchers.hasItem(nonMember))
        );
    }
}
