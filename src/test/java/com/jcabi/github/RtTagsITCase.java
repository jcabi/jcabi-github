/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.github.OAuthScope.Scope;
import jakarta.json.Json;
import jakarta.json.JsonObject;
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
@OAuthScope(Scope.REPO)
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
     * RtTags creates a tag.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsTag() throws Exception {
        final References refs = repo.git().references();
        final String sha = refs.get("refs/heads/master").json()
            .getJsonObject("object").getString("sha");
        final String tag = RandomStringUtils.randomAlphanumeric(Tv.FIVE);
        final JsonObject tagger = Json.createObjectBuilder()
            .add("name", "Scott").add("email", "scott@gmail.com")
            .add("date", "2013-06-17T14:53:35-07:00").build();
        MatcherAssert.assertThat(
            repo.git().tags().create(
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
