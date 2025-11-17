/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration case for {@link IssueLabels}.
 * @since 0.6
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(Scope.REPO)
public final class RtIssueLabelsITCase {
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
        final Github github = new GithubIT().connect();
        RtIssueLabelsITCase.repos = github.repos();
        RtIssueLabelsITCase.repo = new RepoRule().repo(RtIssueLabelsITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterClass
    public static void tearDown() throws IOException {
        if (RtIssueLabelsITCase.repos != null && RtIssueLabelsITCase.repo != null) {
            RtIssueLabelsITCase.repos.remove(RtIssueLabelsITCase.repo.coordinates());
        }
    }

    /**
     * RtIssueLabels can list all labels in an issue.
     * @throws Exception If some problem inside
     */
    @Test
    public void listsLabels() throws Exception {
        final IssueLabels.Smart labels = new IssueLabels.Smart(
            RtIssueLabelsITCase.issue().labels()
        );
        final String name = "test-label";
        final String color = "cfcfcf";
        labels.addIfAbsent(name, color);
        MatcherAssert.assertThat(
            new Label.Smart(labels.get(name)).color(),
            Matchers.equalTo(color)
        );
        labels.remove(name);
    }

    /**
     * Create and return issue to test.
     * @return Issue
     */
    private static Issue issue() throws IOException {
        return RtIssueLabelsITCase.repo.issues().create("test issue title", "test issue body");
    }

}
