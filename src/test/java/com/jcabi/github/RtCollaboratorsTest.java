/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.github.mock.MkStorage;
import com.jcabi.http.Request;
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
 * Tests for {@link RtCollaborators}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtCollaboratorsTest {

    /**
     * RtCollaborators can iterate over a list of collaborators.
     * @throws Exception if any error occurs.
     */
    @Test
    void canIterate() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtCollaboratorsTest.json("octocat"))
                        .add(RtCollaboratorsTest.json("dummy"))
                        .build().toString()
                )
                ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtCollaborators(
                    new JdkRequest(container.home()),
                    RtCollaboratorsTest.repo()
                ).iterate(),
                Matchers.iterableWithSize(2)
            );
        }
    }

    /**
     * User can be added to a repo as a collaborator.
     * @throws Exception if any error occurs.
     */
    @Test
    void userCanBeAddedAsCollaborator() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    Json.createArrayBuilder()
                        .add(RtCollaboratorsTest.json("octocat2"))
                        .add(RtCollaboratorsTest.json("dummy"))
                        .build().toString()
                )
                ).start(RandomPort.port())
        ) {
            final Collaborators users = new RtCollaborators(
                new JdkRequest(container.home()),
                RtCollaboratorsTest.repo()
            );
            users.add("dummy1");
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.PUT)
            );
            container.stop();
        }
    }

    /**
     * User can be checked for being a collaborator.
     * @throws Exception if any error occurs.
     */
    @Test
    void userCanBeTestForBeingCollaborator() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    Json.createArrayBuilder()
                        .add(RtCollaboratorsTest.json("octocat2"))
                        .add(RtCollaboratorsTest.json("dummy"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Values are not equal",
                new RtCollaborators(
                    new JdkRequest(container.home()),
                    RtCollaboratorsTest.repo()
                ).isCollaborator("octocat2"),
                Matchers.equalTo(true)
            );
            container.stop();
        }
    }

    /**
     * User can be removed from a list of collaborators.
     * @throws Exception if any error occurs.
     */
    @Test
    void userCanBeRemoved() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    Json.createArrayBuilder()
                        .add(RtCollaboratorsTest.json("octocat2"))
                        .add(RtCollaboratorsTest.json("dummy"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            final Collaborators users = new RtCollaborators(
                new JdkRequest(container.home()),
                RtCollaboratorsTest.repo()
            );
            users.remove("dummy");
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }

    private static JsonValue json(final String login) {
        return Json.createObjectBuilder()
            .add("login", login)
            .build();
    }

    private static Repo repo() throws IOException {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "collaboratorrepo"))
            .when(repo).coordinates();
        Mockito.doReturn(new MkGitHub(new MkStorage.InFile(), "userLogin"))
            .when(repo).github();
        return repo;
    }
}
