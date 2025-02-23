/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.github.OAuthScope.Scope;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link RtRelease}.
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@OAuthScope(Scope.REPO)
public final class RtReleaseITCase {

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
     * RtRelease can edit a release.
     * @throws Exception If any problems during test execution occur.
     */
    @Test
    public void canEditRelease() throws Exception {
        final Release release = repo.releases().create(
            RandomStringUtils.randomAlphanumeric(Tv.TEN)
        );
        final JsonObject patch = Json.createObjectBuilder()
            .add("tag_name", RandomStringUtils.randomAlphanumeric(Tv.TEN))
            .add("name", "jcabi Github test release")
            .add("body", "jcabi Github was here!")
            .build();
        release.patch(patch);
        final JsonObject json = repo.releases()
            .get(release.number()).json();
        for (final String property : patch.keySet()) {
            MatcherAssert.assertThat(
                json.getString(property),
                Matchers.equalTo(patch.getString(property))
            );
        }
    }

}
