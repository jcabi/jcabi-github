/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtReleases}.
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtReleasesITCase {

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
        RtReleasesITCase.repos = github.repos();
        RtReleasesITCase.repo = RtReleasesITCase.rule.repo(RtReleasesITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtReleasesITCase.repos != null && RtReleasesITCase.repo != null) {
            RtReleasesITCase.repos.remove(RtReleasesITCase.repo.coordinates());
        }
    }

    @Test
    void canFetchAllReleases() throws IOException {
        final Releases releases = RtReleasesITCase.repo.releases();
        try {
            MatcherAssert.assertThat(
                "Collection is not empty",
                releases.iterate(),
                Matchers.not(Matchers.emptyIterableOf(Release.class))
            );
        } finally {
            releases.remove(
                releases.create(
                    RandomStringUtils.secure().nextAlphanumeric(10)
                    ).number()
            );
        }
    }

    @Test
    void canFetchRelease() throws IOException {
        final Releases releases = RtReleasesITCase.repo.releases();
        final Release release = releases.create("v1.0");
        MatcherAssert.assertThat(
            "Fetched release has a wrong number",
            releases.get(release.number()).number(),
            Matchers.equalTo(release.number())
        );
        releases.remove(release.number());
    }

    @Test
    void canFetchReleaseTag() throws IOException {
        final Releases releases = RtReleasesITCase.repo.releases();
        final String tag = "v1.0";
        final Release release = releases.create(tag);
        MatcherAssert.assertThat(
            "Fetched release has a wrong tag",
            new Release.Smart(releases.get(release.number())).tag(),
            Matchers.equalTo(tag)
        );
        releases.remove(release.number());
    }

    @Test
    void canCreateRelease() throws IOException {
        final Releases releases = RtReleasesITCase.repo.releases();
        final Release created = releases.create("0.1");
        try {
            MatcherAssert.assertThat(
                "Created release is different from the obtained one",
                created,
                Matchers.is(releases.get(created.number()))
            );
        } finally {
            releases.remove(created.number());
        }
    }

    @Test
    void canCreateReleaseWithTag() throws IOException {
        final Releases releases = RtReleasesITCase.repo.releases();
        final Release created = releases.create("0.1");
        try {
            MatcherAssert.assertThat(
                "Created release has a wrong tag",
                new Release.Smart(created).tag(),
                Matchers.equalTo(
                    new Release.Smart(releases.get(created.number())).tag()
                )
            );
        } finally {
            releases.remove(created.number());
        }
    }

    @Test
    void canIterateCreatedRelease() throws IOException {
        final Releases releases = RtReleasesITCase.repo.releases();
        final Release release = releases.create(
            RandomStringUtils.secure().nextAlphanumeric(10)
        );
        try {
            MatcherAssert.assertThat(
                "Created release is not iterated",
                releases.iterate(),
                Matchers.hasItem(release)
            );
        } finally {
            releases.remove(release.number());
        }
    }

    @Test
    void canRemoveRelease() throws IOException {
        final Releases releases = RtReleasesITCase.repo.releases();
        final Release release = releases.create(
            RandomStringUtils.secure().nextAlphanumeric(10)
        );
        releases.remove(release.number());
        MatcherAssert.assertThat(
            "Removed release is still there",
            releases.iterate(),
            Matchers.not(Matchers.hasItem(release))
        );
    }

    @Test
    void canEditTag() throws IOException {
        final Releases releases = RtReleasesITCase.repo.releases();
        final Release release = releases.create(
            RandomStringUtils.secure().nextAlphanumeric(10)
        );
        final String tag = RandomStringUtils.secure().nextAlphanumeric(15);
        new Release.Smart(release).tag(tag);
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(releases.get(release.number())).tag(),
            Matchers.equalTo(tag)
        );
        releases.remove(release.number());
    }

    @Test
    void canEditBody() throws IOException {
        final Releases releases = RtReleasesITCase.repo.releases();
        final Release release = releases.create(
            RandomStringUtils.secure().nextAlphanumeric(10)
        );
        final String body = "Description of the release";
        new Release.Smart(release).body(body);
        MatcherAssert.assertThat(
            "Values are not equal",
            new Release.Smart(releases.get(release.number())).body(),
            Matchers.equalTo(body)
        );
        releases.remove(release.number());
    }
}
