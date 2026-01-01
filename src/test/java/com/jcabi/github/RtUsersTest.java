/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

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
import org.mockito.Mockito;

/**
 * Test case for {@link RtUsers}.
 * @since 0.4
 */
@ExtendWith(RandomPort.class)
final class RtUsersTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Test
    void iterateUsers() throws IOException {
        final String identifier = "1";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(RtUsersTest.json("octocat", identifier))
                    .add(RtUsersTest.json("dummy", "2"))
                    .build().toString()
            )
        ).start(RandomPort.port());
        final Users users = new RtUsers(
            Mockito.mock(GitHub.class),
            new ApacheRequest(container.home())
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            users.iterate(identifier),
            Matchers.iterableWithSize(2)
        );
        container.stop();
    }

    @Test
    void getSingleUser() throws IOException {
        final String login = "mark";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                RtUsersTest.json(login, "3").toString()
            )
        ).start(RandomPort.port());
        final Users users = new RtUsers(
            Mockito.mock(GitHub.class),
            new ApacheRequest(container.home())
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            users.get(login).login(),
            Matchers.equalTo(login)
        );
        container.stop();
    }

    @Test
    void getCurrentUser() throws IOException {
        final String login = "kendy";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                RtUsersTest.json(login, "4").toString()
            )
        ).start(RandomPort.port());
        final Users users = new RtUsers(
            Mockito.mock(GitHub.class),
            new ApacheRequest(container.home())
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            users.self().login(),
            Matchers.equalTo(login)
        );
        container.stop();
    }

    /**
     * Create and return JsonObject to test.
     * @param login Username to login
     * @param identifier User Id
     * @return JsonObject
     */
    private static JsonObject json(
        final String login, final String identifier
    ) {
        return Json.createObjectBuilder()
            .add("id", Integer.valueOf(identifier))
            .add("login", login)
            .build();
    }
}
