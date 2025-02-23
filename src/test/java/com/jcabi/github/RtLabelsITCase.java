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
 * Integration case for {@link Labels}.
 * @since 0.6
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@OAuthScope(Scope.REPO)
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
     * RtLabels can list all labels.
     * @throws Exception If some problem inside
     */
    @Test
    public void listsLabels() throws Exception {
        final Labels labels = repo.labels();
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
     * @throws Exception If some problem inside
     */
    @Test
    public void createsNewLabel() throws Exception {
        final Labels labels = repo.labels();
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
