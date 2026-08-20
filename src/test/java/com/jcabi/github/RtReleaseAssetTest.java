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
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtReleaseAsset}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtReleaseAssetTest {

    /**
     * RtReleaseAsset can be described in JSON form.
     * @throws Exception if a problem occurs.
     */
    @Test
    void canRepresentAsJson() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtReleaseAsset(
                new FakeRequest().withBody("{\"asset\":\"release\"}"),
                RtReleaseAssetTest.release(),
                1
            ).json().getString("asset"),
            Matchers.equalTo("release")
        );
    }

    /**
     * RtReleaseAsset can obtain its own release.
     * @throws Exception if a problem occurs.
     */
    @Test
    void canObtainOwnRelease() throws Exception {
        final Release release = RtReleaseAssetTest.release();
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtReleaseAsset(
                new FakeRequest(),
                release,
                1
            ).release(),
            Matchers.is(release)
        );
    }

    @Test
    void patchesAssetThroughPatchMethod() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            RtReleaseAssetTest.patch(container);
            MatcherAssert.assertThat(
                "Asset is not patched through PATCH",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
        }
    }

    @Test
    void sendsPatchInRequestBody() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            RtReleaseAssetTest.patch(container);
            MatcherAssert.assertThat(
                "Patch is not sent in the request body",
                container.take().body(),
                Matchers.containsString("{\"name\":\"hello\"}")
            );
        }
    }

    @Test
    void patchesAssetAtCorrectUri() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            RtReleaseAssetTest.patch(container);
            MatcherAssert.assertThat(
                "Asset is patched at a wrong URI",
                container.take().uri().toString(),
                Matchers.endsWith("/repos/john/blueharvest/releases/assets/2")
            );
        }
    }

    /**
     * RtReleaseAsset can remove itself.
     * @throws Exception If a problem occurs.
     */
    @Test
    void removesAsset() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
            ).start(RandomPort.port())
        ) {
            final RtReleaseAsset asset = new RtReleaseAsset(
                new ApacheRequest(container.home()),
                RtReleaseAssetTest.release(),
                3
            );
            asset.remove();
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }

    @Test
    void streamsRawAssetWithGet() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            RtReleaseAssetTest.raw(container).close();
            MatcherAssert.assertThat(
                "Raw asset is not streamed with GET",
                container.take().method(),
                Matchers.equalTo(Request.GET)
            );
        }
    }

    @Test
    void streamsRawAsset() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            try (InputStream stream = RtReleaseAssetTest.raw(container)) {
                MatcherAssert.assertThat(
                    "Raw asset is not streamed",
                    IOUtils.toString(stream, StandardCharsets.UTF_8),
                    Matchers.notNullValue()
                );
            }
        }
    }

    private static void patch(final MkContainer container) throws IOException {
        new RtReleaseAsset(
            new ApacheRequest(container.home()),
            RtReleaseAssetTest.release(),
            2
        ).patch(Json.createObjectBuilder().add("name", "hello").build());
    }

    private static InputStream raw(final MkContainer container)
        throws IOException {
        return new RtReleaseAsset(
            new ApacheRequest(container.home()),
            RtReleaseAssetTest.release(),
            4
        ).raw();
    }

    private static Release release() throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            new MkGitHub("john").repos().create(
                new Repos.RepoCreate("blueharvest", false)
                )
        ).when(release).repo();
        Mockito.doReturn(1).when(release).number();
        return release;
    }
}
