/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
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
 * Test case for {@link RtReleases}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtReleasesTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void canFetchEmptyListOfReleases() {
        MatcherAssert.assertThat(
            "Collection is not empty",
            new RtReleases(
                new FakeRequest().withBody("[]"),
                RtReleasesTest.repo()
            ).iterate(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void canFetchNonEmptyListOfReleases() {
        final int number = 1;
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtReleases(
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
            ).iterate().iterator().next().number(),
            Matchers.equalTo(number)
        );
    }

    @Test
    void canFetchSingleRelease() {
        MatcherAssert.assertThat(
            "Value is null", new RtReleases(
                new FakeRequest(), RtReleasesTest.repo()
            ).get(1), Matchers.notNullValue()
        );
    }

    @Test
    void createsReleaseWithPost() throws IOException {
        final String tag = "v1.0.0";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtReleasesTest.created(tag))
                .start(RandomPort.port())
        ) {
            RtReleasesTest.releases(container).create(tag);
            MatcherAssert.assertThat(
                "Release is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsReleaseWithTag() throws IOException {
        final String tag = "v1.0.0";
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtReleasesTest.created(tag))
                .next(RtReleasesTest.fetched(tag))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created release has a wrong tag",
                RtReleasesTest.releases(container).create(tag)
                    .json().getString("tag_name"),
                Matchers.equalTo(tag)
            );
        }
    }

    @Test
    void deletesReleaseAtCorrectUri() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            RtReleasesTest.releases(container).remove(1);
            MatcherAssert.assertThat(
                "Release is deleted at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/releases/1")
            );
        }
    }

    @Test
    void deletesReleaseWithDelete() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            RtReleasesTest.releases(container).remove(1);
            MatcherAssert.assertThat(
                "Release is not deleted with DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    private static RtReleases releases(final MkContainer container)
        throws IOException {
        return new RtReleases(
            new JdkRequest(container.home()),
            RtReleasesTest.repo()
        );
    }

    private static MkAnswer created(final String tag) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            RtReleasesTest.release(tag).toString()
        );
    }

    private static MkAnswer fetched(final String tag) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtReleasesTest.release(tag).toString()
        );
    }

    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "releases"))
            .when(repo).coordinates();
        return repo;
    }

    private static JsonObject release(final String tag) {
        return Json.createObjectBuilder()
            .add("id", 1)
            .add("tag_name", tag)
            .build();
    }
}
