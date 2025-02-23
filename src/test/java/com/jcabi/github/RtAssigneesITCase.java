/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.github.OAuthScope.Scope;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link RtAssignees}.
 * @since 0.7
 */
@OAuthScope(Scope.READ_ORG)
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
     * @throws Exception If some errors occurred.
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final Github github = new GithubIT().connect();
        repos = github.repos();
        repo = new RepoRule().repo(repos);
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
     * RtAssignees can iterate over assignees.
     * @throws Exception Exception If some problem inside
     */
    @Test
    public void iteratesAssignees() throws Exception {
        final Iterable<User> users = new Smarts<>(
            new Bulk<>(
                repo.assignees().iterate()
            )
        );
        for (final User user : users) {
            MatcherAssert.assertThat(
                user.login(),
                Matchers.notNullValue()
            );
        }
    }

    /**
     * RtAssignees can check if user is assignee for this repo.
     * @throws Exception Exception If some problem inside
     */
    @Test
    public void checkUserIsAssigneeForRepo() throws Exception {
        MatcherAssert.assertThat(
            repo.assignees().check(repo.coordinates().user()),
            Matchers.is(true)
        );
    }

    /**
     * RtAssignees can check if user is NOT assignee for this repo.
     * @throws Exception Exception If some problem inside
     */
    @Test
    public void checkUserIsNotAssigneeForRepo() throws Exception {
        MatcherAssert.assertThat(
            repo.assignees()
                .check(RandomStringUtils.randomAlphanumeric(Tv.TEN)),
            Matchers.is(false)
        );
    }
}
