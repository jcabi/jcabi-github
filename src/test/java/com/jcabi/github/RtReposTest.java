/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
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
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link Repos}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtReposTest {

    /**
     * Owner of the created repo.
     */
    private static final String OWNER = "test-owner";

    /**
     * Name of the created repo.
     */
    private static final String NAME = "test-repo";

    @Test
    void createsRepoWithPost() throws IOException {
        final String response = RtReposTest.response(
            RtReposTest.OWNER, RtReposTest.NAME
        ).toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, response)
            ).next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response)
            ).start(RandomPort.port())
        ) {
            RtReposTest.create(container);
            MatcherAssert.assertThat(
                "Repo is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsRepoWithCoordinates() throws IOException {
        final String response = RtReposTest.response(
            RtReposTest.OWNER, RtReposTest.NAME
        ).toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, response)
            ).next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response)
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created repo has wrong coordinates",
                RtReposTest.create(container).coordinates(),
                new IsEqual<>(
                    new Coordinates.Simple(
                        RtReposTest.OWNER, RtReposTest.NAME
                    )
                )
            );
        }
    }

    @Test
    void iterateRepos() throws IOException {
        final String identifier = "1";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtReposTest.response("octocat", identifier))
                        .add(RtReposTest.response("dummy", "2"))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtRepos(
                    Mockito.mock(GitHub.class),
                    new ApacheRequest(container.home())
                ).iterate(identifier),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    @Test
    void iterateReposUsesPublicRepositoriesPath() throws IOException {
        final String identifier = "364";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(RtReposTest.response("octocat", identifier))
                        .build().toString()
                )
            ).start(RandomPort.port())
        ) {
            new RtRepos(
                Mockito.mock(GitHub.class),
                new ApacheRequest(container.home())
            ).iterate(identifier).iterator().next();
            MatcherAssert.assertThat(
                "iterate(...) must request /repositories?since=<id>",
                container.take().uri().toString(),
                Matchers.endsWith(
                    "/repositories?since=".concat(identifier)
                )
            );
            container.stop();
        }
    }

    @Test
    void removesRepoWithDelete() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            new RtRepos(
                Mockito.mock(GitHub.class),
                new ApacheRequest(container.home())
            ).remove(new Coordinates.Simple("", ""));
            MatcherAssert.assertThat(
                "Repo is not removed with DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void removesRepoWithoutBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            new RtRepos(
                Mockito.mock(GitHub.class),
                new ApacheRequest(container.home())
            ).remove(new Coordinates.Simple("", ""));
            MatcherAssert.assertThat(
                "Repo is removed with a body",
                container.take().body(),
                Matchers.is(Matchers.emptyString())
            );
        }
    }

    /**
     * Create a repo through the given container.
     * @param container Container to serve the repos
     * @return Created repo
     * @throws IOException If there is any I/O problem
     */
    private static Repo create(final MkContainer container) throws IOException {
        return new RepoRule().repo(
            new RtRepos(
                Mockito.mock(GitHub.class),
                new ApacheRequest(container.home())
            )
        );
    }

    /**
     * Create and return JsonObject to test response.
     * @param owner Owner name
     * @param name Repo name
     * @return JsonObject
     */
    private static JsonObject response(final String owner, final String name) {
        return Json.createObjectBuilder()
            .add("name", name)
            .add("full_name", String.format("%s/%s", owner, name)).add(
                "owner",
                Json.createObjectBuilder().add("login", owner).build()
            )
            .build();
    }
}
