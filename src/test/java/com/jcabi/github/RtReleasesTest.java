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
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtReleases}.
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@ExtendWith(RandomPort.class)
public final class RtReleasesTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */


    @Test
    public void canFetchEmptyListOfReleases() {
        final Releases releases = new RtReleases(
            new FakeRequest().withBody("[]"),
            RtReleasesTest.repo()
        );
        MatcherAssert.assertThat(
            "Collection is not empty",
            releases.iterate(),
            Matchers.emptyIterable()
        );
    }

    @Test
    public void canFetchNonEmptyListOfReleases() {
        final int number = 1;
        final Releases releases = new RtReleases(
            new FakeRequest().withBody(
                Json.createArrayBuilder().add(
                    Json.createObjectBuilder()
                        .add("id", number)
                        .add("tag_name", "v1.0.0")
                        .add("name", "v1.0.0")
                        .add("body", "Release")
                ).build().toString()
            ),
            RtReleasesTest.repo()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            releases.iterate().iterator().next().number(),
            Matchers.equalTo(number)
        );
    }

    @Test
    public void canFetchSingleRelease() {
        final Releases releases = new RtReleases(
            new FakeRequest(), RtReleasesTest.repo()
        );
        MatcherAssert.assertThat(
            "Value is null", releases.get(1), Matchers.notNullValue()
        );
    }

    @Test
    public void canCreateRelease() throws IOException {
        final String tag = "v1.0.0";
        final String rel = RtReleasesTest.release(tag).toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, rel)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, rel))
                .start(RandomPort.port())
        ) {
            final RtReleases releases = new RtReleases(
                new JdkRequest(container.home()),
                RtReleasesTest.repo()
            );
            final Release release = releases.create(tag);
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                release.json().getString("tag_name"),
                Matchers.equalTo(tag)
            );
            container.stop();
        }
    }

    @Test
    public void canDeleteRelease() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    ""
                )
            ).start(RandomPort.port())
        ) {
            final Releases releases = new RtReleases(
                new ApacheRequest(container.home()),
                RtReleasesTest.repo()
            );
            releases.remove(1);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "String does not end with expected value",
                query.uri().toString(),
                Matchers.endsWith("/releases/1")
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "releases"))
            .when(repo).coordinates();
        return repo;
    }

    /**
     * Create and return JsonObject to test.
     * @param tag The tag name of the release
     * @return JsonObject
     */
    private static JsonObject release(final String tag) {
        return Json.createObjectBuilder()
            .add("id", 1)
            .add("tag_name", tag)
            .build();
    }
}
