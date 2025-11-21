/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.text.ParseException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Release}.
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (400 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
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
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.url().toString(),
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
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.htmlUrl().toString(),
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
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.assetsUrl().toString(),
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
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.uploadUrl().toString(),
            Matchers.equalTo(uploadurl)
        );
    }

    @Test
    void testId() {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(1).when(release).number();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.number(),
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
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.tag(),
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
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.commitish(),
            Matchers.equalTo(master)
        );
    }

    @Test
    void fetchName() throws IOException {
        final Release release = Mockito.mock(Release.class);
        final String name = "v1";
        // @checkstyle MultipleStringLiterals (3 lines)
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", name)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.hasName(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.name(),
            Matchers.equalTo(name)
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
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.hasName(),
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
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.body(),
            Matchers.equalTo(description)
        );
    }

    @Test
    void fetchDescription() throws IOException, ParseException {
        final Release release = Mockito.mock(Release.class);
        final String created = "2013-02-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("created_at", created)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.createdAt(),
            Matchers.equalTo(new GitHub.Time(created).date())
        );
    }

    @Test
    void fetchPublished() throws IOException, ParseException {
        final Release release = Mockito.mock(Release.class);
        final String published = "2013-01-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("published_at", published)
                .build()
        ).when(release).json();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.publishedAt(),
            Matchers.equalTo(new GitHub.Time(published).date())
        );
    }

    @Test
    void isPrerelease() throws IOException {
        final Release release = Mockito.mock(Release.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("prerelease", Boolean.TRUE).build()
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
            Json.createObjectBuilder().add("draft", Boolean.TRUE).build()
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
            Json.createObjectBuilder().add("draft", Boolean.FALSE).build()
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
}
