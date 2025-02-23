/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.github.OAuthScope.Scope;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link RtReleaseAssets}.
 *
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@OAuthScope(Scope.REPO)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RtReleaseAssetsITCase {

    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    private static RepoRule rule = new RepoRule();

    /**
     * Set up test fixtures.
     * @throws Exception If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final Github github = new GithubIT().connect();
        repos = github.repos();
        repo = rule.repo(repos);
        repo.releases().create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
    }

    /**
     * Tear down test fixtures.
     * @throws Exception If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if (repos != null && repo != null) {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtReleaseAssets can upload release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void uploadsAssets() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final String name = "upload.txt";
            final ReleaseAsset uploaded = assets.upload(
                "upload".getBytes(),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                uploaded.json().getString("name"),
                Matchers.is(name)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can upload two release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void uploadsTwoAssets() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final String name = "upload.txt";
            final ReleaseAsset uploaded = assets.upload(
                "upload".getBytes(),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                uploaded.json().getString("name"),
                Matchers.is(name)
            );
            final String othername = "upload2.txt";
            final ReleaseAsset otheruploaded = assets.upload(
                "upload2".getBytes(),
                "text/plain",
                othername
            );
            MatcherAssert.assertThat(
                otheruploaded.json().getString("name"),
                Matchers.is(othername)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can upload one release assets to two releases.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void uploadsSameAssetInTwoReleases() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        final Release otherrelease = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        final ReleaseAssets assets = release.assets();
        final ReleaseAssets otherassets = otherrelease.assets();
        try {
            final String name = "upload.txt";
            final ReleaseAsset uploaded = assets.upload(
                "upload".getBytes(),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                uploaded.json().getString("name"),
                Matchers.is(name)
            );
            final ReleaseAsset otheruploaded = otherassets.upload(
                "upload".getBytes(),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                otheruploaded.json().getString("name"),
                Matchers.is(name)
            );
        } finally {
            releases.remove(release.number());
            releases.remove(otherrelease.number());
        }
    }

    /**
     * RtReleaseAssets can fetch release assets by asset ID.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void fetchesAssets() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final ReleaseAsset uploaded = assets.upload(
                "fetch".getBytes(),
                "text/plain",
                "fetch.txt"
            );
            MatcherAssert.assertThat(
                assets.get(uploaded.number()),
                Matchers.is(uploaded)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can iterate through multiple release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void iteratesAssets() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            final ReleaseAsset first = assets.upload(
                "first".getBytes(),
                "text/plain",
                "first.txt"
            );
            final ReleaseAsset second = assets.upload(
                "second".getBytes(),
                "text/plain",
                "second.txt"
            );
            MatcherAssert.assertThat(
                assets.iterate(),
                Matchers.contains(first, second)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleaseAssets can return an empty list of release assets.
     * @throws Exception If an exception occurs.
     */
    @Test
    public void returnsNoAssets() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            MatcherAssert.assertThat(
                assets.iterate(),
                Matchers.emptyIterable()
            );
        } finally {
            releases.remove(release.number());
        }
    }

}
