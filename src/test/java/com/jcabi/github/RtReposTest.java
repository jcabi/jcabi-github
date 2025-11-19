/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Repos}.
 * @since 0.8
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
public final class RtReposTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RepoRule rule = new RepoRule();

    @Test
    public void createRepo() throws IOException {
        final String owner = "test-owner";
        final String name = "test-repo";
        final String response = RtReposTest.response(owner, name).toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, response)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response))
                .start(RandomPort.port())
        ) {
            final RtRepos repos = new RtRepos(
                Mockito.mock(GitHub.class),
                new ApacheRequest(container.home())
            );
            final Repo repo = this.rule.repo(repos);
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                "Assertion failed",
                repo.coordinates(),
                new IsEqual<>(new Coordinates.Simple(owner, name))
            );
            container.stop();
        }
    }

    @Test
    public void iterateRepos() throws IOException {
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
            final RtRepos repos = new RtRepos(
                Mockito.mock(GitHub.class),
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                repos.iterate(identifier),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    @Test
    public void removeRepo() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            final Repos repos = new RtRepos(
                Mockito.mock(GitHub.class),
                new ApacheRequest(container.home())
            );
            repos.remove(new Coordinates.Simple("", ""));
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                query.body(),
                Matchers.is(Matchers.emptyString())
            );
            container.stop();
        }
    }

    /**
     * Create and return JsonObject to test response.
     * @param owner Owner name
     * @param name Repo name
     * @return JsonObject
     */
    private static JsonObject response(
        final String owner, final String name) {
        return Json.createObjectBuilder()
            .add("name", name)
            .add("full_name", String.format("%s/%s", owner, name))
            .add(
                "owner",
                Json.createObjectBuilder().add("login", owner).build()
            )
            .build();
    }

}
