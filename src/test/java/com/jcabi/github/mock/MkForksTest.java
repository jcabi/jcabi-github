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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkForks}.
 * @since 0.8
 */
public final class MkForksTest {

    @Test
    @Disabled
    public void createsFork() throws IOException {
        final MkForks forks = new MkForks(
            new MkStorage.InFile(),
            "Test", new Coordinates.Simple("tests", "forks")
        );
        MatcherAssert.assertThat(
            "Value is null",
            forks.create("blah"),
            Matchers.notNullValue()
        );
    }

    @Test
    public void iteratesForks() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Fork fork = repo.forks().create("Organization");
        final Iterable<Fork> iterate = repo.forks().iterate("Order");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            iterate,
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            "Collection does not contain expected item",
            iterate,
            Matchers.hasItem(fork)
        );
    }
}
