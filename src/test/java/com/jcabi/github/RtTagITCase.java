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
 * Integration testcase for RtTag.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@OAuthScope(Scope.REPO)
public final class RtTagITCase {

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
     * RtTag should return its json representation.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void fetchesJson() throws Exception {
        final String object = "object";
        final String message = "message";
        final String content = "initial version";
        final String tag = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final References refs = repo.git().references();
        final String sha = refs.get("refs/heads/master").json()
            .getJsonObject(object).getString("sha");
        final JsonObject tagger = Json.createObjectBuilder()
            .add("name", "Scott").add("email", "scott@gmail.com")
            .add("date", "2013-06-17T14:53:35-07:00").build();
        try {
            MatcherAssert.assertThat(
                repo.git().tags().create(
                    Json.createObjectBuilder().add("tag", tag)
                        .add(message, content)
                        .add(object, sha).add("type", "commit")
                        .add("tagger", tagger).build()
                ).json().getString(message),
                Matchers.is(content)
            );
        } finally {
            refs.remove(
                new StringBuilder().append("tags/").append(tag).toString()
            );
        }
    }
}
