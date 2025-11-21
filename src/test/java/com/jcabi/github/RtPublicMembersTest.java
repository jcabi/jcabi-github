/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;


/**
 * Test case for {@link RtPublicMembers}.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@ExtendWith(RandomPort.class)
public final class RtPublicMembersTest {
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
    public void fetchesOrg() throws IOException {
        final Organization org = RtPublicMembersTest.organization();
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtPublicMembers(new FakeRequest(), org).org(),
            Matchers.equalTo(org)
        );
    }

    /**
     * RtPublicMembers can conceal a user's membership in the organization.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void concealsMembers() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR))
                .start(RandomPort.port())
        ) {
            final RtPublicMembers members = new RtPublicMembers(
                new ApacheRequest(container.home()),
                RtPublicMembersTest.organization()
            );
            members.conceal(RtPublicMembersTest.user());
            final MkQuery req = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                req.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                req.body(),
                Matchers.is(Matchers.emptyOrNullString())
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                req.uri().toString(),
                Matchers.endsWith(RtPublicMembersTest.MEMBER_URL)
            );
            Assertions.assertThrows(
                AssertionError.class,
                () -> members.conceal(RtPublicMembersTest.user())
            );
            container.stop();
        }
    }

    /**
     * RtPublicMembers can publicize the membership of
     * a user in the organization.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void publicizesMembers() throws IOException {
        try (MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR))
            .start(RandomPort.port())
        ) {
            final RtPublicMembers members = new RtPublicMembers(
                new ApacheRequest(container.home()),
                RtPublicMembersTest.organization()
            );
            members.publicize(RtPublicMembersTest.user());
            final MkQuery req = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                req.method(),
                Matchers.equalTo(Request.PUT)
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                req.uri().toString(),
                Matchers.endsWith(RtPublicMembersTest.MEMBER_URL)
            );
            Assertions.assertThrows(
                AssertionError.class,
                () -> members.publicize(RtPublicMembersTest.user())
            );
            container.stop();
        }
    }

    /**
     * RtPublicMembers can check whether a user
     * is a public member of the organization.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void checkPublicMembership() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
                .next(
                    new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR)
                )
                .start(RandomPort.port())
        ) {
            final RtPublicMembers members = new RtPublicMembers(
                new ApacheRequest(container.home()),
                RtPublicMembersTest.organization()
            );
            members.contains(RtPublicMembersTest.user());
            final MkQuery req = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                req.method(),
                Matchers.equalTo(Request.GET)
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                req.uri().toString(),
                Matchers.endsWith(RtPublicMembersTest.MEMBER_URL)
            );
            MatcherAssert.assertThat(
                "404 is interpreted as the user not being a public member",
                !members.contains(RtPublicMembersTest.user()),
                Matchers.is(true)
            );
            MatcherAssert.assertThat(
                "204 is interpreted as the user being a public member",
                members.contains(RtPublicMembersTest.user()),
                Matchers.is(true)
            );
            Assertions.assertThrows(
                AssertionError.class,
                () -> members.contains(RtPublicMembersTest.user())
            );
            container.stop();
        }
    }

    /**
     * RtPublicMembers can list the public members of the organization.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void iteratesPublicMembers() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(
                    new MkAnswer.Simple(
                        HttpURLConnection.HTTP_OK,
                        "[{\"login\":\"octobat\"}]"
                    )
                )
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR))
                .start(RandomPort.port())
        ) {
            final RtPublicMembers members = new RtPublicMembers(
                new ApacheRequest(container.home()),
                RtPublicMembersTest.organization()
            );
            members.iterate().iterator().next();
            final MkQuery req = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                req.method(),
                Matchers.equalTo(Request.GET)
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                req.uri().toString(),
                Matchers.endsWith(RtPublicMembersTest.MEMBERS_URL)
            );
            Assertions.assertThrows(
                AssertionError.class,
                () -> members.iterate().iterator().next()
            );
            container.stop();
        }
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
