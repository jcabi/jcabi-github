/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.immutable.ArrayMap;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPulls}.
 *
 * @checkstyle ClassDataAbstractionCouplingCheck (200 lines)
 */
public final class RtPullsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtPulls can create a pull request.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void createPull() throws Exception {
        final String title = "new feature";
        final String body = pull(title).toString();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body))
                .start(this.resource.port())
        ) {
            final RtPulls pulls = new RtPulls(
                new ApacheRequest(container.home()),
                repo()
            );
            final Pull pull = pulls.create(title, "octocat", "master");
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                new Pull.Smart(pull).title(),
                Matchers.equalTo(title)
            );
            container.stop();
        }
    }

    /**
     * RtPulls can get a single pull request.
     * @throws Exception if some problem inside
     */
    @Test
    public void getSinglePull() throws Exception {
        final String title = "new-feature";
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    pull(title).toString()
                )
            ).start(this.resource.port())
        ) {
            final RtPulls pulls = new RtPulls(
                new ApacheRequest(container.home()),
                repo()
            );
            final Pull pull = pulls.get(Tv.BILLION);
            MatcherAssert.assertThat(
                new Pull.Smart(pull).title(),
                Matchers.equalTo(title)
            );
            container.stop();
        }
    }

    /**
     * RtPulls can iterate pulls.
     * @throws Exception if there is any error
     */
    @Test
    public void iteratePulls() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add(pull("new-topic"))
                        .add(pull("Amazing new feature"))
                        .build().toString()
                )
            ).start(this.resource.port())
        ) {
            final RtPulls pulls = new RtPulls(
                new ApacheRequest(container.home()),
                repo()
            );
            MatcherAssert.assertThat(
                pulls.iterate(new ArrayMap<>()),
                Matchers.<Pull>iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * Create and return JsonObject to test.
     * @param title The title of the pull request
     * @return JsonObject
     */
    private static JsonObject pull(final String title) {
        return Json.createObjectBuilder()
            .add("number", Tv.BILLION)
            .add("state", Issue.OPEN_STATE)
            .add("title", title)
            .build();
    }

    /**
     * Create and return repo to test.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }
}
