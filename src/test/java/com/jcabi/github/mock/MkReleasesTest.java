/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Release;
import com.jcabi.github.Releases;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkReleases}.
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (300 lines)
 */
public final class MkReleasesTest {
    /**
     * MkReleases can fetch empty list of releases.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchEmptyListOfReleases() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkReleases can fetch non-empty list of releases.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchNonEmptyListOfReleases() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        final String tag = "v1.0";
        releases.create(tag);
        MatcherAssert.assertThat(
            // @checkstyle MultipleStringLiterals (1 line)
            releases.iterate().iterator().next().json().getString("tag_name"),
            Matchers.equalTo(tag)
        );
    }

    /**
     * MkReleases can fetch a single release.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchSingleRelease() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        MatcherAssert.assertThat(releases.get(1), Matchers.notNullValue());
    }

    /**
     * MkReleases can create a release.
     * @throws Exception If some problem inside
     */
    @Test
    public void canCreateRelease() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        final String tag = "v1.0.0";
        final Release release = releases.create(tag);
        MatcherAssert.assertThat(
            release.json().getString("tag_name"),
            Matchers.equalTo(tag)
        );
    }

    /**
     * MkReleases can iterate through the releases.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void iteratesReleases() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        releases.create("v1.0.1");
        releases.create("v1.0.2");
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.<Release>iterableWithSize(2)
        );
    }

    /**
     * MkReleases can be removed.
     * @throws Exception - if something goes wrong.
     */
    @Test
    public void canRemoveRelease() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        releases.create("v1.1.1");
        releases.create("v1.1.2");
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.<Release>iterableWithSize(2)
        );
        releases.remove(1);
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.<Release>iterableWithSize(1)
        );
    }

    /**
     * MkReleases can find release by tag.
     * @throws Exception If some problem inside
     */
    @Test
    public void findsReleaseByTag() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        final String tag = "v5.0";
        releases.create(tag);
        MatcherAssert.assertThat(
            new Releases.Smart(releases).exists(tag),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new Release.Smart(new Releases.Smart(releases).find(tag)).tag(),
            Matchers.equalTo(tag)
        );
    }

    /**
     * The release's name should be empty upon initial creation.
     *
     * @throws Exception Unexpected.
     */
    @Test
    public void releaseNameIsEmpty() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        final String tag = "tag";
        releases.create(tag);
        MatcherAssert.assertThat(
            new Release.Smart(releases.iterate().iterator().next())
                .name().isEmpty(),
            Matchers.is(true)
        );
    }

    /**
     * The release's body should be empty upon initial creation.
     *
     * @throws Exception Unexpected.
     */
    @Test
    public void releaseBodyIsEmpty() throws Exception {
        final Releases releases = new MkGithub().randomRepo().releases();
        final String tag = "tag";
        releases.create(tag);
        MatcherAssert.assertThat(
            new Release.Smart(releases.iterate().iterator().next())
                .body().isEmpty(),
            Matchers.is(true)
        );
    }
}
