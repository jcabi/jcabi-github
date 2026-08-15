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
 * Integration case for {@link Labels}.
 * @since 0.6
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtLabelsITCase {

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
        RtLabelsITCase.repos = github.repos();
        RtLabelsITCase.repo = new RepoRule().repo(RtLabelsITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtLabelsITCase.repos != null && RtLabelsITCase.repo != null) {
            RtLabelsITCase.repos.remove(RtLabelsITCase.repo.coordinates());
        }
    }

    @Test
    void listsLabels() throws IOException {
        final Iterable<Label.Smart> list =
            new Smarts<>(RtLabelsITCase.repo.labels().iterate());
        for (final Label.Smart label : list) {
            MatcherAssert.assertThat(
                "Values are not equal",
                label.color(),
                Matchers.not(Matchers.is(Matchers.emptyString()))
            );
        }
    }

    @Test
    void createsNewLabel() throws IOException {
        MatcherAssert.assertThat(
            "Created label has no color",
            new Label.Smart(
                new Labels.Smart(RtLabelsITCase.repo.labels())
                    .createOrGet("test-3")
            ).color(),
            Matchers.notNullValue()
        );
    }

    @Test
    void iteratesCreatedLabels() throws IOException {
        final Labels labels = RtLabelsITCase.repo.labels();
        new Labels.Smart(labels).createOrGet("test-4");
        MatcherAssert.assertThat(
            "Created label is not iterated",
            labels.iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
