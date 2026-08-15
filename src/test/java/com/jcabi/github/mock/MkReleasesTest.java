/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Release;
import com.jcabi.github.Releases;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkReleases}.
 * @since 0.8
 */
final class MkReleasesTest {

    @Test
    void canFetchEmptyListOfReleases() throws IOException {
        MatcherAssert.assertThat(
            "Collection is not empty",
            new MkGitHub().randomRepo().releases().iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkReleases can fetch non-empty list of releases.
     */
    @Test
    void canFetchNonEmptyListOfReleases() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        final String tag = "v1.0";
        releases.create(tag);
        MatcherAssert.assertThat(
            "Values are not equal",
            releases.iterate().iterator().next().json().getString("tag_name"),
            Matchers.equalTo(tag)
        );
    }

    @Test
    void canFetchSingleRelease() throws IOException {
        MatcherAssert.assertThat(
            "Value is null", new MkGitHub().randomRepo().releases().get(1), Matchers.notNullValue()
        );
    }

    @Test
    void canCreateRelease() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        final String tag = "v1.0.0";
        MatcherAssert.assertThat(
            "Values are not equal",
            releases.create(tag).json().getString("tag_name"),
            Matchers.equalTo(tag)
        );
    }

    @Test
    void iteratesReleases() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        releases.create("v1.0.1");
        releases.create("v1.0.2");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            releases.iterate(),
            Matchers.iterableWithSize(2)
        );
    }

    @Test
    void canRemoveRelease() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        releases.create("v1.1.1");
        releases.create("v1.1.2");
        releases.remove(1);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            releases.iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void findsReleaseByTag() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        final String tag = "v5.0";
        releases.create(tag);
        MatcherAssert.assertThat(
            "Release is not found by its tag",
            new Releases.Smart(releases).exists(tag),
            Matchers.is(true)
        );
    }

    @Test
    void tagsReleaseFoundByTag() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        final String tag = "v5.0";
        releases.create(tag);
        MatcherAssert.assertThat(
            "Release found by its tag has a wrong tag",
            new Release.Smart(new Releases.Smart(releases).find(tag)).tag(),
            Matchers.equalTo(tag)
        );
    }

    /**
     * The release's name should be empty upon initial creation.
     */
    @Test
    void releaseNameIsEmpty() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        releases.create("tag");
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(releases.iterate().iterator().next())
                .name().isEmpty(),
            Matchers.is(true)
        );
    }

    /**
     * The release's body should be empty upon initial creation.
     */
    @Test
    void releaseBodyIsEmpty() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        releases.create("tag");
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(releases.iterate().iterator().next())
                .body().isEmpty(),
            Matchers.is(true)
        );
    }
}
