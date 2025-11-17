/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.GitHub;
import com.jcabi.github.Release;
import com.jcabi.github.Releases;
import jakarta.json.Json;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkRelease}.
 *
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class MkReleaseTest {
    /**
     * Check if a release can be deleted.
     */
    @Test
    public void canDeleteRelease() throws IOException {
        final Releases releases = MkReleaseTest.releases();
        final Release release = releases.create("v1.0");
        release.delete();
        MatcherAssert.assertThat(
            "Values are not equal",
            releases.iterate().iterator().hasNext(),
            Matchers.is(false)
        );
    }

    /**
     * Smart decorator returns url.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetUrl() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.url().toString(),
            Matchers.equalTo(this.value(release, "url"))
        );
    }

    /**
     * Smart decorator returns assets url.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetAssetsUrl() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.assetsUrl().toString(),
            Matchers.equalTo(this.value(release, "assets_url"))
        );
    }

    /**
     * Smart decorator returns html url.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetHtmlUrl() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.htmlUrl().toString(),
            Matchers.equalTo(this.value(release, "html_url"))
        );
    }

    /**
     * Smart decorator returns upload url.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetUploadUrl() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.uploadUrl().toString(),
            Matchers.equalTo(this.value(release, "upload_url"))
        );
    }

    /**
     * Smart decorator returns tag name.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetTag() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.tag(),
            Matchers.equalTo(this.value(release, "tag_name"))
        );
    }

    /**
     * Smart decorator returns target commitish.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetCommitish() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.commitish(),
            Matchers.equalTo(this.value(release, "target_commitish"))
        );
    }

    /**
     * Smart decorator returns name.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetName() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.name(),
            Matchers.equalTo(this.value(release, "name"))
        );
    }

    /**
     * Smart decorator returns body.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetBody() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.body(),
            Matchers.equalTo(this.value(release, "body"))
        );
    }

    /**
     * Smart decorator returns created date.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetCreatedAt() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.createdAt(),
            Matchers.equalTo(new GitHub.Time(this.value(release, "created_at"))
                .date()
            )
        );
    }

    /**
     * Smart decorator returns prerelease.
     * @throws Exception If some problem inside
     */
    @Test
    public void prerelease() throws Exception {
        final Release release = MkReleaseTest.release();
        release.patch(
            Json.createObjectBuilder().add("prerelease", true).build()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(release).prerelease(),
            Matchers.is(true)
        );
    }

    /**
     * Smart decorator returns published date.
     * @throws Exception If some problem inside
     */
    @Test
    public void canGetPublishedAt() throws Exception {
        final Release release = MkReleaseTest.release();
        final Release.Smart smart = new Release.Smart(release);
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.publishedAt(),
            Matchers.equalTo(
                new GitHub.Time(this.value(release, "published_at")).date()
            )
        );
    }

    /**
     * Returns string property value.
     * @param release Release
     * @param name Property name
     * @return Value as a string
     * @throws IOException If some problem inside
     */
    private String value(final Release release, final String name)
        throws IOException {
        final JsonValue jsonValue = release.json().get(name);
        String result = null;
        if (jsonValue instanceof JsonString) {
            result = ((JsonString) jsonValue).getString();
        }
        return result;
    }

    /**
     * Create a release to work with.
     * @return Release
     */
    private static Release release() throws IOException {
        return MkReleaseTest.releases().create("v1");
    }

    /**
     * Creates a Releases instance set to work with.
     * @return A test Releases instance.
     * @throws IOException if any I/O problems occur.
     */
    private static Releases releases() throws IOException {
        return new MkGitHub().randomRepo().releases();
    }
}
