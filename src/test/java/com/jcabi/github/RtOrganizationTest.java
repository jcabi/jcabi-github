/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtOrganization}.
 * @since 0.24
 */
public final class RtOrganizationTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    @Test
    public void canFetchIssueAsJson() throws IOException {
        final RtOrganization org = new RtOrganization(
            new MkGitHub(),
            new FakeRequest().withBody("{\"organization\":\"json\"}"),
            "testJson"
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            org.json().getString("organization"),
            Matchers.equalTo("json")
        );
    }

    @Test
    public void patchWithJson() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
            ).start(this.resource.port())
        ) {
            final RtOrganization org = new RtOrganization(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "testPatch"
            );
            org.patch(
                Json.createObjectBuilder().add("patch", "test").build()
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.PATCH)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                query.body(),
                Matchers.equalTo("{\"patch\":\"test\"}")
            );
            container.stop();
        }
    }

    @Test
    public void canCompareInstances() throws IOException {
        final RtOrganization less = new RtOrganization(
            new MkGitHub(),
            new FakeRequest(),
            "abc"
        );
        final RtOrganization greater = new RtOrganization(
            new MkGitHub(),
            new FakeRequest(),
            "def"
        );
        MatcherAssert.assertThat(
            "Value is not less than expected",
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            greater.compareTo(less), Matchers.greaterThan(0)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            less.compareTo(less), Matchers.equalTo(0)
        );
    }

    @Test
    public void canRepresentAsString() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "blah")
            ).start(this.resource.port())
        ) {
            final RtOrganization org = new RtOrganization(
                new MkGitHub(),
                new ApacheRequest(container.home()),
                "testToString"
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                org.toString(),
                Matchers.endsWith("/orgs/testToString")
            );
            container.stop();
        }
    }

}
