/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtReleaseAssets}.
 *
 * @since 0.8
 */
final class RtReleaseAssetsTest {

    /**
     * RtRelease can list assets for a release.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    void listReleaseAssets() throws Exception {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            new RtReleaseAssets(
                new FakeRequest().withStatus(HttpURLConnection.HTTP_OK)
                    .withBody("[{\"id\":1}, {\"id\":2}]"), RtReleaseAssetsTest.release()
            ).iterate(),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * RtRelease can upload a release asset.
     *
     * @throws Exception If something goes wrong
     */
    @Test
    void uploadReleaseAsset() throws Exception {
        final String body = "{\"id\":1}";
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtReleaseAssets(
                new FakeRequest().withStatus(HttpURLConnection.HTTP_CREATED)
                    .withBody(body),
                RtReleaseAssetsTest.release()
            ).upload(body.getBytes(StandardCharsets.UTF_8), "text/plain", "hello.txt")
                .number(),
            Matchers.is(1)
        );
    }

    /**
     * RtReleaseAssets can upload to the upload URL of the release.
     *
     * @throws Exception If something goes wrong
     */
    @Test
    void uploadsToUrlOfRelease() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED, "{\"id\":7}"
                )
            ).start(RandomPort.port())
        ) {
            final Release release = RtReleaseAssetsTest.release();
            Mockito.doReturn(
                Json.createObjectBuilder().add(
                    "upload_url",
                    String.format(
                        "%s/api/uploads/repos/a/b/releases/1/assets{?name,label}",
                        container.home()
                    )
                ).build()
            ).when(release).json();
            new RtReleaseAssets(
                new JdkRequest("http://localhost:1"), release
            ).upload(new byte[] {1}, "text/plain", "hello.txt");
            MatcherAssert.assertThat(
                "Upload did not go to the upload URL of the release",
                container.take().uri().toString(),
                Matchers.endsWith("/api/uploads/repos/a/b/releases/1/assets?name=hello.txt")
            );
        }
    }

    /**
     * RtRelease can get a single release asset.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    void fetchesReleaseAsset() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtReleaseAssets(
                new FakeRequest().withStatus(HttpURLConnection.HTTP_OK)
                    .withBody("{\"id\":3}"),
                RtReleaseAssetsTest.release()
            ).get(3).number(),
            Matchers.is(3)
        );
    }

    private static Release release() throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(new MkGitHub("john").randomRepo()).when(release).repo();
        Mockito.doReturn(1).when(release).number();
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("upload_url", "http://localhost/assets{?name,label}")
                .build()
        ).when(release).json();
        return release;
    }
}
