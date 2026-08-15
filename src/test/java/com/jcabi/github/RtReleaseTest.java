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
import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtRelease}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtReleaseTest {

    /**
     * An empty JSON string.
     */
    private static final String EMPTY_JSON = "{}";

    @Test
    void editsReleaseThroughPatch() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtReleaseTest.EMPTY_JSON
                )
            ).start(RandomPort.port())
        ) {
            RtReleaseTest.release(container.home()).patch(
                Json.createObjectBuilder().add("tag_name", "v1.0.0").build()
            );
            MatcherAssert.assertThat(
                "Release is not edited through PATCH",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
        }
    }

    @Test
    void sendsEditedRelease() throws IOException {
        final JsonObject json = Json.createObjectBuilder()
            .add("tag_name", "v1.0.0")
            .build();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtReleaseTest.EMPTY_JSON
                )
            ).start(RandomPort.port())
        ) {
            RtReleaseTest.release(container.home()).patch(json);
            MatcherAssert.assertThat(
                "Edited release is not sent",
                container.take().body(),
                Matchers.equalTo(json.toString())
            );
        }
    }

    @Test
    void deletesRelease() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NO_CONTENT,
                    RtReleaseTest.EMPTY_JSON
                )
            ).start(RandomPort.port())
        ) {
            RtReleaseTest.release(container.home()).delete();
            MatcherAssert.assertThat(
                "Release is not deleted through DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void executesPatchRequest() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtReleaseTest.EMPTY_JSON
                )
            ).start(RandomPort.port())
        ) {
            RtReleaseTest.release(container.home()).patch(
                Json.createObjectBuilder().add("name", "v1").build()
            );
            MatcherAssert.assertThat(
                "Request is not sent through PATCH",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
        }
    }

    /**
     * Create a test release.
     * @param uri REST API entry point
     * @return A test release
     */
    private static RtRelease release(final URI uri) {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("tstuser").when(coords).user();
        Mockito.doReturn("tstbranch").when(coords).repo();
        return new RtRelease(
            new ApacheRequest(uri),
            repo,
            2
        );
    }
}
