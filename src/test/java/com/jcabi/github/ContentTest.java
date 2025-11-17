/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Content}.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ContentTest {
    /**
     * Content.Smart can fetch type property from Content.
     */
    @Test
    public final void fetchesType() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final String prop = "this is some type";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("type", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            new Content.Smart(content).type(),
            Matchers.is(prop)
        );
    }

    /**
     * Content.Smart can fetch size property from Content.
     */
    @Test
    public final void fetchesSize() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final int prop = 5555;
        Mockito.doReturn(
            Json.createObjectBuilder()
                // @checkstyle MagicNumber (1 line)
                .add("size", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            new Content.Smart(content).size(),
            // @checkstyle MagicNumber (1 line)
            Matchers.is(prop)
        );
    }

    /**
     * Content.Smart can fetch name property from Content.
     */
    @Test
    public final void fetchesName() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final String prop = "this is some name";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            new Content.Smart(content).name(),
            Matchers.is(prop)
        );
    }

    /**
     * Content.Smart can fetch path property from Content.
     */
    @Test
    public final void fetchesPath() {
        final Content content = Mockito.mock(Content.class);
        final String path = "this is some path";
        Mockito.doReturn(path).when(content).path();
        MatcherAssert.assertThat(
            new Content.Smart(content).path(),
            Matchers.is(path)
        );
    }

    /**
     * Content.Smart can fetch sha property from Content.
     */
    @Test
    public final void fetchesSha() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final String prop = "this is some sha";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("sha", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            new Content.Smart(content).sha(),
            Matchers.is(prop)
        );
    }

    /**
     * Content.Smart can fetch url property from Content.
     */
    @Test
    public final void fetchesUrl() throws IOException, MalformedURLException, URISyntaxException {
        final Content content = Mockito.mock(Content.class);
        // @checkstyle LineLength (1 line)
        final String prop = "https://api.github.com/repos/pengwynn/octokit/contents/README.md";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            new Content.Smart(content).url(),
            Matchers.is(new URI(prop).toURL())
        );
    }

    /**
     * Content.Smart can fetch git_url property from Content.
     */
    @Test
    public final void fetchesGitUrl() throws IOException, MalformedURLException, URISyntaxException {
        final Content content = Mockito.mock(Content.class);
        // @checkstyle LineLength (1 line)
        final String prop = "https://api.github.com/repos/pengwynn/octokit/git/blobs/3d21ec53a331a6f037a91c368710b99387d012c1";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("git_url", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            new Content.Smart(content).gitUrl(),
            Matchers.is(new URI(prop).toURL())
        );
    }

    /**
     * Content.Smart can fetch html_url property from Content.
     */
    @Test
    public final void fetchesHtmlUrl() throws IOException, MalformedURLException, URISyntaxException {
        final Content content = Mockito.mock(Content.class);
        // @checkstyle LineLength (1 line)
        final String prop = "https://github.com/pengwynn/octokit/blob/master/README.md";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("html_url", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            new Content.Smart(content).htmlUrl(),
            Matchers.is(new URI(prop).toURL())
        );
    }

    /**
     * Content.Smart can fetch encoded content.
     */
    @Test
    public final void fetchesContent() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final String prop = "dGVzdCBlbmNvZGU=";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("content", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            new Content.Smart(content).content(),
            Matchers.is(prop)
        );
    }

    /**
     * Content.Smart can fetch decoded content.
     */
    @Test
    public final void fetchesDecoded() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final String prop = "dGVzdCBlbmNvZGXigqw=";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("content", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            new String(
                new Content.Smart(content).decoded(), StandardCharsets.UTF_8
            ),
            Matchers.is("test encode\u20ac")
        );
    }

    /**
     * Content.Smart can get underlying repo.
     */
    @Test
    public final void smartCanGetUnderlyingRepo() {
        final Content content = Mockito.mock(Content.class);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(repo).when(content).repo();
        MatcherAssert.assertThat(
            new Content.Smart(content).repo(),
            Matchers.is(repo)
        );
    }
}
