/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import com.jcabi.immutable.ArrayMap;
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
 * Integration case for {@link Github}.
 */
@OAuthScope(Scope.REPO)
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
     * RtIssues can iterate issues.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesIssues() throws Exception {
        final Iterable<Issue.Smart> issues = new Smarts<>(
            new Bulk<>(
                repo.issues().iterate(
                    new ArrayMap<String, String>().with("sort", "comments")
                )
            )
        );
        for (final Issue.Smart issue : issues) {
            MatcherAssert.assertThat(
                issue.title(),
                Matchers.notNullValue()
            );
        }
    }

    /**
     * RtIssues can search issues within a repository.
     * @throws Exception If some problem inside
     */
    @Test
    public void searchesIssues() throws Exception {
        final String targetLabel = "bug";
        final EnumMap<Issues.Qualifier, String> qualifiers =
            new EnumMap<>(Issues.Qualifier.class);
        qualifiers.put(Issues.Qualifier.LABELS, targetLabel);
        final Iterable<Issue.Smart> issues = new Smarts<>(
            new Bulk<>(
                repo.issues().search(
                    Issues.Sort.UPDATED,
                    Search.Order.ASC,
                    qualifiers
                )
            )
        );
        Date prevUpdated = null;
        final Set<String> labelNames = new HashSet<>();
        for (final Issue.Smart issue : issues) {
            MatcherAssert.assertThat(
                issue.title(),
                Matchers.notNullValue()
            );
            if (prevUpdated != null) {
                MatcherAssert.assertThat(
                    issue.updatedAt(),
                    Matchers.lessThanOrEqualTo(prevUpdated)
                );
            }
            prevUpdated = issue.updatedAt();
            labelNames.clear();
            for (final Label label : issue.roLabels().iterate()) {
                labelNames.add(label.name());
            }
            MatcherAssert.assertThat(
                labelNames,
                Matchers.contains(targetLabel)
            );
        }
    }
}
