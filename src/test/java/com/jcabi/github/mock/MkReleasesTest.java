/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Release;
import com.jcabi.github.Releases;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkReleases}.
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (300 lines)
 */
public final class MkReleasesTest {
    @Test
    public void canFetchEmptyListOfReleases() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        MatcherAssert.assertThat(
            "Collection is not empty",
            releases.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkReleases can fetch non-empty list of releases.
     */
    @Test
    public void canFetchNonEmptyListOfReleases() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        final String tag = "v1.0";
        releases.create(tag);
        MatcherAssert.assertThat(
            "Values are not equal",
            // @checkstyle MultipleStringLiterals (1 line)
            releases.iterate().iterator().next().json().getString("tag_name"),
            Matchers.equalTo(tag)
        );
    }

    @Test
    public void canFetchSingleRelease() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        MatcherAssert.assertThat(
            "Value is null", releases.get(1), Matchers.notNullValue());
    }

    @Test
    public void canCreateRelease() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        final String tag = "v1.0.0";
        final Release release = releases.create(tag);
        MatcherAssert.assertThat(
            "Values are not equal",
            release.json().getString("tag_name"),
            Matchers.equalTo(tag)
        );
    }

    @Test
    public void iteratesReleases() throws IOException {
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
    public void canRemoveRelease() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        releases.create("v1.1.1");
        releases.create("v1.1.2");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            releases.iterate(),
            Matchers.iterableWithSize(2)
        );
        releases.remove(1);
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            releases.iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    public void findsReleaseByTag() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        final String tag = "v5.0";
        releases.create(tag);
        MatcherAssert.assertThat(
            "Values are not equal",
            new Releases.Smart(releases).exists(tag),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(new Releases.Smart(releases).find(tag)).tag(),
            Matchers.equalTo(tag)
        );
    }

    /**
     * The release's name should be empty upon initial creation.
     */
    @Test
    public void releaseNameIsEmpty() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        final String tag = "tag";
        releases.create(tag);
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
    public void releaseBodyIsEmpty() throws IOException {
        final Releases releases = new MkGitHub().randomRepo().releases();
        final String tag = "tag";
        releases.create(tag);
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(releases.iterate().iterator().next())
                .body().isEmpty(),
            Matchers.is(true)
        );
    }
}
