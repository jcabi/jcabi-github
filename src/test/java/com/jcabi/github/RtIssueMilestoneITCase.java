/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration case for {@link Milestones}.
 * @since 0.33.1
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtIssueMilestoneITCase {
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
        repo = new RepoRule().repo(RtIssueMilestoneITCase.repos);
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

    @Test
    public void addIssueToMilestone() throws Exception {
        final Issue issue = RtIssueMilestoneITCase.issue();
        final Milestone milestone = RtIssueMilestoneITCase.repo
            .milestones().create("one");
        new Issue.Smart(issue).milestone(milestone);
        MatcherAssert.assertThat(
            new Milestone.Smart(milestone).openIssues(),
            Matchers.greaterThan(0)
        );
    }

    @Test
    public void checkMilestone() throws Exception {
        final Issue.Smart issue = new Issue.Smart(
            RtIssueMilestoneITCase.issue()
        );
        MatcherAssert.assertThat(
            issue.hasMilestone(),
            Matchers.is(false)
        );
        final Milestone milestone = RtIssueMilestoneITCase.repo
            .milestones().create("two");
        issue.milestone(milestone);
        MatcherAssert.assertThat(
            issue.hasMilestone(),
            Matchers.is(true)
        );
    }

    @Test
    public void readMilestone() throws Exception {
        final String title = "three";
        final Issue.Smart issue = new Issue.Smart(
            RtIssueMilestoneITCase.issue()
        );
        issue.milestone(
            RtIssueMilestoneITCase.repo.milestones().create(title)
        );
        MatcherAssert.assertThat(
            new Milestone.Smart(
                new Issue.Smart(
                    RtIssueMilestoneITCase.repo.issues().get(issue.number())
                ).milestone()
            ).title(),
            Matchers.equalTo(title)
        );
    }

    /**
     * Create and return issue to test.
     * @return Issue
     * @throws Exception If some problem inside
     */
    private static Issue issue() throws Exception {
        return repo.issues().create("test issue title", "test issue body");
    }
}
