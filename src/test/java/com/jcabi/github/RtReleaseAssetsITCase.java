/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Integration test for {@link RtReleaseAssets}.
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtReleaseAssetsITCase {

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
     */
    private static RepoRule rule = new RepoRule();

    /**
     * Set up test fixtures.
     */
    @BeforeAll
    static void setUp() throws IOException {
        final GitHub github = GitHubIT.connect();
        RtReleaseAssetsITCase.repos = github.repos();
        RtReleaseAssetsITCase.repo = RtReleaseAssetsITCase.rule.repo(RtReleaseAssetsITCase.repos);
        RtReleaseAssetsITCase.repo.releases().create(
            RandomStringUtils.secure().nextAlphanumeric(10)
        );
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtReleaseAssetsITCase.repos != null && RtReleaseAssetsITCase.repo != null) {
            RtReleaseAssetsITCase.repos.remove(RtReleaseAssetsITCase.repo.coordinates());
        }
    }

    @Test
    void uploadsAssets() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
        final Release release = releases
            .create(RandomStringUtils.secure().nextAlphanumeric(10));
        try {
            final String name = "upload.txt";
            MatcherAssert.assertThat(
                "Values are not equal",
                release.assets().upload(
                    "upload".getBytes(StandardCharsets.UTF_8),
                    "text/plain",
                    name
                ).json().getString("name"),
                Matchers.is(name)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    @Test
    void uploadsSecondAsset() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
        final Release release = releases
            .create(RandomStringUtils.secure().nextAlphanumeric(10));
        final ReleaseAssets assets = release.assets();
        try {
            final String othername = "upload2.txt";
            assets.upload(
                "upload".getBytes(StandardCharsets.UTF_8),
                "text/plain",
                "upload.txt"
            );
            MatcherAssert.assertThat(
                "Second asset has a wrong name",
                assets.upload(
                    "upload2".getBytes(StandardCharsets.UTF_8),
                    "text/plain",
                    othername
                ).json().getString("name"),
                Matchers.is(othername)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    @Test
    void uploadsSameAssetInTwoReleases() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
        final Release release = releases.create(
            RandomStringUtils.secure().nextAlphanumeric(10)
        );
        final Release otherrelease = releases.create(
            RandomStringUtils.secure().nextAlphanumeric(10)
        );
        final ReleaseAssets assets = release.assets();
        try {
            final String name = "upload.txt";
            assets.upload(
                "upload".getBytes(StandardCharsets.UTF_8),
                "text/plain",
                name
            );
            MatcherAssert.assertThat(
                "Asset of the other release has a wrong name",
                otherrelease.assets().upload(
                    "upload".getBytes(StandardCharsets.UTF_8),
                    "text/plain",
                    name
                ).json().getString("name"),
                Matchers.is(name)
            );
        } finally {
            releases.remove(release.number());
            releases.remove(otherrelease.number());
        }
    }

    @Test
    void fetchesAssets() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
        final Release release = releases
            .create(RandomStringUtils.secure().nextAlphanumeric(10));
        final ReleaseAssets assets = release.assets();
        try {
            final ReleaseAsset uploaded = assets.upload(
                "fetch".getBytes(StandardCharsets.UTF_8),
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
    void iteratesAssets() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
        final Release release = releases
            .create(RandomStringUtils.secure().nextAlphanumeric(10));
        final ReleaseAssets assets = release.assets();
        try {
            MatcherAssert.assertThat(
                "Assertion failed",
                assets.iterate(),
                Matchers.contains(
                    assets.upload(
                        "first".getBytes(StandardCharsets.UTF_8),
                        "text/plain",
                        "first.txt"
                    ),
                    assets.upload(
                        "second".getBytes(StandardCharsets.UTF_8),
                        "text/plain",
                        "second.txt"
                        )
                )
            );
        } finally {
            releases.remove(release.number());
        }
    }

    @Test
    void returnsNoAssets() throws IOException {
        final Releases releases = RtReleaseAssetsITCase.repo.releases();
        final Release release = releases
            .create(RandomStringUtils.secure().nextAlphanumeric(10));
        try {
            MatcherAssert.assertThat(
                "Collection is not empty",
                release.assets().iterate(),
                Matchers.emptyIterable()
            );
        } finally {
            releases.remove(release.number());
        }
    }
}
