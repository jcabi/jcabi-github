/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtAssignees}.
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@ExtendWith(RandomPort.class)
final class RtAssigneesTest {

    @Test
    void iteratesAssignees() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtAssigneesTest.json("octocat"))
                        .add(RtAssigneesTest.json("dummy"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            final Assignees users = new RtAssignees(
                new JdkRequest(container.home()),
                RtAssigneesTest.repo()
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                users.iterate(),
                Matchers.iterableWithSize(2)
            );
        }
    }

    @Test
    void checkUserIsAssigneeForRepo() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    Json.createArrayBuilder()
                        .add(RtAssigneesTest.json("octocat2"))
                        .add(RtAssigneesTest.json("dummy"))
                        .build().toString()
                )
            ).start(RandomPort.port())) {
            final Assignees users = new RtAssignees(
                new JdkRequest(container.home()),
                RtAssigneesTest.repo()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                users.check("octocat2"),
                Matchers.equalTo(true)
            );
        }
    }

    @Test
    void checkUserIsNotAssigneeForRepo() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    Json.createArrayBuilder()
                        .add(RtAssigneesTest.json("octocat3"))
                        .add(RtAssigneesTest.json("dummy"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            final Assignees users = new RtAssignees(
                new JdkRequest(container.home()),
                RtAssigneesTest.repo()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                users.check("octocat33"),
                Matchers.equalTo(false)
            );
        }
    }

    /**
     * Create and return JsonObject to test.
     * @param login Username to login
     * @return JsonObject
     */
    private static JsonValue json(final String login) {
        return Json.createObjectBuilder()
            .add("login", login)
            .build();
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "assignee"))
            .when(repo).coordinates();
        Mockito.doReturn(Mockito.mock(GitHub.class)).when(repo).github();
        return repo;
    }
}
