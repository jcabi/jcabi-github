/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Label}.
 * @since 0.1
 */
final class LabelTest {

    @Test
    void distinguishesLabelsOfDifferentRepos() {
        MatcherAssert.assertThat(
            "Labels of different repos are equal",
            LabelTest.label("jef").equals(LabelTest.label("stan")),
            Matchers.is(false)
        );
    }

    @Test
    void comparesLabelsOfDifferentRepos() {
        MatcherAssert.assertThat(
            "Labels of different repos are the same",
            LabelTest.label("jef").compareTo(LabelTest.label("stan")),
            Matchers.not(0)
        );
    }

    /**
     * Create and return label for testing.
     * @param user User name
     * @return Label
     */
    private static Label.Unmodified label(final String user) {
        return new Label.Unmodified(
            LabelTest.repo(user, String.format("%s_repo", user)),
            "{\"name\":\"paul\"}"
        );
    }

    /**
     * Create and return repo for testing.
     * @param user User name
     * @param rpo Repo name
     * @return Repo
     */
    private static Repo repo(final String user, final String rpo) {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple(user, rpo))
            .when(repo).coordinates();
        return repo;
    }
}
