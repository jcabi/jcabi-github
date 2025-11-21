/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtRelease}.
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtReleaseITCase {

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
    static void setUp() throws IOException {
        final GitHub github = GitHubIT.connect();
        RtReleaseITCase.repos = github.repos();
        RtReleaseITCase.repo = RtReleaseITCase.rule.repo(RtReleaseITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtReleaseITCase.repos != null && RtReleaseITCase.repo != null) {
            RtReleaseITCase.repos.remove(RtReleaseITCase.repo.coordinates());
        }
    }

    @Test
    void canEditRelease() throws IOException {
        final Release release = RtReleaseITCase.repo.releases().create(
            RandomStringUtils.secure().nextAlphanumeric(10)
        );
        final JsonObject patch = Json.createObjectBuilder()
            .add("tag_name", RandomStringUtils.secure().nextAlphanumeric(10))
            .add("name", "jcabi GitHub test release")
            .add("body", "jcabi GitHub was here!")
            .build();
        release.patch(patch);
        final JsonObject json = RtReleaseITCase.repo.releases()
            .get(release.number()).json();
        for (final String property : patch.keySet()) {
            MatcherAssert.assertThat(
                "Values are not equal",
                json.getString(property),
                Matchers.equalTo(patch.getString(property))
            );
        }
    }

}
