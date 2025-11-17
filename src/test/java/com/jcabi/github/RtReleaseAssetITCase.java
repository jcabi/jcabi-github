/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.github.OAuthScope.Scope;
import jakarta.json.Json;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link RtReleaseAsset}.
 *
 * @since 0.8
 * @checkstyle MultipleStringLiterals (300 lines)
 */
@OAuthScope(Scope.REPO)
public final class RtReleaseAssetITCase {

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
     * RtReleaseAsset can fetch as JSON object.
     * @throws Exception if some problem inside
     */
    @Test
    public void fetchAsJSON() throws Exception {
        final String name = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final Release release = repo.releases().create(name);
        try {
            MatcherAssert.assertThat(
                release.json().getInt("id"),
                Matchers.equalTo(release.number())
            );
        } finally {
            release.delete();
        }
    }

    /**
     * RtReleaseAsset can execute patch request.
     * @throws Exception if some problem inside
     */
    @Test
    public void executePatchRequest() throws Exception {
        final Release release = repo.releases().create(
            String.format("v%s", RandomStringUtils.randomAlphanumeric(Tv.TEN))
        );
        final String desc = "Description of the release";
        try {
            release.patch(Json.createObjectBuilder().add("body", desc).build());
            MatcherAssert.assertThat(
                new Release.Smart(release).body(),
                Matchers.startsWith(desc)
            );
        } finally {
            release.delete();
        }
    }

    /**
     * RtReleaseAsset can do delete operation.
     * @throws Exception If something goes wrong
     */
    @Test
    public void removesReleaseAsset() throws Exception {
        final Releases releases = repo.releases();
        final String rname = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final Release release = releases.create(rname);
        try {
            MatcherAssert.assertThat(
                releases.get(release.number()),
                Matchers.notNullValue()
            );
        } finally {
            release.delete();
        }
        MatcherAssert.assertThat(
            releases.iterate(),
            Matchers.not(Matchers.contains(release))
        );
    }

}
