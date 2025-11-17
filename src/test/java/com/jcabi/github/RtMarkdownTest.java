/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.json.Json;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.apache.http.HttpHeaders;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtMarkdown}.
 *
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
public final class RtMarkdownTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtMarkdown should be able to return JSON output.
     *
     */
    @Test
    public void returnsJsonOutput() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "{\"a\":\"b\"}")
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML)
            ).start(this.resource.port())
        ) {
            final RtMarkdown markdown = new RtMarkdown(
                new MkGitHub(),
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                markdown.render(
                    Json.createObjectBuilder().add("hello", "world").build()
                ),
                Matchers.equalTo("{\"a\":\"b\"}")
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().body(),
                Matchers.equalTo("{\"hello\":\"world\"}")
            );
            container.stop();
        }
    }

    /**
     * RtMarkdown should be able to return raw output.
     *
     */
    @Test
    public void returnsRawOutput() throws IOException {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "Test Output")
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML)
            ).start(this.resource.port())
        ) {
            final RtMarkdown markdown = new RtMarkdown(
                new MkGitHub(),
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                markdown.raw("Hello World!"),
                Matchers.equalTo("Test Output")
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().body(),
                Matchers.equalTo("Hello World!")
            );
            container.stop();
        }
    }

}
