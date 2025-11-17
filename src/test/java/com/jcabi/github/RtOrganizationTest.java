/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtOrganization}.
 *
 */
public final class RtOrganizationTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtOrganization should be able to describe itself in JSON format.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canFetchIssueAsJson() throws Exception {
        final RtOrganization org = new RtOrganization(
            new MkGithub(),
            new FakeRequest().withBody("{\"organization\":\"json\"}"),
            "testJson"
        );
        MatcherAssert.assertThat(
            org.json().getString("organization"),
            Matchers.equalTo("json")
        );
    }

    /**
     * RtOrganization should be able to perform a patch request.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void patchWithJson() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
            ).start(this.resource.port())
        ) {
            final RtOrganization org = new RtOrganization(
                new MkGithub(),
                new ApacheRequest(container.home()),
                "testPatch"
            );
            org.patch(
                Json.createObjectBuilder().add("patch", "test").build()
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.PATCH)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.equalTo("{\"patch\":\"test\"}")
            );
            container.stop();
        }
    }

    /**
     * RtOrganization should be able to compare instances of each other.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
        final RtOrganization less = new RtOrganization(
            new MkGithub(),
            new FakeRequest(),
            "abc"
        );
        final RtOrganization greater = new RtOrganization(
            new MkGithub(),
            new FakeRequest(),
            "def"
        );
        MatcherAssert.assertThat(
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            greater.compareTo(less), Matchers.greaterThan(0)
        );
        MatcherAssert.assertThat(
            less.compareTo(less), Matchers.equalTo(0)
        );
    }

    /**
     * RtOrganization can return a String representation correctly reflecting
     * its URI.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    public void canRepresentAsString() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "blah")
            ).start(this.resource.port())
        ) {
            final RtOrganization org = new RtOrganization(
                new MkGithub(),
                new ApacheRequest(container.home()),
                "testToString"
            );
            MatcherAssert.assertThat(
                org.toString(),
                Matchers.endsWith("/orgs/testToString")
            );
            container.stop();
        }
    }

}
