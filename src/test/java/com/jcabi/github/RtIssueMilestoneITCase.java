/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
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
     */
    @BeforeClass
    public static void setUp() throws IOException {
        final GitHub github = new GitHubIT().connect();
        RtIssueMilestoneITCase.repos = github.repos();
        RtIssueMilestoneITCase.repo = new RepoRule().repo(RtIssueMilestoneITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterClass
    public static void tearDown() throws IOException {
        if (RtIssueMilestoneITCase.repos != null && RtIssueMilestoneITCase.repo != null) {
            RtIssueMilestoneITCase.repos.remove(RtIssueMilestoneITCase.repo.coordinates());
        }
    }

    @Test
    public void addIssueToMilestone() throws Exception {
        final Issue issue = RtIssueMilestoneITCase.issue();
        final Milestone milestone = RtIssueMilestoneITCase.repo
            .milestones().create("one");
        new Issue.Smart(issue).milestone(milestone);
        MatcherAssert.assertThat(
            "Value is not greater than expected",
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
            "Values are not equal",
            issue.hasMilestone(),
            Matchers.is(false)
        );
        final Milestone milestone = RtIssueMilestoneITCase.repo
            .milestones().create("two");
        issue.milestone(milestone);
        MatcherAssert.assertThat(
            "Values are not equal",
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
            "Values are not equal",
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
     */
    private static Issue issue() throws IOException {
        return RtIssueMilestoneITCase.repo.issues().create("test issue title", "test issue body");
    }
}
