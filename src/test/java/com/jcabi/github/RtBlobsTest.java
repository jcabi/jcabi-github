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
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtBlobs}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtBlobsTest {

    @Test
    void createsBlobWithPost() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_CREATED,
                    RtBlobsTest.blob().toString()
                )
            ).start(RandomPort.port())
        ) {
            new RtBlobs(
                new ApacheRequest(container.home()),
                RtBlobsTest.repo()
            ).create("Content of the blob", "utf-8");
            MatcherAssert.assertThat(
                "Blob is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsBlobWithUrl() throws IOException {
        final String body = RtBlobsTest.blob().toString();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created blob has a wrong URL",
                new Blob.Smart(
                    new RtBlobs(
                        new ApacheRequest(container.home()),
                        RtBlobsTest.repo()
                    ).create("Content of the blob", "utf-8")
                ).url(),
                Matchers.equalTo("http://localhost/1")
            );
        }
    }

    @Test
    void fetchesBlob() {
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db52";
        MatcherAssert.assertThat(
            "Values are not equal", new RtBlobs(
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add("sha", sha)
                        .build()
                        .toString()
                ),
                RtBlobsTest.repo()
            ).get(sha).sha(), Matchers.equalTo(sha)
        );
    }

    /**
     * Create and return repo to test.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }

    /**
     * Create and return JsonObject to test.
     * @return JsonObject
     */
    private static JsonObject blob() {
        return Json.createObjectBuilder()
            .add("url", "http://localhost/1")
            .add("sha", RandomStringUtils.secure().next(40, "0123456789abcdef"))
            .build();
    }
}
