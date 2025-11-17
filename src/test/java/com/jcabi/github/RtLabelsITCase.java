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
    @BeforeClass
    public static void setUp() throws IOException {
        final Github github = new GithubIT().connect();
        RtLabelsITCase.repos = github.repos();
        RtLabelsITCase.repo = new RepoRule().repo(RtLabelsITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterClass
    public static void tearDown() throws IOException {
        if (RtLabelsITCase.repos != null && RtLabelsITCase.repo != null) {
            RtLabelsITCase.repos.remove(RtLabelsITCase.repo.coordinates());
        }
    }

    /**
     * RtLabels can list all labels.
     */
    @Test
    public void listsLabels() throws IOException {
        final Labels labels = RtLabelsITCase.repo.labels();
        final Iterable<Label.Smart> list =
            new Smarts<>(labels.iterate());
        for (final Label.Smart label : list) {
            MatcherAssert.assertThat(
                label.color(),
                Matchers.not(Matchers.is(Matchers.emptyString()))
            );
        }
    }

    /**
     * RtLabels can create a new label.
     */
    @Test
    public void createsNewLabel() throws IOException {
        final Labels labels = RtLabelsITCase.repo.labels();
        final Label label = new Labels.Smart(labels).createOrGet("test-3");
        MatcherAssert.assertThat(
            new Label.Smart(label).color(),
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            labels.iterate(),
            Matchers.not(Matchers.emptyIterable())
        );
    }
}
