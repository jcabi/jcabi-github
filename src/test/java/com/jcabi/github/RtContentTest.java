/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import jakarta.ws.rs.core.HttpHeaders;
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
 * Test case for {@link RtContent}.
 * @since 0.8
 */
@Immutable
@ExtendWith(RandomPort.class)
final class RtContentTest {

    @Test
    void fetchContentAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtContent(
                new FakeRequest().withBody("{\"content\":\"json\"}"),
                RtContentTest.repo(),
                "blah"
            ).json().getString("content"),
            Matchers.equalTo("json")
        );
    }

    @Test
    void patchesThroughPatchMethod() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
            ).start(RandomPort.port())
        ) {
            RtContentTest.patch(container);
            MatcherAssert.assertThat(
                "Content is not patched through PATCH",
                container.take().method(),
                Matchers.equalTo(Request.PATCH)
            );
        }
    }

    @Test
    void sendsPatchInRequestBody() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
            ).start(RandomPort.port())
        ) {
            RtContentTest.patch(container);
            MatcherAssert.assertThat(
                "Patch is not sent in the request body",
                container.take().body(),
                Matchers.equalTo("{\"patch\":\"test\"}")
            );
        }
    }

    @Test
    void comparesSmallerContent() {
        MatcherAssert.assertThat(
            "Content is not less than the greater one",
            RtContentTest.content("aaa").compareTo(
                RtContentTest.content("zzz")
            ),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerContent() {
        MatcherAssert.assertThat(
            "Content is not greater than the smaller one",
            RtContentTest.content("zzz").compareTo(
                RtContentTest.content("aaa")
            ),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void comparesEqualContents() {
        MatcherAssert.assertThat(
            "Equal contents are not the same",
            RtContentTest.content("zzz").compareTo(
                RtContentTest.content("zzz")
            ),
            Matchers.is(0)
        );
    }

    @Test
    void fetchesRawContent() throws IOException {
        final String raw = "the raw €";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, raw)
            ).start(RandomPort.port())
        ) {
            try (InputStream stream = RtContentTest.raw(container)) {
                MatcherAssert.assertThat(
                    "Raw content is different",
                    IOUtils.toString(stream, StandardCharsets.UTF_8),
                    Matchers.is(raw)
                );
            }
        }
    }

    @Test
    void asksForRawContent() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "raw")
            ).start(RandomPort.port())
        ) {
            RtContentTest.raw(container).close();
            MatcherAssert.assertThat(
                "Raw content is not asked for",
                container.take().headers().get(HttpHeaders.ACCEPT).get(0),
                Matchers.is("application/vnd.github.v3.raw")
            );
        }
    }

    /**
     * Patch the content served by the given container.
     * @param container Container to serve the content
     * @throws IOException If there is any I/O problem
     */
    private static void patch(final MkContainer container) throws IOException {
        new RtContent(
            new ApacheRequest(container.home()),
            RtContentTest.repo(),
            "path"
        ).patch(Json.createObjectBuilder().add("patch", "test").build());
    }

    /**
     * Raw content served by the given container.
     * @param container Container to serve the content
     * @return Stream of the raw content
     * @throws IOException If there is any I/O problem
     */
    private static InputStream raw(final MkContainer container)
        throws IOException {
        return new RtContent(
            new ApacheRequest(container.home()),
            RtContentTest.repo(),
            "raw"
        ).raw();
    }

    /**
     * Content with the given path.
     * @param path Path of the content
     * @return The content
     */
    private static RtContent content(final String path) {
        return new RtContent(
            new FakeRequest(),
            RtContentTest.repo(),
            path
        );
    }

    /**
     * Mock repo for GhIssue creation.
     * @return The mock repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("user").when(coords).user();
        Mockito.doReturn("repo").when(coords).repo();
        return repo;
    }
}
