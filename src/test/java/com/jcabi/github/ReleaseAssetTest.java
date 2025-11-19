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
import java.text.ParseException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link ReleaseAsset}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (150 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class ReleaseAssetTest {

    @Test
    public final void fetchesUrl() throws IOException, MalformedURLException, URISyntaxException {
        final ReleaseAsset asset = Mockito.mock(ReleaseAsset.class);
        // @checkstyle LineLength (1 line)
        final String prop = "https://api.github.com/repos/octo/Hello/releases/assets/1";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("url", prop)
                .build()
        ).when(asset).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new ReleaseAsset.Smart(asset).url(),
            Matchers.is(new URI(prop).toURL())
        );
    }

    @Test
    public final void fetchesName() throws IOException {
        final ReleaseAsset asset = Mockito.mock(ReleaseAsset.class);
        final String prop = "assetname.ext";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", prop)
                .build()
        ).when(asset).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new ReleaseAsset.Smart(asset).name(),
            Matchers.is(prop)
        );
    }

    @Test
    public final void fetchesLabel() throws IOException {
        final ReleaseAsset asset = Mockito.mock(ReleaseAsset.class);
        final String prop = "short description";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("label", prop)
                .build()
        ).when(asset).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new ReleaseAsset.Smart(asset).label(),
            Matchers.is(prop)
        );
    }

    @Test
    public final void fetchesState() throws IOException {
        final ReleaseAsset asset = Mockito.mock(ReleaseAsset.class);
        final String prop = "uploaded";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("state", prop)
                .build()
        ).when(asset).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new ReleaseAsset.Smart(asset).state(),
            Matchers.is(prop)
        );
    }

    @Test
    public final void fetchesContentType() throws IOException {
        final ReleaseAsset asset = Mockito.mock(ReleaseAsset.class);
        final String prop = "application/zip";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("content_type", prop)
                .build()
        ).when(asset).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new ReleaseAsset.Smart(asset).contentType(),
            Matchers.is(prop)
        );
    }

    @Test
    public final void fetchesSize() throws IOException {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final int prop = 1024;
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("size", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new ReleaseAsset.Smart(releaseAsset).size(),
            Matchers.is(prop)
        );
    }

    @Test
    public final void fetchesDownloadCount() throws IOException {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final int prop = 42;
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("download_count", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new ReleaseAsset.Smart(releaseAsset).downloadCount(),
            Matchers.is(prop)
        );
    }

    @Test
    public final void fetchesCreatedAt() throws IOException, ParseException {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "2013-02-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("created_at", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new ReleaseAsset.Smart(releaseAsset).createdAt(),
            Matchers.equalTo(new GitHub.Time(prop).date())
        );
    }

    @Test
    public final void fetchesUpdatedAt() throws IOException, ParseException {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "2013-02-27T19:35:32Z";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("updated_at", prop)
                .build()
        ).when(releaseAsset).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new ReleaseAsset.Smart(releaseAsset).updatedAt(),
            Matchers.equalTo(new GitHub.Time(prop).date())
        );
    }

    @Test
    public final void updatesName() throws IOException {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "new_name";
        new ReleaseAsset.Smart(releaseAsset).name(prop);
        Mockito.verify(releaseAsset).patch(
            Json.createObjectBuilder().add("name", prop).build()
        );
    }

    @Test
    public final void updatesLabel() throws IOException {
        final ReleaseAsset releaseAsset = Mockito.mock(ReleaseAsset.class);
        final String prop = "new_label";
        new ReleaseAsset.Smart(releaseAsset).label(prop);
        Mockito.verify(releaseAsset).patch(
            Json.createObjectBuilder().add("label", prop).build()
        );
    }
}
