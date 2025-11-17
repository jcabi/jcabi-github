/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

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
import org.mockito.Mockito;

/**
 * Test case for {@link RtUsers}.
 *
 */
public final class RtUsersTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtUsers can iterate users.
     * @throws Exception if there is any error
     */
    @Test
    public void iterateUsers() throws Exception {
        final String identifier = "1";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(RtUsersTest.json("octocat", identifier))
                    .add(RtUsersTest.json("dummy", "2"))
                    .build().toString()
            )
        ).start(this.resource.port());
        final Users users = new RtUsers(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home())
        );
        MatcherAssert.assertThat(
            users.iterate(identifier),
            Matchers.<User>iterableWithSize(2)
        );
        container.stop();
    }

    /**
     * RtUsers can get a single user.
     *
     * @throws Exception  if there is any error
     */
    @Test
    public void getSingleUser() throws Exception {
        final String login = "mark";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                RtUsersTest.json(login, "3").toString()
            )
        ).start(this.resource.port());
        final Users users = new RtUsers(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home())
        );
        MatcherAssert.assertThat(
            users.get(login).login(),
            Matchers.equalTo(login)
        );
        container.stop();
    }

    /**
     * RtUsers can get a current  user.
     *
     * @throws Exception  if there is any error
     */
    @Test
    public void getCurrentUser() throws Exception {
        final String login = "kendy";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                RtUsersTest.json(login, "4").toString()
            )
        ).start(this.resource.port());
        final Users users = new RtUsers(
            Mockito.mock(Github.class),
            new ApacheRequest(container.home())
        );
        MatcherAssert.assertThat(
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
