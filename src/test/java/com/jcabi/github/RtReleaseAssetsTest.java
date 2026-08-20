/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtReleaseAssets}.
 * @since 0.8
 */
final class RtReleaseAssetsTest {

    /**
     * RtRelease can list assets for a release.
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
     * RtRelease can get a single release asset.
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
        return release;
    }
}
