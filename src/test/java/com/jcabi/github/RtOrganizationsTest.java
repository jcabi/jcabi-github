/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

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
     */
    @Test
    void fetchesSingleOrganization() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Value is null",
                new RtOrganizations(
                    new MkGitHub(),
                    new ApacheRequest(container.home())
                ).get("org"),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    void retrievesOrganizations() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtOrganizationsTest.answer())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Wrong amount of organizations is retrieved",
                new RtOrganizations(
                    new MkGitHub(),
                    new ApacheRequest(container.home())
                ).iterate(),
                Matchers.iterableWithSize(3)
            );
        }
    }

    @Test
    void retrievesOrganizationsFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtOrganizationsTest.answer())
                .start(RandomPort.port())
        ) {
            new RtOrganizations(
                new MkGitHub(),
                new ApacheRequest(container.home())
            ).iterate().iterator().next();
            MatcherAssert.assertThat(
                "Organizations are retrieved from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/user/orgs")
            );
        }
    }

    /**
     * Answer with three organizations.
     * @return Answer
     */
    private static MkAnswer answer() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder()
                .add(RtOrganizationsTest.org(1, "org1"))
                .add(RtOrganizationsTest.org(2, "org2"))
                .add(RtOrganizationsTest.org(3, "org3"))
                .build().toString()
        );
    }

    /**
     * Create and return organization to test.
     * @param number Organization ID
     * @param login Organization login name
     * @return JsonObject
     */
    private static JsonObject org(final int number, final String login) {
        return Json.createObjectBuilder()
            .add("id", number)
            .add("login", login)
            .build();
    }
}
