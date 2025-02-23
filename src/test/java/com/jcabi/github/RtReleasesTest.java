/**
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
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtReleases}.
 * @checkstyle MultipleStringLiterals (500 lines)
 * @since 0.8
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RtReleasesTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtReleases can fetch empty list of releases.
     */
    @Test
    public void canFetchEmptyListOfReleases() {
        final Releases releases = new RtReleases(
            new FakeRequest().withBody("[]"),
            RtReleasesTest.repo()
        );
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * RtReleases can fetch non empty list of releases.
     */
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
            releases.iterate().iterator().next().number(),
            Matchers.equalTo(number)
        );
    }

    /**
     * RtReleases can fetch a single release.
     */
    @Test
    public void canFetchSingleRelease() {
        final Releases releases = new RtReleases(
            new FakeRequest(), RtReleasesTest.repo()
        );
        MatcherAssert.assertThat(releases.get(1), Matchers.notNullValue());
    }

    /**
     * RtReleases can create a release.
     * @throws Exception If some problem inside
     */
    @Test
    public void canCreateRelease() throws Exception {
        final String tag = "v1.0.0";
        final String rel = release(tag).toString();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, rel)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, rel))
                .start(this.resource.port())
        ) {
            final RtReleases releases = new RtReleases(
                new JdkRequest(container.home()),
                repo()
            );
            final Release release = releases.create(tag);
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                release.json().getString("tag_name"),
                Matchers.equalTo(tag)
            );
            container.stop();
        }
    }

    /**
     * RtReleases can delete a release.
     * @throws Exception If some problem inside
     */
    @Test
    public void canDeleteRelease() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    ""
                )
            ).start(this.resource.port());
        ) {
            final Releases releases = new RtReleases(
                new ApacheRequest(container.home()),
                RtReleasesTest.repo()
            );
            releases.remove(1);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.uri().toString(),
                Matchers.endsWith("/releases/1")
            );
            MatcherAssert.assertThat(
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
