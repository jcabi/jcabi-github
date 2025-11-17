/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.net.HttpURLConnection;
import jakarta.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtJson}.
 *
 */
public final class RtJsonTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtJson can fetch HTTP request.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void sendHttpRequest() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"body\":\"hi\"}"
                )
            ).start(this.resource.port())
        ) {
            final RtJson json = new RtJson(new ApacheRequest(container.home()));
            MatcherAssert.assertThat(
                json.fetch().getString("body"),
                Matchers.equalTo("hi")
            );
            container.stop();
        }
    }

    /**
     * RtJson can execute PATCH request.
     *
     * @throws Exception if there is any problem
     */
    @Test
    public void executePatchRequest() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    "{\"body\":\"hj\"}"
                )
            ).start(this.resource.port())
        ) {
            final RtJson json = new RtJson(new ApacheRequest(container.home()));
            json.patch(
                Json.createObjectBuilder()
                    .add("content", "hi you!")
                    .build()
            );
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo("PATCH")
            );
            container.stop();
        }
    }
}
