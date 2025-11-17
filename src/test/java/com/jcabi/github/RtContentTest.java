/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
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
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtContent}.
 * @since 0.8
 */
@Immutable
public final class RtContentTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtContent should be able to describe itself in JSON format.
     *
     */
    @Test
    public void fetchContentAsJson() throws IOException {
        final RtContent content = new RtContent(
            new FakeRequest().withBody("{\"content\":\"json\"}"),
            this.repo(),
            "blah"
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            content.json().getString("content"),
            Matchers.equalTo("json")
        );
    }

    /**
     * RtContent should be able to perform a patch request.
     *
     */
    @Test
    public void patchWithJson() throws IOException {
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "response")
        ).start(this.resource.port())) {
            final RtContent content = new RtContent(
                new ApacheRequest(container.home()),
                this.repo(),
                "path"
            );
            content.patch(
                Json.createObjectBuilder().add("patch", "test").build()
            );
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.PATCH)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                query.body(),
                Matchers.equalTo("{\"patch\":\"test\"}")
            );
        }
    }

    /**
     * RtContent should be able to compare different instances.
     *
     */
    @Test
    public void canCompareInstances() {
        final RtContent less = new RtContent(
            new FakeRequest(),
            this.repo(),
            "aaa"
        );
        final RtContent greater = new RtContent(
            new FakeRequest(),
            this.repo(),
            "zzz"
        );
        MatcherAssert.assertThat(
            "Value is not less than expected",
            less.compareTo(greater), Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            greater.compareTo(less), Matchers.greaterThan(0)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            greater.compareTo(greater), Matchers.is(0)
        );
    }

    /**
     * RtContent should be able to fetch raw content.
     *
     */
    @Test
    public void fetchesRawContent() throws IOException {
        final String raw = "the raw \u20ac";
        try (final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, raw)
        ).start(this.resource.port())) {
            final InputStream stream = new RtContent(
                new ApacheRequest(container.home()),
                this.repo(),
                "raw"
            ).raw();
            MatcherAssert.assertThat(
                "Values are not equal",
                IOUtils.toString(stream, StandardCharsets.UTF_8),
                Matchers.is(raw)
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                container.take().headers().get(HttpHeaders.ACCEPT).get(0),
                Matchers.is("application/vnd.github.v3.raw")
            );
        }
    }

    /**
     * Mock repo for GhIssue creation.
     * @return The mock repo.
     */
    private Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        final Coordinates coords = Mockito.mock(Coordinates.class);
        Mockito.doReturn(coords).when(repo).coordinates();
        Mockito.doReturn("user").when(coords).user();
        Mockito.doReturn("repo").when(coords).repo();
        return repo;
    }
}
