/*
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
 * Test case for {@link RtReleases}.
 * @since 0.8
 */
@OAuthScope(Scope.REPO)
public final class RtReleasesITCase {

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
     * RtReleases can iterate releases.
     * @throws Exception if something goes wrong
     */
    @Test
    public void canFetchAllReleases() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        try {
            MatcherAssert.assertThat(
                releases.iterate(),
                Matchers.not(Matchers.emptyIterableOf(Release.class))
            );
        } finally {
            releases.remove(release.number());
        }
    }

    /**
     * RtReleases can fetch a single release.
     * @throws Exception if any error inside
     */
    @Test
    public void canFetchRelease() throws Exception {
        final Releases releases = repo.releases();
        final String tag = "v1.0";
        final Release release = releases.create(tag);
        MatcherAssert.assertThat(
            releases.get(release.number()).number(),
            Matchers.equalTo(release.number())
        );
        MatcherAssert.assertThat(
            new Release.Smart(releases.get(release.number())).tag(),
            Matchers.equalTo(tag)
        );
        releases.remove(release.number());
    }

    /**
     * RtReleases can create a release.
     * @throws Exception if any error inside
     */
    @Test
    public void canCreateRelease() throws Exception {
        final Releases releases = repo.releases();
        final Release created = releases.create("0.1");
        final int number = created.number();
        try {
            final Release obtained = releases.get(number);
            MatcherAssert.assertThat(
                created,
                Matchers.is(obtained)
            );
            MatcherAssert.assertThat(
                new Release.Smart(created).tag(),
                Matchers.equalTo(new Release.Smart(obtained).tag())
            );
        } finally {
            releases.remove(number);
        }
    }

    /**
     * RtReleases can remove a release.
     * @throws Exception if any problem inside
     */
    @Test
    public void canRemoveRelease() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.hasItem(release)
        );
        releases.remove(release.number());
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.not(Matchers.hasItem(release))
        );
    }

    /**
     * RtReleases can edit release tag.
     * @throws Exception if any problem inside.
     */
    @Test
    public void canEditTag() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        final String tag = RandomStringUtils.randomAlphanumeric(Tv.FIFTEEN);
        new Release.Smart(release).tag(tag);
        MatcherAssert.assertThat(
            new Release.Smart(releases.get(release.number())).tag(),
            Matchers.equalTo(tag)
        );
        releases.remove(release.number());
    }

    /**
     * RtReleases can edit release body.
     * @throws Exception if any problem inside.
     */
    @Test
    public void canEditBody() throws Exception {
        final Releases releases = repo.releases();
        final Release release = releases.create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        final String body = "Description of the release";
        new Release.Smart(release).body(body);
        MatcherAssert.assertThat(
            new Release.Smart(releases.get(release.number())).body(),
            Matchers.equalTo(body)
        );
        releases.remove(release.number());
    }
}
