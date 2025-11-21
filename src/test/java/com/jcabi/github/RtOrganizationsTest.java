/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtOrganizations}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtOrganizationsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    void fetchesSingleOrganization() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            final Organizations orgs = new RtOrganizations(
                new MkGitHub(),
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                "Value is null",
                orgs.get("org"),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    /**
     * RtOrganizations should be able to iterate
     * the logged-in user's organizations.
     * @checkstyle MagicNumberCheck (25 lines)
     */
    @Test
    void retrievesOrganizations() throws IOException {
        final GitHub github = new MkGitHub();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtOrganizationsTest.org(1, "org1"))
                        .add(RtOrganizationsTest.org(2, "org2"))
                        .add(RtOrganizationsTest.org(3, "org3"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            final Organizations orgs = new RtOrganizations(
                github,
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                orgs.iterate(),
                Matchers.iterableWithSize(Tv.THREE)
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
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
