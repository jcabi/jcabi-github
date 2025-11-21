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
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
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
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (3 lines)
 */
@ExtendWith(RandomPort.class)
final class RtReleaseAssetTest {

    /**
     * RtReleaseAsset can be described in JSON form.
     * @throws Exception if a problem occurs.
     */
    @Test
    void canRepresentAsJson() throws Exception {
        final RtReleaseAsset asset = new RtReleaseAsset(
            new FakeRequest().withBody("{\"asset\":\"release\"}"),
            RtReleaseAssetTest.release(),
            1
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            asset.json().getString("asset"),
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
        final RtReleaseAsset asset = new RtReleaseAsset(
            new FakeRequest(),
            release,
            1
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            asset.release(),
            Matchers.is(release)
        );
    }

    /**
     * RtReleaseAsset can create a patch request.
     * @throws Exception If a problem occurs.
     */
    @Test
    void patchesAsset() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            final RtReleaseAsset asset = new RtReleaseAsset(
                new ApacheRequest(container.home()),
                RtReleaseAssetTest.release(),
                2
            );
            final JsonObject json = Json.createObjectBuilder()
                .add("name", "hello").build();
            asset.patch(json);
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.PATCH)
            );
            MatcherAssert.assertThat(
                "String does not contain expected value",
                query.body(),
                Matchers.containsString("{\"name\":\"hello\"}")
            );
            MatcherAssert.assertThat(
                "String does not end with expected value",
                query.uri().toString(),
                Matchers.endsWith("/repos/john/blueharvest/releases/assets/2")
            );
            container.stop();
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
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            container.stop();
        }
    }

    /**
     * RtReleaseAsset can stream raw content.
     * @throws Exception If a problem occurs.
     */
    @Test
    void rawAsset() throws Exception {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "")
            ).start(RandomPort.port())
        ) {
            final RtReleaseAsset asset = new RtReleaseAsset(
                new ApacheRequest(container.home()),
                RtReleaseAssetTest.release(),
                4
            );
            try (InputStream stream = asset.raw()) {
                final MkQuery query = container.take();
                MatcherAssert.assertThat(
                    "Values are not equal",
                    query.method(),
                    Matchers.equalTo(Request.GET)
                );
                MatcherAssert.assertThat(
                    "Value is null",
                    IOUtils.toString(stream, StandardCharsets.UTF_8),
                    Matchers.notNullValue()
                );
            }
            container.stop();
        }
    }

    /**
     * This method returns a Release for testing.
     * @return Release to be used for test.
     */
    private static Release release() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final Repo repo = new MkGitHub("john").repos().create(
            new Repos.RepoCreate("blueharvest", false)
        );
        Mockito.doReturn(repo).when(release).repo();
        Mockito.doReturn(1).when(release).number();
        return release;
    }
}
