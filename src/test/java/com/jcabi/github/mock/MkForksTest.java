/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Fork;
import com.jcabi.github.Repo;
import java.io.IOException;
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
     */
    @Test
    @Ignore
    public void createsFork() throws IOException {
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
     */
    @Test
    public void iteratesForks() throws IOException {
        final Repo repo = new MkGithub().randomRepo();
        final Fork fork = repo.forks().create("Organization");
        final Iterable<Fork> iterate = repo.forks().iterate("Order");
        MatcherAssert.assertThat(
            iterate,
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            iterate,
            Matchers.hasItem(fork)
        );
    }
}
