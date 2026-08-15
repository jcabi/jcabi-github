/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.github.mock.MkOrganization;
import com.jcabi.github.mock.MkStorage;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtPublicMembers}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtPublicMembersTest {

    /**
     * Test organization.
     */
    private static final String ORG = "starfleet";

    /**
     * Test username.
     */
    private static final String USERNAME = "wesley";

    /**
     * Public members URL for test org.
     */
    private static final String MEMBERS_URL = String.format(
        "/orgs/%s/public_members",
        RtPublicMembersTest.ORG
    );

    /**
     * Public member URL for test user in test org.
     */
    private static final String MEMBER_URL = String.format(
        "%s/%s",
        RtPublicMembersTest.MEMBERS_URL,
        RtPublicMembersTest.USERNAME
    );

    /**
     * RtPublicMembers can fetch its organization.
     * @throws IOException If there is an I/O problem
     */
    @Test
    void fetchesOrg() throws IOException {
        final Organization org = RtPublicMembersTest.organization();
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtPublicMembers(new FakeRequest(), org).org(),
            Matchers.equalTo(org)
        );
    }

    @Test
    void concealsWithDelete() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
                .start(RandomPort.port())
        ) {
            RtPublicMembersTest.members(container)
                .conceal(RtPublicMembersTest.user());
            MatcherAssert.assertThat(
                "Membership is not concealed with DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void concealsWithoutBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
                .start(RandomPort.port())
        ) {
            RtPublicMembersTest.members(container)
                .conceal(RtPublicMembersTest.user());
            MatcherAssert.assertThat(
                "Membership is concealed with a body",
                container.take().body(),
                Matchers.is(Matchers.emptyOrNullString())
            );
        }
    }

    @Test
    void concealsAtCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
                .start(RandomPort.port())
        ) {
            RtPublicMembersTest.members(container)
                .conceal(RtPublicMembersTest.user());
            MatcherAssert.assertThat(
                "Membership is concealed at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith(RtPublicMembersTest.MEMBER_URL)
            );
        }
    }

    @Test
    void failsToConcealOnError() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR)
            ).start(RandomPort.port())
        ) {
            final RtPublicMembers members =
                RtPublicMembersTest.members(container);
            Assertions.assertThrows(
                AssertionError.class,
                () -> members.conceal(RtPublicMembersTest.user())
            );
        }
    }

    @Test
    void publicizesWithPut() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
                .start(RandomPort.port())
        ) {
            RtPublicMembersTest.members(container)
                .publicize(RtPublicMembersTest.user());
            MatcherAssert.assertThat(
                "Membership is not publicized with PUT",
                container.take().method(),
                Matchers.equalTo(Request.PUT)
            );
        }
    }

    @Test
    void publicizesAtCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
                .start(RandomPort.port())
        ) {
            RtPublicMembersTest.members(container)
                .publicize(RtPublicMembersTest.user());
            MatcherAssert.assertThat(
                "Membership is publicized at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith(RtPublicMembersTest.MEMBER_URL)
            );
        }
    }

    @Test
    void failsToPublicizeOnError() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR)
            ).start(RandomPort.port())
        ) {
            final RtPublicMembers members =
                RtPublicMembersTest.members(container);
            Assertions.assertThrows(
                AssertionError.class,
                () -> members.publicize(RtPublicMembersTest.user())
            );
        }
    }

    @Test
    void checksMembershipWithGet() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
                .start(RandomPort.port())
        ) {
            RtPublicMembersTest.members(container)
                .contains(RtPublicMembersTest.user());
            MatcherAssert.assertThat(
                "Membership is not checked with GET",
                container.take().method(),
                Matchers.equalTo(Request.GET)
            );
        }
    }

    @Test
    void checksMembershipAtCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
                .start(RandomPort.port())
        ) {
            RtPublicMembersTest.members(container)
                .contains(RtPublicMembersTest.user());
            MatcherAssert.assertThat(
                "Membership is checked at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith(RtPublicMembersTest.MEMBER_URL)
            );
        }
    }

    @Test
    void treatsNotFoundAsNonMember() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "404 is not interpreted as the user not being a member",
                RtPublicMembersTest.members(container)
                    .contains(RtPublicMembersTest.user()),
                Matchers.is(false)
            );
        }
    }

    @Test
    void treatsNoContentAsMember() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "204 is not interpreted as the user being a member",
                RtPublicMembersTest.members(container)
                    .contains(RtPublicMembersTest.user()),
                Matchers.is(true)
            );
        }
    }

    @Test
    void failsToCheckMembershipOnError() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR)
            ).start(RandomPort.port())
        ) {
            final RtPublicMembers members =
                RtPublicMembersTest.members(container);
            Assertions.assertThrows(
                AssertionError.class,
                () -> members.contains(RtPublicMembersTest.user())
            );
        }
    }

    @Test
    void iteratesWithGet() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPublicMembersTest.octobat())
                .start(RandomPort.port())
        ) {
            RtPublicMembersTest.members(container).iterate().iterator().next();
            MatcherAssert.assertThat(
                "Members are not iterated with GET",
                container.take().method(),
                Matchers.equalTo(Request.GET)
            );
        }
    }

    @Test
    void iteratesAtCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtPublicMembersTest.octobat())
                .start(RandomPort.port())
        ) {
            RtPublicMembersTest.members(container).iterate().iterator().next();
            MatcherAssert.assertThat(
                "Members are iterated at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith(RtPublicMembersTest.MEMBERS_URL)
            );
        }
    }

    @Test
    void failsToIterateOnError() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR)
            ).start(RandomPort.port())
        ) {
            final RtPublicMembers members =
                RtPublicMembersTest.members(container);
            Assertions.assertThrows(
                AssertionError.class,
                () -> members.iterate().iterator().next()
            );
        }
    }

    /**
     * Public members served by the given container.
     * @param container Container to serve the members
     * @return Public members
     * @throws IOException If there is an I/O problem
     */
    private static RtPublicMembers members(final MkContainer container)
        throws IOException {
        return new RtPublicMembers(
            new ApacheRequest(container.home()),
            RtPublicMembersTest.organization()
        );
    }

    /**
     * Answer with a single public member.
     * @return Answer
     */
    private static MkAnswer octobat() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            "[{\"login\":\"octobat\"}]"
        );
    }

    /**
     * Get test organization.
     * @return Organization
     * @throws IOException If there is an I/O problem
     */
    private static Organization organization() throws IOException {
        return new MkOrganization(new MkStorage.InFile(), RtPublicMembersTest.ORG);
    }

    /**
     * Get test user.
     * @return User
     * @throws IOException If there is an I/O problem
     */
    private static User user() throws IOException {
        return new MkGitHub().users().get(RtPublicMembersTest.USERNAME);
    }
}
