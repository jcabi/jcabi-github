/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtGists}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtGistsTest {

    @Test
    void canCreateFiles() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    "{\"id\":\"1\"}"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Gist is not created",
                new RtGists(
                    new MkGitHub(),
                    new ApacheRequest(container.home())
                ).create(Collections.singletonMap("test", ""), false),
                Matchers.notNullValue()
            );
        }
    }

    @Test
    void sendsFilesWhileCreating() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    "{\"id\":\"1\"}"
                )
            ).start(RandomPort.port())
        ) {
            new RtGists(
                new MkGitHub(),
                new ApacheRequest(container.home())
            ).create(Collections.singletonMap("test", ""), false);
            MatcherAssert.assertThat(
                "Files are not sent while creating the gist",
                container.take().body(),
                Matchers.startsWith("{\"files\":{\"test\":{\"content\":")
            );
        }
    }

    @Test
    void canRetrieveSpecificGist() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "testing")
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Value is null",
                new RtGists(
                    new MkGitHub(),
                    new ApacheRequest(container.home())
                ).get("gist"),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    void canIterateThroughRtGists() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "[{\"id\":\"hello\"}]"
                )
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Value is null",
                new RtGists(
                    new MkGitHub(),
                    new ApacheRequest(container.home())
                ).iterate().iterator().next(),
                Matchers.notNullValue()
            );
            container.stop();
        }
    }

    @Test
    void removesGistByName() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    ""
                )
            ).start(RandomPort.port())
        ) {
            final Gists gists = new RtGists(
                new MkGitHub(),
                new ApacheRequest(container.home())
            );
            gists.remove("12234");
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }
}
