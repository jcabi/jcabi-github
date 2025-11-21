/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtRelease}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
public final class RtReleaseTest {
    /**
     * An empty JSON string.
     */
    private static final String EMPTY_JSON = "{}";

    /**
     * A mock container used in test to mimic the GitHub server.
     */
    private transient MkContainer container;

    /**
     * Setting up the test fixture.
     */
    @BeforeEach
    public void setUp() {
        this.container = new MkGrizzlyContainer();
    }

    /**
     * Tear down the test fixture to return to the original state.
     */
    @AfterEach
    public void tearDown() {
        this.container.stop();
    }

    @Test
    public void editRelease() throws IOException {
        this.container.next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, RtReleaseTest.EMPTY_JSON)
        ).start(RandomPort.port());
        final RtRelease release = RtReleaseTest.release(this.container.home());
        final JsonObject json = Json.createObjectBuilder()
            .add("tag_name", "v1.0.0")
            .build();
        release.patch(json);
        final MkQuery query = this.container.take();
        MatcherAssert.assertThat(
            "Values are not equal",
            query.method(),
            Matchers.equalTo(Request.PATCH)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            query.body(),
            Matchers.equalTo(json.toString())
        );
    }

    @Test
    public void deleteRelease() throws IOException {
        this.container.next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, RtReleaseTest.EMPTY_JSON)
        ).start(RandomPort.port());
        final RtRelease release = RtReleaseTest.release(this.container.home());
        release.delete();
        MatcherAssert.assertThat(
            "Values are not equal",
            this.container.take().method(),
            Matchers.equalTo(Request.DELETE)
        );
    }

    @Test
    public void executePatchRequest() throws IOException {
        this.container.next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, RtReleaseTest.EMPTY_JSON)
        ).start(RandomPort.port());
        final RtRelease release = RtReleaseTest.release(this.container.home());
        release.patch(Json.createObjectBuilder().add("name", "v1")
            .build()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            this.container.take().method(),
            Matchers.equalTo(Request.PATCH)
        );
    }

    /**
     * Create a test release.
     * @param uri REST API entry point.
     * @return A test release.
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
