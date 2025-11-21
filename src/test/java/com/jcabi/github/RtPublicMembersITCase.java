/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

/**
 * Test case for {@link RtPublicMembers}.
 * @since 0.4
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
    @BeforeAll
    public void setUp() {
        final GitHub github = GitHubIT.connect();
        final Users users = github.users();
        RtPublicMembersITCase.org = github.organizations().get(RtPublicMembersITCase.ORG_NAME);
        RtPublicMembersITCase.member = users.get("yegor256");
        RtPublicMembersITCase.nonMember = users.get("charset");
    }

    @Test
    public void checksPublicMembership() throws IOException {
        MatcherAssert.assertThat(
            "Check true positive of public membership in an organization",
            RtPublicMembersITCase.org.publicMembers().contains(RtPublicMembersITCase.member),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Check true negative of public membership in an organization",
            !RtPublicMembersITCase.org.publicMembers().contains(RtPublicMembersITCase.nonMember),
            Matchers.is(true)
        );
    }

    @Test
    public void listsPublicMembers() {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            RtPublicMembersITCase.org.publicMembers().iterate(),
            Matchers.iterableWithSize(Matchers.greaterThanOrEqualTo(1))
        );
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            RtPublicMembersITCase.org.publicMembers().iterate(),
            Matchers.hasItem(RtPublicMembersITCase.member)
        );
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            RtPublicMembersITCase.org.publicMembers().iterate(),
            Matchers.not(Matchers.hasItem(RtPublicMembersITCase.nonMember))
        );
    }
}
