/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Label}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class LabelTest {

    /**
     * Label.Unmodified can be compared properly.
     */
    @Test
    public void canBeComparedProperly() {
        final Label.Unmodified one = new Label.Unmodified(
            LabelTest.repo("jef", "jef_repo"),
            "{\"name\":\"paul\"}"
        );
        final Label.Unmodified other = new Label.Unmodified(
            LabelTest.repo("stan", "stan_repo"),
            "{\"name\":\"paul\"}"
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            one.equals(other),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            "Assertion failed",
            one.compareTo(other),
            Matchers.not(0)
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
