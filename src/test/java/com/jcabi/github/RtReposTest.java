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
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link com.jcabi.github.Repos}.
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

    /**
     * RtRepos can create a repo.
     * @throws Exception if some problem inside
     */
    @Test
    public void createRepo() throws Exception {
        final String owner = "test-owner";
        final String name = "test-repo";
        final String response = response(owner, name).toString();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, response)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, response))
                .start(this.resource.port())
        ) {
            final RtRepos repos = new RtRepos(
                Mockito.mock(Github.class),
                new ApacheRequest(container.home())
            );
            final Repo repo = this.rule.repo(repos);
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                repo.coordinates(),
                new IsEqual<>(new Coordinates.Simple(owner, name))
            );
            container.stop();
        }
    }

    /**
     * RtUsers can iterate users.
     * @throws Exception if there is any Error
     */
    @Test
    public void iterateRepos() throws Exception {
        final String identifier = "1";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(response("octocat", identifier))
                        .add(response("dummy", "2"))
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final RtRepos repos = new RtRepos(
                Mockito.mock(Github.class),
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                repos.iterate(identifier),
                Matchers.<Repo>iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtRepos can remove a repo.
     * @throws Exception if some problem inside
     */
    @Test
    public void removeRepo() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(this.resource.port())
        ) {
            final Repos repos = new RtRepos(
                Mockito.mock(Github.class),
                new ApacheRequest(container.home())
            );
            repos.remove(new Coordinates.Simple("", ""));
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.is(Matchers.emptyString())
            );
            container.stop();
        }
    }

    /**
     * Create and return JsonObject to test response.
     *
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
