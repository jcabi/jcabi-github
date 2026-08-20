/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import jakarta.json.JsonValue;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Release}.
 * @since 0.1
 */
final class ReleaseTest {

    @Test
    void fetchesUrls() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final String url = "http://url";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", url)
                .build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).url().toString(),
            Matchers.equalTo(url)
        );
    }

    @Test
    void fetchesHtmlUrls() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final String htmlurl = "http://html_url";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("html_url", htmlurl)
                .build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).htmlUrl().toString(),
            Matchers.equalTo(htmlurl)
        );
    }

    @Test
    void fetchesAssetsHtmlUrls() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final String assetsurl = "http://assets_url";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("assets_url", assetsurl)
                .build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).assetsUrl().toString(),
            Matchers.equalTo(assetsurl)
        );
    }

    @Test
    void fetchesUploadHtmlUrls() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final String uploadurl = "http://upload_url";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("upload_url", uploadurl)
                .build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).uploadUrl().toString(),
            Matchers.equalTo(uploadurl)
        );
    }

    @Test
    void fetchesId() {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(1).when(release).number();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).number(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void fetchTag() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final String tag = "v1.0.0";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("tag_name", tag)
                .build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).tag(),
            Matchers.equalTo(tag)
        );
    }

    @Test
    void fetchProperties() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final String master = "master";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("target_commitish", master)
                .build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).commitish(),
            Matchers.equalTo(master)
        );
    }

    @Test
    void indicatesName() throws IOException {
        MatcherAssert.assertThat(
            "Named release does not have a name",
            new Release.Smart(ReleaseTest.named("v1")).hasName(),
            Matchers.is(true)
        );
    }

    @Test
    void fetchName() throws IOException {
        MatcherAssert.assertThat(
            "Name of the release is not fetched",
            new Release.Smart(ReleaseTest.named("v1")).name(),
            Matchers.equalTo("v1")
        );
    }

    /**
     * Release.Smart can determine if the release does not have a name
     * (NULL json value).
     */
    @Test
    void indicatesNoName() throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", JsonValue.NULL)
                .build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).hasName(),
            Matchers.is(false)
        );
    }

    @Test
    void fetchBody() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final String description = "Description of the release";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("body", description)
                .build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).body(),
            Matchers.equalTo(description)
        );
    }

    @Test
    void fetchDescription() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final String created = "2013-02-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("created_at", created)
                .build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).createdAt(),
            Matchers.equalTo(new GitHub.Time(created).date())
        );
    }

    @Test
    void fetchPublished() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final String published = "2013-01-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("published_at", published)
                .build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).publishedAt(),
            Matchers.equalTo(new GitHub.Time(published).date())
        );
    }

    @Test
    void isPrerelease() throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("prerelease", true).build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).prerelease(),
            Matchers.is(Boolean.TRUE)
        );
    }

    @Test
    void isNotPrerelease() throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("prerelease", "false").build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).prerelease(),
            Matchers.is(Boolean.FALSE)
        );
    }

    @Test
    void missingPrerelease() throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).prerelease(),
            Matchers.is(Boolean.FALSE)
        );
    }

    @Test
    void isDraft() throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("draft", true).build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).draft(),
            Matchers.is(Boolean.TRUE)
        );
    }

    @Test
    void isNotDraft() throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("draft", false).build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).draft(),
            Matchers.is(Boolean.FALSE)
        );
    }

    @Test
    void missingDraft() throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().build()
        ).when(release).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).draft(),
            Matchers.is(Boolean.FALSE)
        );
    }

    private static Release named(final String name) throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", name)
                .build()
        ).when(release).json();
        return release;
    }
}
