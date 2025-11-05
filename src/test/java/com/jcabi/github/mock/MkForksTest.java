/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Fork;
import com.jcabi.github.Repo;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link MkForks}.
 *
 */
public final class MkForksTest {

    /**
     * RtForks should be able to create a new fork.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    @Ignore
    public void createsFork() throws Exception {
        final MkForks forks = new MkForks(
            new MkStorage.InFile(),
            "Test", new Coordinates.Simple("tests", "forks")
        );
        MatcherAssert.assertThat(
            forks.create("blah"),
            Matchers.notNullValue()
        );
    }

    /**
     * MkForks can list forks.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesForks() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Fork fork = repo.forks().create("Organization");
        final Iterable<Fork> iterate = repo.forks().iterate("Order");
        MatcherAssert.assertThat(
            iterate,
            Matchers.<Fork>iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            iterate,
            Matchers.hasItem(fork)
        );
    }
}
