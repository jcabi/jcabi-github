/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.immutable.ArrayMap;
import java.io.IOException;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration case for {@link GitHub}.
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtIssuesITCase {
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
        RtIssuesITCase.repos = github.repos();
        RtIssuesITCase.repo = new RepoRule().repo(RtIssuesITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterClass
    public static void tearDown() throws IOException {
        if (RtIssuesITCase.repos != null && RtIssuesITCase.repo != null) {
            RtIssuesITCase.repos.remove(RtIssuesITCase.repo.coordinates());
        }
    }

    /**
     * RtIssues can iterate issues.
     */
    @Test
    public void iteratesIssues() throws IOException {
        final Iterable<Issue.Smart> issues = new Smarts<>(
            new Bulk<>(
                RtIssuesITCase.repo.issues().iterate(
                    new ArrayMap<String, String>().with("sort", "comments")
                )
            )
        );
        for (final Issue.Smart issue : issues) {
            MatcherAssert.assertThat(
                "Value is null",
                issue.title(),
                Matchers.notNullValue()
            );
        }
    }

    /**
     * RtIssues can search issues within a repository.
     */
    @Test
    public void searchesIssues() throws IOException {
        final String targetLabel = "bug";
        final EnumMap<Issues.Qualifier, String> qualifiers =
            new EnumMap<>(Issues.Qualifier.class);
        qualifiers.put(Issues.Qualifier.LABELS, targetLabel);
        final Iterable<Issue.Smart> issues = new Smarts<>(
            new Bulk<>(
                RtIssuesITCase.repo.issues().search(
                    Issues.Sort.UPDATED,
                    Search.Order.ASC,
                    qualifiers
                )
            )
        );
        Date previous = null;
        final Set<String> labelNames = new HashSet<>();
        for (final Issue.Smart issue : issues) {
            MatcherAssert.assertThat(
                "Value is null",
                issue.title(),
                Matchers.notNullValue()
            );
            if (previous != null) {
                MatcherAssert.assertThat(
                    "Value is not less than expected",
                    issue.updatedAt(),
                    Matchers.lessThanOrEqualTo(previous)
                );
            }
            previous = issue.updatedAt();
            labelNames.clear();
            for (final Label label : issue.roLabels().iterate()) {
                labelNames.add(label.name());
            }
            MatcherAssert.assertThat(
                "Assertion failed",
                labelNames,
                Matchers.contains(targetLabel)
            );
        }
    }
}
