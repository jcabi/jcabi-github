/**
 * Copyright (c) 2013-2023, jcabi.com
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
package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test case for {@link RtPublicMembers}.
 *
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
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
        ORG
    );

    /**
     * Public member URL for test user in test org.
     */
    private static final String MEMBER_URL = String.format(
        "%s/%s",
        MEMBERS_URL,
        USERNAME
    );

    /**
     * Rule for checking thrown exception.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    @SuppressWarnings("deprecation")
    public transient ExpectedException thrown = ExpectedException.none();

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtPublicMembers can fetch its organization.
     * @throws IOException If there is an I/O problem
     */
    @Test
    public void fetchesOrg() throws IOException {
        final Organization org = organization();
        MatcherAssert.assertThat(
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
            final MkContainer container = new MkGrizzlyContainer()
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR))
            .start(this.resource.port())
        ) {
            final RtPublicMembers members = new RtPublicMembers(
                new ApacheRequest(container.home()),
                organization()
            );
            members.conceal(user());
            final MkQuery req = container.take();
            MatcherAssert.assertThat(
                req.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                req.body(),
                Matchers.is(Matchers.emptyOrNullString())
            );
            MatcherAssert.assertThat(
                req.uri().toString(),
                Matchers.endsWith(MEMBER_URL)
            );
            this.thrown.expect(AssertionError.class);
            members.conceal(user());
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
            .start(this.resource.port())
        ) {
            final RtPublicMembers members = new RtPublicMembers(
                new ApacheRequest(container.home()),
                organization()
            );
            members.publicize(user());
            final MkQuery req = container.take();
            MatcherAssert.assertThat(
                req.method(),
                Matchers.equalTo(Request.PUT)
            );
            MatcherAssert.assertThat(
                req.uri().toString(),
                Matchers.endsWith(MEMBER_URL)
            );
            this.thrown.expect(AssertionError.class);
            members.publicize(user());
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
            final MkContainer container = new MkGrizzlyContainer()
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
                .next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT))
                .next(
                    new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR)
                )
                .start(this.resource.port())
        ) {
            final RtPublicMembers members = new RtPublicMembers(
                new ApacheRequest(container.home()),
                organization()
            );
            members.contains(user());
            final MkQuery req = container.take();
            MatcherAssert.assertThat(
                req.method(),
                Matchers.equalTo(Request.GET)
            );
            MatcherAssert.assertThat(
                req.uri().toString(),
                Matchers.endsWith(MEMBER_URL)
            );
            MatcherAssert.assertThat(
                "404 is interpreted as the user not being a public member",
                !members.contains(user())
            );
            MatcherAssert.assertThat(
                "204 is interpreted as the user being a public member",
                members.contains(user())
            );
            this.thrown.expect(AssertionError.class);
            members.contains(user());
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
            final MkContainer container = new MkGrizzlyContainer()
                .next(
                    new MkAnswer.Simple(
                        HttpURLConnection.HTTP_OK,
                        "[{\"login\":\"octobat\"}]"
                    )
            )
            .next(new MkAnswer.Simple(HttpURLConnection.HTTP_INTERNAL_ERROR))
            .start(this.resource.port())
        ) {
            final RtPublicMembers members = new RtPublicMembers(
                new ApacheRequest(container.home()),
                organization()
            );
            members.iterate().iterator().next();
            final MkQuery req = container.take();
            MatcherAssert.assertThat(
                req.method(),
                Matchers.equalTo(Request.GET)
            );
            MatcherAssert.assertThat(
                req.uri().toString(),
                Matchers.endsWith(MEMBERS_URL)
            );
            this.thrown.expect(AssertionError.class);
            members.iterate().iterator().next();
            container.stop();
        }
    }

    /**
     * Get test organization.
     * @return Organization
     * @throws IOException If there is an I/O problem
     */
    private static Organization organization() throws IOException {
        return new MkOrganization(new MkStorage.InFile(), ORG);
    }

    /**
     * Get test user.
     * @return User
     * @throws IOException If there is an I/O problem
     */
    private static User user() throws IOException {
        return new MkGithub().users().get(USERNAME);
    }
}
