/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.github.OAuthScope.Scope;
import jakarta.json.Json;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration case for {@link Github}.
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 *  See https://developer.github.com/v3/repos/#list-languages for API details
 */
@OAuthScope(Scope.REPO)
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
     * @throws Exception If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final Github github = new GithubIT().connect();
        repos = github.repos();
        repo = repos.create(
            new Repos.RepoCreate(
                RandomStringUtils.randomAlphanumeric(Tv.TEN),
                false
            ).withAutoInit(true)
        );
        repo.contents().create(
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
     * @throws Exception If some errors occurred.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if (repos != null && repo != null) {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtRepo can identify itself.
     */
    @Test
    public void identifiesItself() {
        MatcherAssert.assertThat(
            repo.coordinates(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtRepo can fetch events.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesEvents() throws Exception {
        final Issue issue = repo.issues().create("Test", "This is a bug");
        new Issue.Smart(issue).close();
        MatcherAssert.assertThat(
            repo.issueEvents().iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * RtRepo can tell if it exists.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void exists() throws Exception {
        MatcherAssert.assertThat(
            new Repo.Smart(repo).exists(), Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * RtRepo can fetch its commits.
     */
    @Test
    public void fetchCommits() {
        MatcherAssert.assertThat(repo.commits(), Matchers.notNullValue());
    }

    /**
     * RtRepo can fetch assignees.
     */
    @Test
    public void iteratesAssignees() {
        MatcherAssert.assertThat(
            repo.assignees().iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }

    /**
     * RtRepo can fetch languages.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchLanguages() throws Exception {
        MatcherAssert.assertThat(repo.languages(), Matchers.notNullValue());
    }

    /**
     * RtRepo can iterate languages. This test is ignored because of bug
     * https://github.com/jcabi/jcabi-github/issues/1007 .
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void iteratesLanguages() throws Exception {
        MatcherAssert.assertThat(
            repo.languages(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
