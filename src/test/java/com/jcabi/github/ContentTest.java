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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Content}.
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
final class ContentTest {
    @Test
    void fetchesType() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final String prop = "this is some type";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("type", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Content.Smart(content).type(),
            Matchers.is(prop)
        );
    }

    @Test
    void fetchesSize() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final int prop = 5555;
        Mockito.doReturn(
            Json.createObjectBuilder()
                // @checkstyle MagicNumber (1 line)
                .add("size", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Content.Smart(content).size(),
            // @checkstyle MagicNumber (1 line)
            Matchers.is(prop)
        );
    }

    @Test
    void fetchesName() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final String prop = "this is some name";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Content.Smart(content).name(),
            Matchers.is(prop)
        );
    }

    @Test
    void fetchesPath() {
        final Content content = Mockito.mock(Content.class);
        final String path = "this is some path";
        Mockito.doReturn(path).when(content).path();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Content.Smart(content).path(),
            Matchers.is(path)
        );
    }

    @Test
    void fetchesSha() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final String prop = "this is some sha";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("sha", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Content.Smart(content).sha(),
            Matchers.is(prop)
        );
    }

    @Test
    void fetchesUrl()
        throws IOException, MalformedURLException, URISyntaxException {
        final Content content = Mockito.mock(Content.class);
        final String prop = String.join(
            "",
            "https://api.github.com/repos/pengwynn/",
            "octokit/contents/README.md"
        );
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Content.Smart(content).url(),
            Matchers.is(new URI(prop).toURL())
        );
    }

    @Test
    void fetchesGitUrl()
        throws IOException, MalformedURLException, URISyntaxException {
        final Content content = Mockito.mock(Content.class);
        final String prop = String.join(
            "",
            "https://api.github.com/repos/pengwynn/octokit/git/blobs/",
            "3d21ec53a331a6f037a91c368710b99387d012c1"
        );
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("git_url", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Content.Smart(content).gitUrl(),
            Matchers.is(new URI(prop).toURL())
        );
    }

    @Test
    void fetchesHtmlUrl()
        throws IOException, MalformedURLException, URISyntaxException {
        final Content content = Mockito.mock(Content.class);
        final String prop = String.join(
            "",
            "https://github.com/pengwynn/octokit/",
            "blob/master/README.md"
        );
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("html_url", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Content.Smart(content).htmlUrl(),
            Matchers.is(new URI(prop).toURL())
        );
    }

    @Test
    void fetchesContent() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final String prop = "dGVzdCBlbmNvZGU=";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("content", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Content.Smart(content).content(),
            Matchers.is(prop)
        );
    }

    @Test
    void fetchesDecoded() throws IOException {
        final Content content = Mockito.mock(Content.class);
        final String prop = "dGVzdCBlbmNvZGXigqw=";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("content", prop)
                .build()
        ).when(content).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new String(
                new Content.Smart(content).decoded(), StandardCharsets.UTF_8
            ),
            Matchers.is("test encode\u20ac")
        );
    }

    @Test
    void smartCanGetUnderlyingRepo() {
        final Content content = Mockito.mock(Content.class);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(repo).when(content).repo();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Content.Smart(content).repo(),
            Matchers.is(repo)
        );
    }
}
