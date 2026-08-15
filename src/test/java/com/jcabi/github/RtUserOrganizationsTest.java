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
 * Test case for {@link RtUserOrganizations}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtUserOrganizationsTest {

    /**
     * Login of the user.
     */
    private static final String USERNAME = "octopus";

    @Test
    void canIterateOrganizationsForUnauthUser() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtUserOrganizationsTest.answer())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Wrong amount of organizations is iterated",
                RtUserOrganizationsTest.organizations(container).iterate(),
                Matchers.iterableWithSize(3)
            );
        }
    }

    @Test
    void iteratesOrganizationsFromCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtUserOrganizationsTest.answer())
                .start(RandomPort.port())
        ) {
            RtUserOrganizationsTest.organizations(container)
                .iterate().iterator().next();
            MatcherAssert.assertThat(
                "Organizations are iterated from a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith(
                    String.format(
                        "/users/%s/orgs", RtUserOrganizationsTest.USERNAME
                    )
                )
            );
        }
    }

    /**
     * Organizations served by the given container.
     * @param container Container to serve the organizations
     * @return Organizations
     * @throws IOException If there is any I/O problem
     */
    private static RtUserOrganizations organizations(
        final MkContainer container
    ) throws IOException {
        final GitHub github = new MkGitHub();
        return new RtUserOrganizations(
            github,
            new ApacheRequest(container.home()),
            github.users().get(RtUserOrganizationsTest.USERNAME)
        );
    }

    /**
     * Answer with three organizations.
     * @return Answer
     */
    private static MkAnswer answer() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            Json.createArrayBuilder()
                .add(RtUserOrganizationsTest.org(3, "org11"))
                .add(RtUserOrganizationsTest.org(4, "org12"))
                .add(RtUserOrganizationsTest.org(5, "org13"))
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
