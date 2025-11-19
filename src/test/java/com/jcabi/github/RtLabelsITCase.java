/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Integration case for {@link Labels}.
 * @since 0.6
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
public final class RtLabelsITCase {
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
        final GitHub github = GitHubIT.connect();
        RtLabelsITCase.repos = github.repos();
        RtLabelsITCase.repo = new RepoRule().repo(RtLabelsITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    public static void tearDown() throws IOException {
        if (RtLabelsITCase.repos != null && RtLabelsITCase.repo != null) {
            RtLabelsITCase.repos.remove(RtLabelsITCase.repo.coordinates());
        }
    }

    @Test
    public void listsLabels() throws IOException {
        final Labels labels = RtLabelsITCase.repo.labels();
        final Iterable<Label.Smart> list =
            new Smarts<>(labels.iterate());
        for (final Label.Smart label : list) {
            MatcherAssert.assertThat(
                "Values are not equal",
                label.color(),
                Matchers.not(Matchers.is(Matchers.emptyString()))
            );
        }
    }

    @Test
    public void createsNewLabel() throws IOException {
        final Labels labels = RtLabelsITCase.repo.labels();
        final Label label = new Labels.Smart(labels).createOrGet("test-3");
        MatcherAssert.assertThat(
            "Value is null",
            new Label.Smart(label).color(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            "Collection is not empty",
            labels.iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
