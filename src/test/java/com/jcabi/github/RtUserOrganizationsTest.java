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
 * Test case for {@link RtUserOrganizations}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtUserOrganizationsTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    void canIterateOrganizationsForUnauthUser() throws IOException {
        final String username = "octopus";
        final GitHub github = new MkGitHub();
        final User user = github.users().get(username);
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(RtUserOrganizationsTest.org(Tv.THREE, "org11"))
                    .add(RtUserOrganizationsTest.org(Tv.FOUR, "org12"))
                    .add(RtUserOrganizationsTest.org(Tv.FIVE, "org13"))
                    .build().toString()
            )
        ).start(RandomPort.port());
        try {
            final UserOrganizations orgs = new RtUserOrganizations(
                github,
                new ApacheRequest(container.home()),
                user
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                orgs.iterate(),
                Matchers.iterableWithSize(Tv.THREE)
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                container.take().uri().toString(),
                Matchers.endsWith(String.format("/users/%s/orgs", username))
            );
        } finally {
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
