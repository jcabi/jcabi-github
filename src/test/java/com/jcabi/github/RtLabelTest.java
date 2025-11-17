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
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtLabel}.
 *
 */
public final class RtLabelTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtLabel can  can fetch HTTP request and describe response as a JSON.
     *
     */
    @Test
    public void sendHttpRequestAndWriteResponseAsJson() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"msg\": \"hi\"}"
                )
            ).start(this.resource.port())
        ) {
            final RtLabel label = new RtLabel(
                new ApacheRequest(container.home()),
                RtLabelTest.repo(),
                "bug"
            );
            MatcherAssert.assertThat(
                label.json().getString("msg"),
                Matchers.equalTo("hi")
            );
            container.stop();
        }
    }

    /**
     * GhLabel can execute PATCH request.
     *
     */
    @Test
    public void executePatchRequest() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"msg\":\"hi\"}"
                )
            ).start(this.resource.port())
        ) {
            final RtLabel label = new RtLabel(
                new ApacheRequest(container.home()),
                RtLabelTest.repo(),
                "enhance"
            );
            label.patch(
                Json.createObjectBuilder()
                    .add("content", "hi you!")
                    .build()
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
            container.stop();
        }
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
}
