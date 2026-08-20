/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.immutable.ArrayMap;
import java.io.IOException;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link GitHub}.
 * @since 0.1
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtIssuesITCase {

    /**
     * Label of the issues to search for.
     */
    private static final String TARGET = "bug";

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
        RtIssuesITCase.repos = github.repos();
        RtIssuesITCase.repo = new RepoRule().repo(RtIssuesITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtIssuesITCase.repos != null && RtIssuesITCase.repo != null) {
            RtIssuesITCase.repos.remove(RtIssuesITCase.repo.coordinates());
        }
    }

    @Test
    void iteratesIssues() throws IOException {
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

    @Test
    void searchesIssuesWithTitles() throws IOException {
        for (final Issue.Smart issue : RtIssuesITCase.found()) {
            MatcherAssert.assertThat(
                "Found issue has no title",
                issue.title(),
                Matchers.notNullValue()
            );
        }
    }

    @Test
    void searchesIssuesInUpdateOrder() throws IOException {
        Instant previous = null;
        for (final Issue.Smart issue : RtIssuesITCase.found()) {
            if (previous != null) {
                MatcherAssert.assertThat(
                    "Found issues are not sorted by update time",
                    issue.updatedAt(),
                    Matchers.lessThanOrEqualTo(previous)
                );
            }
            previous = issue.updatedAt();
        }
    }

    @Test
    void searchesIssuesByLabel() throws IOException {
        for (final Issue.Smart issue : RtIssuesITCase.found()) {
            final Set<String> labels = new HashSet<>();
            for (final Label label : issue.roLabels().iterate()) {
                labels.add(label.name());
            }
            MatcherAssert.assertThat(
                "Found issue has no expected label",
                labels,
                Matchers.contains(RtIssuesITCase.TARGET)
            );
        }
    }

    private static Iterable<Issue.Smart> found() throws IOException {
        final Map<Issues.Qualifier, String> qualifiers =
            new EnumMap<>(Issues.Qualifier.class);
        qualifiers.put(Issues.Qualifier.LABELS, RtIssuesITCase.TARGET);
        return new Smarts<>(
            new Bulk<>(
                RtIssuesITCase.repo.issues().search(
                    Issues.Sort.UPDATED,
                    Search.Order.ASC,
                    qualifiers
                )
            )
        );
    }
}
