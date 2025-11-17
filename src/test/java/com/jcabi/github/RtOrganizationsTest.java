/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtOrganizations}.
 *
 */
public final class RtOrganizationsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtOrganizations should be able to get a single organization.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void fetchesSingleOrganization() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(this.resource.port())
        ) {
            final Organizations orgs = new RtOrganizations(
                new MkGithub(),
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                orgs.get("org"),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    /**
     * RtOrganizations should be able to iterate
     * the logged-in user's organizations.
     *
     * @throws Exception If a problem occurs
     * @checkstyle MagicNumberCheck (25 lines)
     */
    @Test
    public void retrievesOrganizations() throws Exception {
        final Github github = new MkGithub();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(org(1, "org1"))
                        .add(org(2, "org2"))
                        .add(org(3, "org3"))
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final Organizations orgs = new RtOrganizations(
                github,
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                orgs.iterate(),
                Matchers.<Organization>iterableWithSize(Tv.THREE)
            );
            MatcherAssert.assertThat(
                container.take().uri().toString(),
                Matchers.endsWith("/user/orgs")
            );
            container.stop();
        }
    }

    /**
     * Create and return organization to test.
     * @param number Organization ID
     * @param login Organization login name.
     * @return JsonObject
     */
    private static JsonObject org(final int number, final String login) {
        return Json.createObjectBuilder()
            .add("id", number)
            .add("login", login)
            .build();
    }
}
