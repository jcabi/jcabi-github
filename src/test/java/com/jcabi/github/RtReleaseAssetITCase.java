/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import jakarta.json.Json;
import java.io.IOException;
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
@OAuthScope(OAuthScope.Scope.REPO)
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
     */
    @BeforeClass
    public static void setUp() throws IOException {
        final GitHub github = new GitHubIT().connect();
        RtReleaseAssetITCase.repos = github.repos();
        RtReleaseAssetITCase.repo = RtReleaseAssetITCase.rule.repo(RtReleaseAssetITCase.repos);
        RtReleaseAssetITCase.repo.releases().create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
    }

    /**
     * Tear down test fixtures.
     */
    @AfterClass
    public static void tearDown() throws IOException {
        if (RtReleaseAssetITCase.repos != null && RtReleaseAssetITCase.repo != null) {
            RtReleaseAssetITCase.repos.remove(RtReleaseAssetITCase.repo.coordinates());
        }
    }

    /**
     * RtReleaseAsset can fetch as JSON object.
     */
    @Test
    public void fetchAsJson() throws IOException {
        final String name = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final Release release = RtReleaseAssetITCase.repo.releases().create(name);
        try {
            MatcherAssert.assertThat(
                "Values are not equal",
                release.json().getInt("id"),
                Matchers.equalTo(release.number())
            );
        } finally {
            release.delete();
        }
    }

    /**
     * RtReleaseAsset can execute patch request.
     */
    @Test
    public void executePatchRequest() throws IOException {
        final Release release = RtReleaseAssetITCase.repo.releases().create(
            String.format("v%s", RandomStringUtils.randomAlphanumeric(Tv.TEN))
        );
        try {
            final String desc = "Description of the release";
            release.patch(Json.createObjectBuilder().add("body", desc).build());
            MatcherAssert.assertThat(
                "String does not start with expected value",
                new Release.Smart(release).body(),
                Matchers.startsWith(desc)
            );
        } finally {
            release.delete();
        }
    }

    /**
     * RtReleaseAsset can do delete operation.
     */
    @Test
    public void removesReleaseAsset() throws IOException {
        final Releases releases = RtReleaseAssetITCase.repo.releases();
        final String rname = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final Release release = releases.create(rname);
        try {
            MatcherAssert.assertThat(
                "Value is null",
                releases.get(release.number()),
                Matchers.notNullValue()
            );
        } finally {
            release.delete();
        }
        MatcherAssert.assertThat(
            "Assertion failed",
            releases.iterate(),
            Matchers.not(Matchers.contains(release))
        );
    }

}
