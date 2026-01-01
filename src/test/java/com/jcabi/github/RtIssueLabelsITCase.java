/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link IssueLabels}.
 * @since 0.6
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtIssueLabelsITCase {
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
    static void setUp() throws IOException {
        final GitHub github = GitHubIT.connect();
        RtIssueLabelsITCase.repos = github.repos();
        RtIssueLabelsITCase.repo = new RepoRule().repo(RtIssueLabelsITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtIssueLabelsITCase.repos != null && RtIssueLabelsITCase.repo != null) {
            RtIssueLabelsITCase.repos.remove(RtIssueLabelsITCase.repo.coordinates());
        }
    }

    /**
     * RtIssueLabels can list all labels in an issue.
     * @throws Exception If some problem inside
     */
    @Test
    void listsLabels() throws Exception {
        final IssueLabels.Smart labels = new IssueLabels.Smart(
            RtIssueLabelsITCase.issue().labels()
        );
        final String name = "test-label";
        final String color = "cfcfcf";
        labels.addIfAbsent(name, color);
        MatcherAssert.assertThat(
            "Values are not equal",
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
