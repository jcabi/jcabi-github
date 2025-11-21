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
 * Integration testcase for RtTag.
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtTagITCase {

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
        RtTagITCase.repos = github.repos();
        RtTagITCase.repo = RtTagITCase.rule.repo(RtTagITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtTagITCase.repos != null && RtTagITCase.repo != null) {
            RtTagITCase.repos.remove(RtTagITCase.repo.coordinates());
        }
    }

    @Test
    void fetchesJson() throws IOException {
        final String object = "object";
        final String tag = RandomStringUtils.secure().nextAlphanumeric(10);
        final References refs = RtTagITCase.repo.git().references();
        final String sha = refs.get("refs/heads/master").json()
            .getJsonObject(object).getString("sha");
        final JsonObject tagger = Json.createObjectBuilder()
            .add("name", "Scott").add("email", "scott@gmail.com")
            .add("date", "2013-06-17T14:53:35-07:00").build();
        try {
            final String content = "initial version";
            final String message = "message";
            MatcherAssert.assertThat(
                "Values are not equal",
                RtTagITCase.repo.git().tags().create(
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
