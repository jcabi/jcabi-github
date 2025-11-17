/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtGists}.
 *
 */
public final class RtGistsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtGists can create new files.
     *
     */
    @Test
    public void canCreateFiles() throws IOException {
        try (
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                "{\"id\":\"1\"}"
            )
        ).start(this.resource.port())) {
            final Gists gists = new RtGists(
                new MkGitHub(),
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                gists.create(Collections.singletonMap("test", ""), false),
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                container.take().body(),
                Matchers.startsWith("{\"files\":{\"test\":{\"content\":")
            );
            container.stop();
        }
    }

    /**
     * RtGists can retrieve a specific Gist.
     *
     */
    @Test
    public void canRetrieveSpecificGist() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testing")
            ).start(this.resource.port())
        ) {
            final Gists gists = new RtGists(
                new MkGitHub(),
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                gists.get("gist"),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    /**
     * RtGists can iterate through its contents.
     *
     */
    @Test
    public void canIterateThrouRtGists() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"id\":\"hello\"}]"
                )
            ).start(this.resource.port())
        ) {
            final Gists gists = new RtGists(
                new MkGitHub(),
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                gists.iterate().iterator().next(),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }
    /**
     * RtGists can remove a gist by name.
     */
    @Test
    public void removesGistByName() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    ""
                )
            ).start(this.resource.port())) {
            final Gists gists = new RtGists(
                new MkGitHub(),
                new ApacheRequest(container.home())
            );
            gists.remove("12234");
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }
}
