/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Integration test for {@link RtReleaseAssets}.
 *
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
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
     */
    @BeforeAll
    public static void setUp() throws IOException {
        final GitHub github = new GitHubIT().connect();
        RtReleaseAssetsITCase.repos = github.repos();
        RtReleaseAssetsITCase.repo = RtReleaseAssetsITCase.rule.repo(RtReleaseAssetsITCase.repos);
        RtReleaseAssetsITCase.repo.releases().create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    public static void tearDown() throws IOException {
        if (RtReleaseAssetsITCase.repos != null && RtReleaseAssetsITCase.repo != null) {
            RtReleaseAssetsITCase.repos.remove(RtReleaseAssetsITCase.repo.coordinates());
        }
    }

    @Test
    public void uploadsAssets() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
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
                "Values are not equal",
                uploaded.json().getString("name"),
                Matchers.is(name)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    @Test
    public void uploadsTwoAssets() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
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
                "Values are not equal",
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
                "Values are not equal",
                otheruploaded.json().getString("name"),
                Matchers.is(othername)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    @Test
    public void uploadsSameAssetInTwoReleases() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
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
                "Values are not equal",
                uploaded.json().getString("name"),
                Matchers.is(name)
            );
            final ReleaseAsset otheruploaded = otherassets.upload(
                "upload".getBytes(),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                otheruploaded.json().getString("name"),
                Matchers.is(name)
            );
        } finally {
            releases.remove(release.number());
            releases.remove(otherrelease.number());
        }
    }

    @Test
    public void fetchesAssets() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
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
                "Values are not equal",
                assets.get(uploaded.number()),
                Matchers.is(uploaded)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    @Test
    public void iteratesAssets() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
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
                "Assertion failed",
                assets.iterate(),
                Matchers.contains(first, second)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    @Test
    public void returnsNoAssets() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
        final Release release = releases
            .create(RandomStringUtils.randomAlphanumeric(Tv.TEN));
        final ReleaseAssets assets = release.assets();
        try {
            MatcherAssert.assertThat(
                "Collection is not empty",
                assets.iterate(),
                Matchers.emptyIterable()
            );
        } finally {
            releases.remove(release.number());
        }
    }

}
