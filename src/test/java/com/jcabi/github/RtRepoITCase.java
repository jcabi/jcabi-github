/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import jakarta.json.Json;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link GitHub}.
 * @since 0.1
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 *  See https://developer.github.com/v3/repos/#list-languages for API details
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtRepoITCase {
    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * Set up test fixtures.
     */
    @BeforeAll
    public void setUp() throws IOException {
        final GitHub github = GitHubIT.connect();
        RtRepoITCase.repos = github.repos();
        RtRepoITCase.repo = RtRepoITCase.repos.create(
            new Repos.RepoCreate(
                RandomStringUtils.randomAlphanumeric(Tv.TEN),
                false
            ).withAutoInit(true)
        );
        RtRepoITCase.repo.contents().create(
            Json.createObjectBuilder()
                .add("path", "test.java")
                .add("message", "Test file for language test")
                .add(
                    "content", Base64.encodeBase64String(
                        "some content".getBytes()
                    )
                )
                .add("ref", "master")
                .build()
        );
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    public void tearDown() throws IOException {
        if (RtRepoITCase.repos != null && RtRepoITCase.repo != null) {
            RtRepoITCase.repos.remove(RtRepoITCase.repo.coordinates());
        }
    }

    @Test
    public void identifiesItself() {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoITCase.repo.coordinates(),
            Matchers.notNullValue()
        );
    }

    @Test
    public void iteratesEvents() throws IOException {
        final Issue issue = RtRepoITCase.repo.issues().create("Test", "This is a bug");
        new Issue.Smart(issue).close();
        MatcherAssert.assertThat(
            "Collection is not empty",
            RtRepoITCase.repo.issueEvents().iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    @Test
    public void exists() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new Repo.Smart(RtRepoITCase.repo).exists(), Matchers.is(Boolean.TRUE)
        );
    }

    @Test
    public void fetchCommits() {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoITCase.repo.commits(),
            Matchers.notNullValue()
        );
    }

    @Test
    public void iteratesAssignees() {
        MatcherAssert.assertThat(
            "Collection is not empty",
            RtRepoITCase.repo.assignees().iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    @Test
    public void fetchLanguages() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            RtRepoITCase.repo.languages(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can iterate languages. This test is ignored because of bug
     * https://github.com/jcabi/jcabi-github/issues/1007 .
     */
    @Test
    @Disabled
    public void iteratesLanguages() throws IOException {
        MatcherAssert.assertThat(
            "Collection is not empty",
            RtRepoITCase.repo.languages(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
