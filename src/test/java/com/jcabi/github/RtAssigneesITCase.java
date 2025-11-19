/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Test case for {@link RtAssignees}.
 * @since 0.7
 */
@OAuthScope(OAuthScope.Scope.READ_ORG)
public final class RtAssigneesITCase {
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
    public static void setUp() throws IOException {
        final GitHub github = new GitHubIT().connect();
        RtAssigneesITCase.repos = github.repos();
        RtAssigneesITCase.repo = new RepoRule().repo(RtAssigneesITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    public static void tearDown() throws IOException {
        if (RtAssigneesITCase.repos != null && RtAssigneesITCase.repo != null) {
            RtAssigneesITCase.repos.remove(RtAssigneesITCase.repo.coordinates());
        }
    }

    @Test
    public void iteratesAssignees() throws IOException {
        final Iterable<User> users = new Smarts<>(
            new Bulk<>(
                RtAssigneesITCase.repo.assignees().iterate()
            )
        );
        for (final User user : users) {
            MatcherAssert.assertThat(
                "Value is null",
                user.login(),
                Matchers.notNullValue()
            );
        }
    }

    @Test
    public void checkUserIsAssigneeForRepo() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            RtAssigneesITCase.repo.assignees().check(RtAssigneesITCase.repo.coordinates().user()),
            Matchers.is(true)
        );
    }

    @Test
    public void checkUserIsNotAssigneeForRepo() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            RtAssigneesITCase.repo.assignees()
                .check(RandomStringUtils.randomAlphanumeric(Tv.TEN)),
            Matchers.is(false)
        );
    }
}
