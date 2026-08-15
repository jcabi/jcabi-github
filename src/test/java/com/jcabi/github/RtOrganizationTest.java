/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtOrganization}.
 * @since 0.24
 */
@ExtendWith(RandomPort.class)
final class RtOrganizationTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void canFetchIssueAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtOrganization(
                new MkGitHub(),
                new FakeRequest().withBody("{\"organization\":\"json\"}"),
                "testJson"
            ).json().getString("organization"),
            Matchers.equalTo("json")
        );
    }

    @Test
    void patchesThroughPatchMethod() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
            ).start(RandomPort.port())
        ) {
            RtOrganizationTest.patch(container);
            MatcherAssert.assertThat(
                "Organization is not patched through PATCH",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
        }
    }

    @Test
    void sendsPatchInRequestBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
            ).start(RandomPort.port())
        ) {
            RtOrganizationTest.patch(container);
            MatcherAssert.assertThat(
                "Patch is not sent in the request body",
                container.take().body(),
                Matchers.equalTo("{\"patch\":\"test\"}")
            );
        }
    }

    @Test
    void comparesSmallerOrganization() throws IOException {
        MatcherAssert.assertThat(
            "Organization is not less than the greater one",
            RtOrganizationTest.organization("abc").compareTo(
                RtOrganizationTest.organization("def")
            ),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerOrganization() throws IOException {
        MatcherAssert.assertThat(
            "Organization is not greater than the smaller one",
            RtOrganizationTest.organization("def").compareTo(
                RtOrganizationTest.organization("abc")
            ),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void comparesEqualOrganizations() throws IOException {
        MatcherAssert.assertThat(
            "Equal organizations are not the same",
            RtOrganizationTest.organization("abc").compareTo(
                RtOrganizationTest.organization("abc")
            ),
            Matchers.equalTo(0)
        );
    }

    @Test
    void canRepresentAsString() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "blah")
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "String does not end with expected value",
                new RtOrganization(
                    new MkGitHub(),
                    new ApacheRequest(container.home()),
                    "testToString"
                ).toString(),
                Matchers.endsWith("/orgs/testToString")
            );
            container.stop();
        }
    }

    /**
     * Patch the organization served by the given container.
     * @param container Container to serve the organization
     * @throws IOException If there is any I/O problem
     */
    private static void patch(final MkContainer container) throws IOException {
        new RtOrganization(
            new MkGitHub(),
            new ApacheRequest(container.home()),
            "testPatch"
        ).patch(Json.createObjectBuilder().add("patch", "test").build());
    }

    /**
     * Organization with the given login.
     * @param login Login of the organization
     * @return The organization
     * @throws IOException If fails
     */
    private static RtOrganization organization(final String login)
        throws IOException {
        return new RtOrganization(
            new MkGitHub(),
            new FakeRequest(),
            login
        );
    }
}
