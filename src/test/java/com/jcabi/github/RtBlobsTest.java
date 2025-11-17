/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
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
import java.net.HttpURLConnection;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtBlobs}.
 *
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (200 lines)
 */
public final class RtBlobsTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtBlobs can create a blob.
     *
     * @throws Exception if some problem inside
     */
    @Test
    public void canCreateBlob() throws Exception {
        final String content = "Content of the blob";
        final String body = blob().toString();
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body))
            .start(this.resource.port())) {
            final RtBlobs blobs = new RtBlobs(
                new ApacheRequest(container.home()),
                repo()
            );
            final Blob blob = blobs.create(content, "utf-8");
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                new Blob.Smart(blob).url(),
                Matchers.equalTo("http://localhost/1")
            );
        }
    }

    /**
     * RtBlobs can get blob.
     *
     */
    @Test
    public void getBlob() {
        final String sha = "6dcb09b5b57875f334f61aebed695e2e4193db52";
        final Blobs blobs = new RtBlobs(
            new FakeRequest().withBody(
                Json.createObjectBuilder()
                    .add("sha", sha)
                    .build()
                    .toString()
            ),
            repo()
        );
        MatcherAssert.assertThat(blobs.get(sha).sha(), Matchers.equalTo(sha));
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
     * @checkstyle MagicNumberCheck (10 lines)
     */
    private static JsonObject blob() {
        return Json.createObjectBuilder()
            .add("url", "http://localhost/1")
            .add("sha", RandomStringUtils.random(40, "0123456789abcdef"))
            .build();
    }
}
