/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
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
     * @throws Exception If some problem inside
     */
    private static Issue issue() throws Exception {
        return repo.issues().create("test issue title", "test issue body");
    }

}
