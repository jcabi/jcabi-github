/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

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
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtJson}.
 * @since 0.1
 */
public final class RtJsonTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    @Test
    public void sendHttpRequest() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"body\":\"hi\"}"
                )
            ).start(RandomPort.port())
        ) {
            final RtJson json = new RtJson(new ApacheRequest(container.home()));
            MatcherAssert.assertThat(
                "Values are not equal",
                json.fetch().getString("body"),
                Matchers.equalTo("hi")
            );
            container.stop();
        }
    }

    @Test
    public void executePatchRequest() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"body\":\"hj\"}"
                )
            ).start(RandomPort.port())
        ) {
            final RtJson json = new RtJson(new ApacheRequest(container.home()));
            json.patch(
                Json.createObjectBuilder()
                    .add("content", "hi you!")
                    .build()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().method(),
                Matchers.equalTo("PATCH")
            );
            container.stop();
        }
    }
}
