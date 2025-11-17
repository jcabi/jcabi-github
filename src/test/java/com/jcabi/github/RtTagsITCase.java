/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github;

import com.jcabi.aspects.Tv;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration testcase for RtTags.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtTagsITCase {

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
        RtTagsITCase.repos = github.repos();
        RtTagsITCase.repo = RtTagsITCase.rule.repo(RtTagsITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterClass
    public static void tearDown() throws IOException {
        if (RtTagsITCase.repos != null && RtTagsITCase.repo != null) {
            RtTagsITCase.repos.remove(RtTagsITCase.repo.coordinates());
        }
    }

    /**
     * RtTags creates a tag.
     */
    @Test
    public void createsTag() throws IOException {
        final References refs = RtTagsITCase.repo.git().references();
        final String sha = refs.get("refs/heads/master").json()
            .getJsonObject("object").getString("sha");
        final String tag = RandomStringUtils.randomAlphanumeric(Tv.FIVE);
        final JsonObject tagger = Json.createObjectBuilder()
            .add("name", "Scott").add("email", "scott@gmail.com")
            .add("date", "2013-06-17T14:53:35-07:00").build();
        MatcherAssert.assertThat(
            RtTagsITCase.repo.git().tags().create(
                Json.createObjectBuilder()
                    .add("tag", tag).add("message", "initial version")
                    .add("object", sha).add("type", "commit")
                    .add("tagger", tagger).build()
            ), Matchers.notNullValue()
        );
        refs.remove(
            new StringBuilder().append("tags/").append(tag).toString()
        );
    }
}
