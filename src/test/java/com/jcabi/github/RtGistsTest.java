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
 * @since 0.1
 */
public final class RtGistsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    @Test
    public void canCreateFiles() throws IOException {
        try (
        MkContainer container = new MkGrizzlyContainer().next(
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
                "Value is null",
                gists.create(Collections.singletonMap("test", ""), false),
                Matchers.notNullValue()
            );
            MatcherAssert.assertThat(
                "String does not start with expected value",
                container.take().body(),
                Matchers.startsWith("{\"files\":{\"test\":{\"content\":")
            );
            container.stop();
        }
    }

    @Test
    public void canRetrieveSpecificGist() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testing")
            ).start(this.resource.port())
        ) {
            final Gists gists = new RtGists(
                new MkGitHub(),
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                "Value is null",
                gists.get("gist"),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    public void canIterateThrouRtGists() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
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
                "Value is null",
                gists.iterate().iterator().next(),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    public void removesGistByName() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
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
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }
}
