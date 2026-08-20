/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtMarkdown}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtMarkdownTest {

    @Test
    void returnsJsonOutput() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtMarkdownTest.answer("{\"a\":\"b\"}"))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Rendered markdown is different",
                RtMarkdownTest.markdown(container).render(
                    Json.createObjectBuilder().add("hello", "world").build()
                ),
                Matchers.equalTo("{\"a\":\"b\"}")
            );
        }
    }

    @Test
    void sendsJsonInput() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtMarkdownTest.answer("{\"a\":\"b\"}"))
                .start(RandomPort.port())
        ) {
            RtMarkdownTest.markdown(container).render(
                Json.createObjectBuilder().add("hello", "world").build()
            );
            MatcherAssert.assertThat(
                "Markdown to render is not sent",
                container.take().body(),
                Matchers.equalTo("{\"hello\":\"world\"}")
            );
        }
    }

    @Test
    void returnsRawOutput() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtMarkdownTest.answer("Test Output"))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Rendered raw markdown is different",
                RtMarkdownTest.markdown(container).raw("Hello World!"),
                Matchers.equalTo("Test Output")
            );
        }
    }

    @Test
    void sendsRawInput() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtMarkdownTest.answer("Test Output"))
                .start(RandomPort.port())
        ) {
            RtMarkdownTest.markdown(container).raw("Hello World!");
            MatcherAssert.assertThat(
                "Raw markdown to render is not sent",
                container.take().body(),
                Matchers.equalTo("Hello World!")
            );
        }
    }

    private static RtMarkdown markdown(final MkContainer container)
        throws IOException {
        return new RtMarkdown(
            new MkGitHub(),
            new ApacheRequest(container.home())
        );
    }

    private static MkAnswer answer(final String body) {
        return new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body)
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML);
    }
}
